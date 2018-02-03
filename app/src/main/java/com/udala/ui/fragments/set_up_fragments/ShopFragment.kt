package com.udala.ui.fragments.set_up_fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udala.*
import com.udala.adapters.ListAdapter
import com.udala.utils.BaseUtils
import kotlinx.android.synthetic.main.shop_fragment.*
import org.jetbrains.anko.toast
import org.json.JSONObject

/**
 * Created by apjoe on 12/4/2017.
 */
class ShopFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var pDialog : ProgressDialog
    private lateinit var loggedInUser : UserData
    private lateinit var existingShops : MutableList<Store>
    private lateinit var states : MutableList<State>
    private lateinit var cities : MutableList<City>
    private lateinit var LoBs : MutableList<LoB>
    private lateinit var attendants : MutableList<Attendant>
    private lateinit var dialog : AlertDialog
    private var selectedState  : State? = null
    private var selectedCity : City? = null
    private var selectLOB : LoB? = null

    private lateinit var firstName : EditText
    private lateinit var lastName : EditText
    private lateinit var email : EditText
    private lateinit var company : EditText
    private lateinit var password : EditText

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
        loggedInUser = BaseUtils(context).adminUserData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val customView = layoutInflater.inflate(R.layout.create_attendant, null, false)
        firstName = customView!!.findViewById<EditText>(R.id.firstName)
        lastName = customView.findViewById<EditText>(R.id.lastName)
        email = customView.findViewById<EditText>(R.id.email)
        company = customView.findViewById<EditText>(R.id.company)
        password = customView.findViewById<EditText>(R.id.password)

        setHasOptionsMenu(true)

        dialog = AlertDialog.Builder(mContext)
                .setTitle("Add attendant")
                .setView(customView)
                .setPositiveButton("CREATE", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()

                    if(!Patterns.EMAIL_ADDRESS.matcher(email.text).matches() or
                            firstName.text.toString().isEmpty() or
                            lastName.text.toString().isEmpty() or
                            company.text.toString().isEmpty() or
                            password.text.toString().isEmpty()){
                        mContext.toast("Please provide valid information")
                    }else{
                        pDialog = ProgressDialog.show(mContext, null, "Creating attendant...", false, false);

                        val createAttendantUrl = getString(R.string.base_url)+"attendants"
                        val params = mutableListOf<Pair<String, Any>>()

                        params.add(Pair("first_name", firstName.text.toString()))
                        params.add(Pair("last_name", lastName.text.toString()))
                        params.add(Pair("email", email.text.toString()))
                        params.add(Pair("company", company.text.toString()))
                        params.add(Pair("password", password.text.toString()))
                        params.add(Pair("area_id", "bbkjbj"))

                        createAttendantUrl.httpPost(params).header("Authorization" to "Bearer "+loggedInUser.token).responseString { request, response, result ->
                            pDialog.dismiss()

                            val(res, err) = result
                            if(err != null){
                                Log.d("RESPONSE", err.toString())
                                mContext.toast("Unable to create attendant. Please try again")
                                return@responseString
                            }

                            try {
                                val baseObject = JSONObject(res)
                                getAttendants(false)
                            }catch (e : Exception){
                                mContext.toast(e.localizedMessage)
                            }
                        }

                    }
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .create()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.add_shop, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_add -> {
                shopList.visibility = View.GONE
                emptyState.visibility = View.INVISIBLE
                shopForm.visibility = View.VISIBLE
                getAttendants(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.shop_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        getExistingShops();

        val addShop = view!!.findViewById<Button>(R.id.addShop)
        val addAttendantBtn = view.findViewById<Button>(R.id.addAttendantBtn)
        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)
        val registerBtn = view.findViewById<Button>(R.id.registerBtn)

        addAttendantBtn.setOnClickListener {
            dialog.show()
        }

        addShop.setOnClickListener {
            shopList.visibility = View.INVISIBLE
            emptyState.visibility = View.INVISIBLE
            shopForm.visibility = View.VISIBLE
            getAttendants(true)
        }

        registerBtn.setOnClickListener {
            pDialog = ProgressDialog.show(mContext, "Please wait", "Creating shop...", false, false);

            val createShopUrl = getString(R.string.base_url)+"stores"
            val params = mutableListOf<Pair<String, Any>>()

            params.add(Pair("attendant", loggedInUser.id))
            params.add(Pair("name", name.text.toString()))
            params.add(Pair("line_of_business_id", selectLOB!!.id))
            params.add(Pair("area_id", selectedCity!!.id))
            params.add(Pair("phone", phone_number.text.toString()))
            params.add(Pair("is_warehouse", "0"))

            createShopUrl.httpPost(params).header("Authorization" to "Bearer "+loggedInUser.token).timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->

                pDialog.dismiss()

                mContext.toast(result.toString())

                val (res, err) = result

                if(err != null){
                    mContext.toast("Unable to create shop. Please try again")
                    return@responseString
                }

                try {
                    val baseObject = JSONObject(res)
                    if(baseObject.getString("status") == "success"){
                        getExistingShops()
                    }

                }catch (e : Exception){
                    mContext.toast(e.localizedMessage)
                }

            }
        }
    }

    private fun getAttendants(proceedToNextStep : Boolean) {
        pDialog = ProgressDialog.show(mContext, null, "Getting attendants...", false, false);

        val attendantUrl = getString(R.string.base_url)+"attendants"

        attendantUrl.httpGet().header("Authorization" to "Bearer "+loggedInUser.token).timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->
            pDialog.dismiss()

            val (res, err) = result
            if(err != null){
                mContext.toast("Unable to get attendants. Please try again")
                return@responseString
            }

           try {
               val baseObject = JSONObject(res)
               val type = object : TypeToken<MutableList<Attendant>>(){}.type

               attendants = Gson().fromJson(baseObject.getJSONObject("data").getJSONArray("data").toString(), type)

               val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, attendants)
               attendant.adapter = arrayAdapter

               if(proceedToNextStep){
                   getLinesOfBusiness()
               }
           }catch (e:Exception){
               mContext.toast(e.localizedMessage)
           }

        }

    }

    private fun getLinesOfBusiness() {
        pDialog = ProgressDialog.show(mContext, null, "One moment please", false, false);

        val lobUrl = getString(R.string.base_url)+"line-of-business"

        lobUrl.httpGet().header("Authorization" to "Bearer "+loggedInUser.token).timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->
            pDialog.dismiss()

            val (res, err) = result
            if(err != null){
                mContext.toast("Unable to get line of businesses. Please try again")
                return@responseString
            }

           try {
               val baseObject = JSONObject(res)
               val type = object : TypeToken<MutableList<LoB>>(){}.type

               LoBs = Gson().fromJson(baseObject.getJSONArray("data").toString(), type)

               val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, LoBs)
               line_of_business.adapter = arrayAdapter
               line_of_business.onItemSelectedListener = lobListener

               getStates()
           }catch (e : Exception){
               mContext.toast(e.localizedMessage)
           }
        }

    }

    private fun getExistingShops() {
        pDialog = ProgressDialog.show(mContext, null, "Getting existing shops...", false, false);

        val shopsUrl = getString(R.string.base_url)+"stores/?is_warehouse=0"

        shopsUrl.httpGet().header("Authorization" to "Bearer "+loggedInUser.token).timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->

            pDialog.dismiss()

            val (res , err) = result

            if(err != null){
                mContext.toast("Unable to get existing shops. Please try again")
                return@responseString
            }

            try {
                val baseObject = JSONObject(res)
                val shopArray = baseObject.getJSONObject("data").optJSONArray("data")

                val type = object : TypeToken<MutableList<Store>>(){}.type
                existingShops = Gson().fromJson(shopArray.toString(), type)

                if(existingShops.size == 0){
                    shopList.visibility = View.INVISIBLE
                    emptyState.visibility = View.VISIBLE
                    shopForm.visibility = View.INVISIBLE

                }else{
                    shopList.visibility = View.VISIBLE
                    emptyState.visibility = View.INVISIBLE
                    shopForm.visibility = View.INVISIBLE

                    val listAdapter = ListAdapter(mContext, existingShops)
                    shopList.adapter = listAdapter
                }
            }catch (e:Exception){
                mContext.toast(e.localizedMessage)
            }
        }

    }

    private fun getStates() {
        pDialog = ProgressDialog.show(mContext, "A moment please", "Getting states...", false, false)
        val url = getString(R.string.base_url)+"countries/f1d633a0-0511-11e8-9576-45d16e2e132e/"
        url.httpGet().timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->

            pDialog.dismiss()

            val (res, error) = result

            if(error != null){
                mContext.toast("Unable to get states. Please try again")
                return@responseString
            }

            try {
                val baseObject = JSONObject(res)
                val statesArray = baseObject.getJSONObject("data").getJSONArray("data")
                val type = object : TypeToken<MutableList<State>>(){}.type
                states = Gson().fromJson(statesArray.toString(), type)

                val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, states)
                state_spinner.adapter = arrayAdapter

                state_spinner.onItemSelectedListener = listener
            }catch (e : Exception){
                mContext.toast(e.localizedMessage)
            }

        }
    }

    private fun getCities(selectedState: State) {
        pDialog = ProgressDialog.show(mContext, "A moment please", "Getting cities...", false, false)
        val url = getString(R.string.base_url)+"countries/f1d633a0-0511-11e8-9576-45d16e2e132e/"+selectedState.id
        url.httpGet().timeout(BaseUtils(mContext).TIMEOUT).responseString { request, response, result ->

            pDialog.dismiss()

            val (res, error) = result

            if(error != null){
                mContext.toast("Unable to get cities. Please try again")
                return@responseString
            }

           try {
               val baseObject = JSONObject(res)
               val statesArray = baseObject.getJSONObject("data").getJSONArray("data")
               val type = object : TypeToken<MutableList<City>>(){}.type
               cities = Gson().fromJson(statesArray.toString(), type)

               val arrayAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, cities)
               city_spinner.adapter = arrayAdapter

               city_spinner.onItemSelectedListener = cityListener
           }catch (e:Exception){
               mContext.toast(e.localizedMessage)
           }

        }
    }

    private var listener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            selectedState = states[p2]
            getCities(selectedState!!)
        }
    }

    private var cityListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            selectedCity = cities[p2]
        }

    }

    private var lobListener = object  : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            selectLOB = LoBs[p2]
        }

    }

}

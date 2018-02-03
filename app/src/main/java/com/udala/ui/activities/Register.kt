package com.udala.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udala.*
import com.udala.utils.BaseUtils
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class Register : AppCompatActivity() {

    lateinit var progressDialog : ProgressDialog
    lateinit var states : MutableList<State>
    lateinit var cities : MutableList<City>
    var selectedState  : State? = null
    var selectedCity : City? = null
    lateinit var user : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        clickEvents();

        getStates();
    }

    private fun getStates() {
        progressDialog = ProgressDialog.show(this@Register, "A moment please", "Getting states...", false, false)
        val url = getString(R.string.base_url)+"countries/f1d633a0-0511-11e8-9576-45d16e2e132e/"
        url.httpGet().timeout(BaseUtils(this@Register).TIMEOUT).responseString { request, response, result ->

            progressDialog.dismiss()

            val (res, error) = result

            if(error != null){
                toast("Unable to get states. Please try again")
                return@responseString
            }

            try {
                val baseObject = JSONObject(res)
                val statesArray = baseObject.getJSONObject("data").getJSONArray("data")
                val type = object : TypeToken<MutableList<State>>(){}.type
                states = Gson().fromJson(statesArray.toString(), type)

                val arrayAdapter = ArrayAdapter(this@Register, android.R.layout.simple_spinner_dropdown_item, states)
                state_spinner.adapter = arrayAdapter as SpinnerAdapter?

                state_spinner.onItemSelectedListener = listener
            }catch (e : Exception){
                toast(e.localizedMessage)
            }


        }
    }

    private fun getCities(selectedState: State) {
        progressDialog = ProgressDialog.show(this@Register, "A moment please", "Getting cities...", false, false)
        val url = getString(R.string.base_url)+"countries/f1d633a0-0511-11e8-9576-45d16e2e132e/"+selectedState.id
        url.httpGet().timeout(BaseUtils(this@Register).TIMEOUT).responseString { request, response, result ->

            progressDialog.dismiss()

            val (res, error) = result

            if(error != null){
                toast("Unable to get cities. Please try again")
                return@responseString
            }

            try {
                val baseObject = JSONObject(res)
                val statesArray = baseObject.getJSONObject("data").getJSONArray("data")
                val type = object : TypeToken<MutableList<City>>(){}.type
                cities = Gson().fromJson(statesArray.toString(), type)

                val arrayAdapter = ArrayAdapter(this@Register, android.R.layout.simple_spinner_dropdown_item, cities)
                city_spinner.adapter = arrayAdapter

                city_spinner.onItemSelectedListener = cityListener
            }catch (e: Exception){
                toast(e.localizedMessage)
            }


        }
    }

    private fun clickEvents() {
        registerBtn.setOnClickListener {

          if(validateFields()){

              progressDialog = ProgressDialog.show(this@Register, "A moment please", "Registering account...", false, false)


              val url = getString(R.string.base_url)+"auth/register"
              val params = mutableListOf<Pair<String, Any>>()
              params.add(Pair("first_name", first_name.text.toString()))
              params.add(Pair("last_name", last_name.text.toString()))
              params.add(Pair("email", email.text.toString()))
              params.add(Pair("company", company_name.text.toString()))
              params.add(Pair("password", password.text.toString()))
              params.add(Pair("area_id", selectedCity!!.id))
              params.add(Pair("phone", phone_number.text.toString()))

              url.httpPost(params).responseString { request, response, result ->

                  progressDialog.dismiss()

                  val (res, err) = result

                  val baseObject = JSONObject(res)
                  val userObject = baseObject.getJSONObject("data").getJSONObject("user")

                  val type = object  : TypeToken<UserData>(){}.type
                  user = Gson().fromJson(userObject.toString(), type)
                  user.token = baseObject.getJSONObject("data").getString("token")

                  BaseUtils(this@Register).adminUserData = user
                  BaseUtils(this@Register).loggedIn = true

                  success.visibility = View.VISIBLE
                  Handler().postDelayed({
                      val intent = Intent(self@this, DashBoard::class.java)
                      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                      startActivity(intent)
                  }, 2000)

              }

          }

        }
    }

    private fun validateFields(): Boolean {

        if(first_name.text.toString().trim().isEmpty()){
            first_name.error = "Please enter first name"
            return false
        }

        if(last_name.text.toString().trim().isEmpty()){
            last_name.error = "Please enter last name"
            return false
        }


        if(company_name.text.toString().trim().isEmpty()){
            company_name.error = "Please enter company name"
            return false
        }


        if(phone_number.text.toString().trim().isEmpty()){
            last_name.error = "Please enter phone number"
            return false
        }


        if(password.text.toString().trim().isEmpty()){
            password.error = "Please enter password"
            return false
        }

        if(selectedCity == null){
            toast("Please choose a city")
            return false
        }

//        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
//            email.error = "Please enter a valid email address"
//            return false
//        }

        return true
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
}

package com.udala

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udala.ui.activities.Register
import com.udala.utils.BaseUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.json.JSONObject

class Login : AppCompatActivity() {

    private lateinit var progressDialog : ProgressDialog
    private lateinit var user : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        clickEvents();
    }

    private fun clickEvents() {
        signUp.setOnClickListener {
            startActivity(Intent(self@this, Register::class.java))
        }

        signInBtn.setOnClickListener {
            if(validateFields()){

                progressDialog = ProgressDialog.show(this@Login, null, "Logging in...", false, false)


                val url = getString(R.string.base_url)+"auth/login"
                val params = mutableListOf<Pair<String, Any>>()

                params.add(Pair("identity", email.text.toString()))
                params.add(Pair("password", password.text.toString()))

                url.httpPost(params).timeout(BaseUtils(this@Login).TIMEOUT).responseString { request, response, result ->

                    progressDialog.dismiss()

                    val (res, err) = result

                    if(err != null){
                        try {
                            val errorBaseObject = JSONObject(String(err.errorData))
                            alert(errorBaseObject.getString("message"),"Log in error").show()
                        }catch (e : Exception){
                            toast("Unable to complete request. Please try again.")
                        }
                        return@responseString
                    }

                    try {
                        val baseObject = JSONObject(res)
                        val userObject = baseObject.getJSONObject("data").getJSONObject("user")

                        val type = object  : TypeToken<UserData>(){}.type
                        user = Gson().fromJson(userObject.toString(), type)
                        user.token = baseObject.getJSONObject("data").getString("token")

                        BaseUtils(this@Login).adminUserData = user
                        BaseUtils(this@Login).loggedIn = true

                        Handler().postDelayed({
                            val intent = Intent(self@this, DashBoard::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }, 2000)
                    }catch (e: Exception){
                        toast(e.localizedMessage)
                    }


                }

            }
        }
    }

    private fun validateFields(): Boolean {

//        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
//            email.error = "Please enter a valid email address"
//            return false
//        }

        if(password.text.toString().trim().isEmpty()){
            password.error = "Please enter password"
            return false
        }



        return true
    }
}

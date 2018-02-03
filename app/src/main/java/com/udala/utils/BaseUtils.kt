package com.udala.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.udala.UserData

/**
 * Created by apjoe on 12/2/2017.
 */
class BaseUtils (context : Context) {

    private val TAG = "com.udala"
    private val PREF_NAME = TAG+".prefs"
    private val LOGGED_IN = "logged_in"
    private val ADMIN_DATA = "admin_data"

    private val mContext = context

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0);

    public val TIMEOUT: Int = 60 * 1000


    var loggedIn: Boolean
        get() = prefs.getBoolean(LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(LOGGED_IN, value).apply()

    fun showDialog(title: String, message: Any?) {
        val dialog : AlertDialog = AlertDialog.Builder(mContext).create()
        dialog.setTitle(title)
        dialog.setMessage(message.toString())
        dialog.show()
    }

    var adminUserData : UserData
        get() {
            val savedUserString = prefs.getString(ADMIN_DATA,"")
            val gson = Gson()
            return gson.fromJson(savedUserString, UserData::class.java)
        }
        set(value) {
            val gson = Gson()
            prefs.edit().putString(ADMIN_DATA, gson.toJson(value)).apply()
        }


}
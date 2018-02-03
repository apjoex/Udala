package com.udala

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.udala.utils.BaseUtils

class Start : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if(BaseUtils(self@ this).loggedIn){
            startActivity(Intent(self@this, DashBoard::class.java))
        }else{
            startActivity(Intent(self@this, Login::class.java))
        }

        finish()
    }
}

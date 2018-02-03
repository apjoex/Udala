package com.udala

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.udala.ui.activities.Worker
import com.udala.utils.BaseUtils
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.search_bar.*

class DashBoard : AppCompatActivity() {

    lateinit var loggedInUser : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        loggedInUser = BaseUtils(this@DashBoard).adminUserData

        admin_name.text = loggedInUser.first_name

        clickEvents();
    }

    private fun clickEvents() {
        setup_box.setOnClickListener {
            openScreen("setup")
        }

        report_box.setOnClickListener {
            openScreen("report")
        }

        stock_box.setOnClickListener {
            openScreen("stock")
        }

        sales_box.setOnClickListener {
            openScreen("sales")
        }

        help_box.setOnClickListener {
            openScreen("help")
        }

        search_box.setOnClickListener {
            openScreen("search")
        }

        searchBar.setOnClickListener {
            openScreen("search")
        }

    }

    private fun openScreen(data: String) {
        val intent = Intent(self@this, Worker::class.java)
        intent.putExtra("destination", data)
        startActivity(intent)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.dashboard, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId){
//            R.id.action_search -> {
//                openScreen("search")
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

}

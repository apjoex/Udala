package com.udala.ui.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.udala.R
import com.udala.UserData
import com.udala.ui.fragments.*
import com.udala.utils.BaseUtils
import kotlinx.android.synthetic.main.activity_worker.*
import kotlinx.android.synthetic.main.app_bar_worker.*
import kotlinx.android.synthetic.main.search_bar.*

class Worker : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var destination : String? = ""
    lateinit var loggedInUser : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker)
        setSupportActionBar(toolbar)

        loggedInUser = BaseUtils(this@Worker).adminUserData

        admin_name.text = loggedInUser.first_name

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        destination = intent.getStringExtra("destination")

        when (destination){
            "setup" -> {
                showFragment(0)
            }
            "report" -> {
                showFragment(1)
            }
            "stock" -> {
                showFragment(2)
            }
            "sales" -> {
                showFragment(3)
            }
            "help" -> {
                showFragment(4)
            }
            "search" -> {
                showFragment(5)
            }
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun showFragment(position: Int) {
        nav_view.menu.getItem(position).isChecked = true
        onNavigationItemSelected(nav_view.menu.getItem(position))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_setup -> {
                // Handle the camera action
                supportActionBar?.title = "Set up"
                setCurrentFragment(SetUpFragment())
            }
            R.id.nav_report -> {
                supportActionBar?.title = "Report"
                setCurrentFragment(ReportFragment())
            }
            R.id.nav_stock -> {
                supportActionBar?.title = "Stock"
                setCurrentFragment(StockFragment())
            }
            R.id.nav_sales -> {
                supportActionBar?.title = "Sales"
                setCurrentFragment(SalesFragment())
            }
            R.id.nav_help -> {
                supportActionBar?.title = "Help"
                setCurrentFragment(HelpFragment())
            }
            R.id.nav_search -> {
                supportActionBar?.title = "Search"
                setCurrentFragment(SearchFragment())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setCurrentFragment(fragment: android.support.v4.app.Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .disallowAddToBackStack()
                .commit()
    }
}

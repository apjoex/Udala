package com.udala.ui.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.udala.R
import com.udala.adapters.PagerAdapter
import kotlinx.android.synthetic.main.setup_fragment.*

/**
 * Created by apjoe on 12/3/2017.
 */
class SetUpFragment : android.support.v4.app.Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.setup_fragment, container, false)
        val pager  = v!!.findViewById<ViewPager>(R.id.pager)
        val tabLayout  = v.findViewById<TabLayout>(R.id.tabLayout)
        val adapter = PagerAdapter(childFragmentManager)
        adapter.numberOfTabs = 2
        pager.adapter = adapter
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(listener)
        return v
    }

    val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            pager.currentItem = tab?.position ?: 0
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            pager.currentItem = tab?.position ?: 0
        }

    }


}
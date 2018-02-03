package com.udala.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.udala.ui.fragments.set_up_fragments.ShopFragment
import com.udala.ui.fragments.set_up_fragments.WarehouseFragment

/**
 * Created by apjoe on 12/4/2017.
 */
class PagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    var numberOfTabs = 0

    override fun getItem(position: Int): Fragment? {
        return when(position){
            0 -> {
                ShopFragment()
            }
            1 -> {
                WarehouseFragment()
            }
            else  -> {
                null
            }
        }

    }


    override fun getCount(): Int {
        return numberOfTabs
    }
}
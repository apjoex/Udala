package com.udala.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.udala.R
import com.udala.Store

/**
 * Created by apjoex on 22/01/2018.
 */
class ListAdapter(mContext : Context, shops : MutableList<Store>) : BaseAdapter() {

    lateinit var mContext : Context
    lateinit var shops : MutableList<Store>
    private val mInflator: LayoutInflater

    init {
        this.mContext = mContext
        this.shops = shops
        this.mInflator = LayoutInflater.from(mContext)

    }

    override fun getItem(p0: Int): Any {
        return 0
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return shops.size
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.shop_item, parent, false)
            vh = ListRowHolder(view)
            view!!.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.name.text = shops[position].name
        return view
    }

    private class ListRowHolder(row: View?) {
        public val name: TextView

        init {
            this.name = row?.findViewById(R.id.name) as TextView
        }
    }

}
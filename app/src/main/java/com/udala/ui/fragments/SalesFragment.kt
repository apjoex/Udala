package com.udala.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import com.udala.R

/**
 * Created by apjoe on 12/3/2017.
 */
class SalesFragment : Fragment() {

    lateinit var bank_details : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.sales_fragment, container, false)
        val payment_type = view!!.findViewById<RadioGroup>(R.id.payment_type)
        bank_details = view.findViewById<LinearLayout>(R.id.bank_details);
        payment_type.setOnCheckedChangeListener(listener)

        return view
    }

    val listener = RadioGroup.OnCheckedChangeListener { radioGroup, position ->
      //  Toast.makeText(activity, "Position is ${position}", Toast.LENGTH_SHORT).show()
        when(radioGroup.checkedRadioButtonId){
            R.id.bank_option -> {
                bank_details.visibility = View.VISIBLE
            }
            else -> {
                bank_details.visibility = View.GONE
            }
        }
    }
}


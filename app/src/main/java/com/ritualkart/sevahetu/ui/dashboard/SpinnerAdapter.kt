package com.ritualkart.sevahetu.ui.dashboard

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ritualkart.sevahetu.R

class SpinnerAdapter(context: Context, private val mLayoutResourceId: Int,
                     var list: List<String>) : ArrayAdapter<String>(context, mLayoutResourceId, list) {

    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): String {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.tv_address) as TextView
            cityAutoCompleteView.text = getItem(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}
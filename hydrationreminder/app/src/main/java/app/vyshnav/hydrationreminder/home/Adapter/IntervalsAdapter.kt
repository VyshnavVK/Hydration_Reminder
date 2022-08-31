package app.vyshnav.hydrationreminder.home.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import app.vyshnav.hydrationreminder.home.Model.intrevalsModel

class IntervalsAdapter (var context: Context, var list: List<intrevalsModel>) : BaseAdapter() {
    override fun getCount(): Int = list.size

    override fun getItem(p0: Int): Any = list[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item,p2,false)
        view.findViewById<TextView>(android.R.id.text1).text = list[p0].hoursText
        return view
    }
}
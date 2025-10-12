package com.example.wakadp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R
import com.example.wakadp1.data.DaySummary

class DayAdapter(private val days: List<DaySummary>) :
    RecyclerView.Adapter<DayAdapter.DayVH>() {

    inner class DayVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvActivities: TextView = view.findViewById(R.id.tvActivities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayVH(v)
    }

    override fun onBindViewHolder(holder: DayVH, position: Int) {
        val day = days[position]
        holder.tvDate.text = day.dateFormatted
        holder.tvActivities.text = "${day.count} activities"
    }

    override fun getItemCount(): Int = days.size
}

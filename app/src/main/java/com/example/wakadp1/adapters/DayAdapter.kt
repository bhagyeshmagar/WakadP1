package com.example.wakadp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R
import com.example.wakadp1.data.DaySummary



class DayAdapter(private val days: List<DaySummary>) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val recyclerEntries: RecyclerView = view.findViewById(R.id.recyclerEntries)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.tvDate.text = day.dateFormatted

        holder.recyclerEntries.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerEntries.adapter = ActivityAdapter(day.entries)
    }

    override fun getItemCount(): Int = days.size
}
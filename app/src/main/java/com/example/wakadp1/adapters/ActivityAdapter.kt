package com.example.wakadp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R
import com.example.wakadp1.data.ActivityEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityAdapter(private val activities: List<ActivityEntry>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]

        holder.tvType.text = activity.activityType

        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeRange = "${formatter.format(Date(activity.startTime))} - ${formatter.format(
            Date(
                activity.endTime
            )
        )}"
        holder.tvTime.text = timeRange

        holder.tvNotes.text = activity.notes ?: ""
    }

    override fun getItemCount(): Int = activities.size
}
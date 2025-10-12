package com.example.wakadp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.data.ActivityTypeCount
import com.example.wakadp1.R
<<<<<<< HEAD
=======

>>>>>>> 30baf9b (Initial commit)
class SummaryAdapter(private val onClick: (String) -> Unit) : RecyclerView.Adapter<SummaryAdapter.VH>() {

    private val items = ArrayList<ActivityTypeCount>()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvCount: TextView = view.findViewById(R.id.tvCount)

        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
<<<<<<< HEAD
                    onClick(items[pos].activityType)
=======
                    onClick(items[pos].type)   // ✅ changed from activityType → type
>>>>>>> 30baf9b (Initial commit)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_summary, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
<<<<<<< HEAD
        holder.tvType.text = item.activityType
=======
        holder.tvType.text = item.type              // ✅ changed from activityType → type
>>>>>>> 30baf9b (Initial commit)
        holder.tvCount.text = item.count.toString()
    }

    override fun getItemCount(): Int = items.size

    fun submitList(list: List<ActivityTypeCount>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}

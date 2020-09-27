package com.example.anlosia.ui.list.presence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anlosia.R
import com.example.anlosia.model.PresenceResponse
import kotlinx.android.synthetic.main.item_list_presence.view.*
import org.w3c.dom.Text

class ListPresenceAdapter(private val dataset: List<PresenceResponse>) :
    RecyclerView.Adapter<ListPresenceAdapter.ListPresenceViewHolder>() {
    class ListPresenceViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListPresenceAdapter.ListPresenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_presence, parent, false)

        return ListPresenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListPresenceViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.date_presence).text = dataset[position].date_presence
        holder.view.findViewById<TextView>(R.id.start_presence).text = dataset[position].start_presence
        holder.view.findViewById<TextView>(R.id.end_presence).text = dataset[position].end_presence
    }

    override fun getItemCount() = dataset.size

}
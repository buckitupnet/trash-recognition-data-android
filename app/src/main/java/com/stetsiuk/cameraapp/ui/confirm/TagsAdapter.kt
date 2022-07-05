package com.stetsiuk.cameraapp.ui.confirm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stetsiuk.cameraapp.R
import com.stetsiuk.cameraapp.app.TAGS

class TagsAdapter(private var tagClickListener: OnTagClickListener) :
    RecyclerView.Adapter<TagsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tag_list_item, parent, false)
        return TagsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagsViewHolder, position: Int) {
        holder.bind(TAGS[position], tagClickListener)
    }

    override fun getItemCount(): Int {
        return TAGS.size
    }
}

class TagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvTag: TextView = itemView.findViewById(R.id.textTag)

    fun bind(tag: String, tagClickListener: OnTagClickListener) {
        tvTag.text = tag

        itemView.setOnClickListener {
            tagClickListener.onTagClick(tag)
        }
    }
}

interface OnTagClickListener {
    fun onTagClick(data: String)
}
package com.jinwoo.memoapplication.View.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_layout.view.*

class MemoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var titleTextView: TextView
    var dateTextView: TextView
    init {
        titleTextView = itemView.itemmeomo_textview_title
        dateTextView = itemView.itemmeomo_textview_date
    }
}
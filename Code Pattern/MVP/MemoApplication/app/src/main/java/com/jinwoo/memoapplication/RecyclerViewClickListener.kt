package com.jinwoo.memoapplication

import com.jinwoo.memoapplication.Model.MemoModel

interface RecyclerViewClickListener{
    fun onItemClicked(position: Int, memo: MemoModel, key: String)
    fun onItemLongClicked(position: Int, memo: MemoModel, key: String)
}
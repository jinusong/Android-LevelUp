package com.jinwoo.memoapplication.Contract

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.jinwoo.memoapplication.Model.MemoModel
import com.jinwoo.memoapplication.View.adapter.MemoAdapter
import org.jetbrains.anko.AlertBuilder

interface MainContract{
    interface MainView{
        fun createAlert(key: String): AlertBuilder<android.app.AlertDialog>
        fun enterMemo(memo: MemoModel, key: String)
        fun listClickListener()
        fun createRecyclerView()
    }
    interface MainPresenter{
        val view: MainView
        val mAdapter: MemoAdapter
        val mDatabaseReference: DatabaseReference

        fun getAdapter(): MemoAdapter
    }
}
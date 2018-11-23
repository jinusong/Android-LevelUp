package com.jinwoo.memoapplication.Presenter

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jinwoo.memoapplication.Contract.MainContract
import com.jinwoo.memoapplication.View.adapter.MemoAdapter

class MainPresenter(view: MainContract.MainView) : MainContract.MainPresenter {

    override val view: MainContract.MainView

    override val mDatabaseReference: DatabaseReference
            by lazy { FirebaseDatabase.getInstance().getReference("notes") }

    override val mAdapter: MemoAdapter by lazy { MemoAdapter(mDatabaseReference) }

    init { this.view = view }

    override fun getAdapter(): MemoAdapter = mAdapter
}
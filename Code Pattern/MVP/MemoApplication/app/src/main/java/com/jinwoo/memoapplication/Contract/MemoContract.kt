package com.jinwoo.memoapplication.Contract

import android.content.Context
import android.content.Intent
import com.google.firebase.database.DatabaseReference
import com.jinwoo.memoapplication.Model.MemoModel

interface MemoContract{

    interface MemoView{
        fun notifyFinish()
        fun notifyError(error: String)
        fun setMemo(intent: Intent)
        fun notifyToast(text: String)
    }

    interface MemoPresenter{
        val view: MemoView
        var model: MemoModel
        val mDatabaseReference: DatabaseReference

        fun sendData(title: String, content: String)
        fun keyNullCheck(MemoKey: String?, context: Context)
        fun SaveMemo()
        fun UpdateMemo(MemoKey: String?)
    }

}
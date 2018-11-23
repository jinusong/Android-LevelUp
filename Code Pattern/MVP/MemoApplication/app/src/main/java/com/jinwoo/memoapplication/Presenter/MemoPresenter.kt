package com.jinwoo.memoapplication.Presenter

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jinwoo.memoapplication.Contract.MemoContract
import com.jinwoo.memoapplication.Model.MemoModel
import java.text.SimpleDateFormat
import java.util.*

class MemoPresenter(view: MemoContract.MemoView) : MemoContract.MemoPresenter{

    override val view: MemoContract.MemoView
    override lateinit var model: MemoModel
    override val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().getReference() }

    private lateinit var title: String
    private lateinit var content: String

    private val REQUIRED_TITLE: String = "제목을 입력하세요."
    private val REQUIRED_CONTENTS: String = "내용을 입력하세요."

    init {
        this.view = view
    }

    override fun sendData(title: String, content: String) {
        this.title = title
        this.content = title
        var dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
        model = MemoModel(title, content, dateFormat.format(Date()))
    }

    override fun keyNullCheck(MemoKey: String?, context: Context) =
        if(MemoKey == null) SaveMemo()
        else UpdateMemo(MemoKey)

    override fun SaveMemo() {
        if (TextUtils.isEmpty(title)) {
            view.notifyError(REQUIRED_TITLE)
            return
        }
        if (TextUtils.isEmpty(content)) {
            view.notifyError(REQUIRED_CONTENTS)
            return
        }
        mDatabaseReference.child("notes")
                .push().setValue(model).addOnSuccessListener {
                    view.notifyToast("저장 성공")
                }.addOnFailureListener {
                    view.notifyToast("저장 실패")
                }
        view.notifyFinish()
    }

    override fun UpdateMemo(MemoKey: String?) {
        var path = "notes/$MemoKey/"
        var childUpdates: Map<String, Any?> = model.toMap(path)
        mDatabaseReference.updateChildren(childUpdates)
        view.notifyFinish()
    }
}
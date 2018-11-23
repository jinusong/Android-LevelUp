package com.jinwoo.memoapplication.View

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.jinwoo.memoapplication.Contract.MemoContract
import com.jinwoo.memoapplication.Presenter.MemoPresenter
import com.jinwoo.memoapplication.R
import kotlinx.android.synthetic.main.activity_memo.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class MemoActivity:AppCompatActivity(), MemoContract.MemoView {

    val memoPresenter: MemoPresenter by lazy { MemoPresenter(this) }
    var mMemoKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent?.let{ setMemo(intent) }

        memo_btn_save.setOnClickListener {
            memoPresenter.sendData(memo_title.text.toString(), memo_content.text.toString())
            memoPresenter.keyNullCheck(mMemoKey, this)
        }
    }

    override fun notifyFinish() = finish()

    override fun notifyToast(text: String) = toast(text)

    override fun notifyError(error: String) = memo_title.setError(error)

    override fun setMemo(intent: Intent) {
        mMemoKey = intent.getStringExtra("memokey")
        var title: String? = intent.getStringExtra("title")
        var contents: String? = intent.getStringExtra("contents")
        memo_title.setText(title)
        memo_content.setText(contents)
    }
}
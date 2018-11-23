package com.jinwoo.memoapplication.View

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.jinwoo.memoapplication.Contract.MainContract
import com.jinwoo.memoapplication.Presenter.MainPresenter
import com.jinwoo.memoapplication.Model.MemoModel
import com.jinwoo.memoapplication.R
import com.jinwoo.memoapplication.RecyclerViewClickListener
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), MainContract.MainView {

    val mainPresenter: MainPresenter by lazy { MainPresenter(this)}
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createRecyclerView()
        listClickListener()
        main_fab_add.setOnClickListener { startActivity<MemoActivity>() }
    }

    override fun createAlert(key: String) =
            alert(title = "메모 삭제", message = "메모를 삭제하시겠습니까?") {
            positiveButton("삭제") {
                mainPresenter.mDatabaseReference.child(key).removeValue()
            }
            negativeButton("취소") { null }
        }

    override fun enterMemo(memo: MemoModel, key: String) =
        startActivity<MemoActivity>("memokey" to key,
                "title" to memo.title, "contents" to memo.contents)

    override fun listClickListener() =
        mainPresenter.mAdapter.setOnClickListener(object: RecyclerViewClickListener {
            override fun onItemClicked(position: Int, memo: MemoModel, key: String) {
                mainPresenter.view.enterMemo(memo, key)
            }
            override fun onItemLongClicked(position: Int, memo: MemoModel, key: String) {
                mainPresenter.view.createAlert(key).show()
            }
        })

    override fun createRecyclerView(){
        recyclerView = findViewById(R.id.recycler_list)
        var decoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mainPresenter.getAdapter()
    }
}

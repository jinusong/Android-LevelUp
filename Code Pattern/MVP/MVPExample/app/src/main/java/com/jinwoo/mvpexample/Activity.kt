package com.jinwoo.mvpexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class Activity : AppCompatActivity(), Contract.View {
    val presenter by lazy { Presenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        plus.setOnClickListener {
            presenter.calc(getFirstNum(), getSecondNum(), '+')
        }

        minus.setOnClickListener {
            presenter.calc(getFirstNum(), getSecondNum(), '-')
        }

        divide.setOnClickListener {
            presenter.calc(getFirstNum(), getSecondNum(), '/')
        }

        multiple.setOnClickListener {
            presenter.calc(getFirstNum(), getSecondNum(), '*')
        }
    }

    override fun setCalcResult(res: Int) { result.text = res.toString() }

    override fun getFirstNum(): Int = firstNum.text.toString().toInt()

    override fun getSecondNum(): Int = secondNum.text.toString().toInt()
}

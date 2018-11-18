package com.jinwoo.mvpexample

class Presenter(val view: Contract.View) : Contract.Presenter {
    override fun calc(x: Int, y: Int, type: Char) {
        val res = when (type) {
            '+' -> x + y
            '-' -> x - y
            '*' -> x * y
            '/' -> x / y
            else -> 0
        }
        view.setCalcResult(res)
    }
}
package com.jinwoo.mvpexample

interface Contract {
    interface View {
        fun setCalcResult(res: Int)
        fun getFirstNum(): Int
        fun getSecondNum(): Int
    }

    interface Presenter {
        fun calc(x: Int, y: Int, type: Char)
    }
}
package com.jinwoo.asd

import android.databinding.ObservableField
import android.view.View

class ViewModel(val navigator: Navigator) {
    var result = 0
    var num1 = ObservableField<String>("")
    var num2 = ObservableField<String>("")

    fun cal(ad : Char){
        try{
            when(ad) {
                '+' -> result = num1.toInt() + num2.toInt()
                '-' -> result = num1.toInt() - num2.toInt()
                '/' -> result = num1.toInt() / num2.toInt()
                '*' -> result = num1.toInt() * num2.toInt()
            }
            navigator.showToast(result.toString())
        } catch (e: NumberFormatException){
            navigator.showToast("값을 입력해주세요")
        }
    }

    fun ObservableField<String>.toInt() = this.get()!!.toInt()
}
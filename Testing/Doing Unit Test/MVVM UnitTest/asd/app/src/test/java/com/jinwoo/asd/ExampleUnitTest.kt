package com.jinwoo.asd

import android.databinding.Observable
import android.databinding.ObservableField
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    lateinit var viewModel: ViewModel
    lateinit var navigator: Navigator
    @Before
    fun VM(){
        navigator = mock(Navigator::class.java)
        viewModel = ViewModel(navigator)
        viewModel.num1 = ObservableField("10")
        viewModel.num2 = ObservableField("30")
    }

    @Test
    fun add() {
        viewModel.cal('+')
        assertEquals(viewModel.result, 40)
    }

    @Test
    fun minus(){
        viewModel.cal('-')
        assertEquals(viewModel.result, -20)
    }

    @Test
    fun multiple(){
        viewModel.cal('*')
        assertEquals(viewModel.result, 300)
    }

    @Test
    fun division(){
        viewModel.cal('/')
        assertEquals(viewModel.result, 0)
    }

    @Test
    fun nonInsert(){
        viewModel.num1 = ObservableField("")
        viewModel.cal('+')
        verify(navigator).showToast("값을 입력해주세요")
    }
}

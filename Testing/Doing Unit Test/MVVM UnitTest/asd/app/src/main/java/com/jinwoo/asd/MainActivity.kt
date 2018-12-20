package com.jinwoo.asd

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.jinwoo.asd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        var viewModel = ViewModel(this)
        binding.vm = viewModel
    }

    override fun showToast(result: String) = Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
}

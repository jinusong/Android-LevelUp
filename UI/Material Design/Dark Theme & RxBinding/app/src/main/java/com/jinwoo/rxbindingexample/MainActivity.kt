package com.jinwoo.rxbindingexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var isAnimating = false

        lottie.setAnimation("lottie.json")
        lottie.loop(true)

        disposable.add(button.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isAnimating = !isAnimating
                if (isAnimating) {
                    lottie.playAnimation()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else {
                    lottie.pauseAnimation()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            })
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }
}

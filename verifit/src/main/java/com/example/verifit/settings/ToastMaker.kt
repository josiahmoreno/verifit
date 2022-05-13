package com.example.verifit.settings

import android.content.Context
import android.widget.Toast

class ToastMakerImpl(val applicationContext: Context) : ToastMaker {
    override fun makeText(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }

}

class NoOpToastMaker: ToastMaker
interface ToastMaker {
    fun makeText(s: String) {

    }

}

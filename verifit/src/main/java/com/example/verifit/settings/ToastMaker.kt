package com.example.verifit.settings

import android.content.Context
import android.widget.Toast

class ToastMaker(val applicationContext: Context) {
    fun makeText(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }

}

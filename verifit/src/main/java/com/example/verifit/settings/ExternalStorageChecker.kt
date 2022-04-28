package com.example.verifit.settings

import android.content.Context
import android.os.Environment

class ExternalStorageChecker(applicationContext: Context) {
    operator fun invoke(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

}

package com.docwei.globaldot

import android.app.Application
import android.os.Build
import android.util.Log
import com.docwei.doblib.ZeTaTrack

/**
 * Created by liwk on 2021/6/12.
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("ZeTadot","onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.e("ZeTadot", "onCreate-->${getProcessName()}")
        }
        ZeTaTrack.init(this)
    }
}
package com.docwei.doblib

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

/**
 * Created by liwk on 2021/6/12.
 */
object ZeTaTrack : Application.ActivityLifecycleCallbacks {
    var frontBackgroundHelper:ZeTaFrontBackgroundHelper?=null
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
        frontBackgroundHelper = ZeTaFrontBackgroundHelper(application)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        val count = frontBackgroundHelper?.getAppCount()
        val currentCount = count?.plus(1) ?: 1
        frontBackgroundHelper?.commitAppCount(currentCount)
        if (currentCount == 1) { //应用从后台切换到前台
            Log.e("ZeTadot", "前台---》${activity::class.java.canonicalName}")
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        val count = frontBackgroundHelper?.getAppCount()
        val currentCount = count?.minus(1)?:0
        frontBackgroundHelper?.commitAppCount(currentCount)
        if (currentCount == 0 && !activity.isFinishing) { //应用从前台切换到后台
            Log.e("ZeTadot", "后台的---》${activity::class.java.canonicalName}")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}
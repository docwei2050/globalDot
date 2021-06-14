package com.docwei.globaldot

import android.util.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * Created by liwk on 2021/6/14.
 */
@Aspect
class MyAopTest {
    //写法一 ----------------
    @Around("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    fun getExecuteTime(proceeding: ProceedingJoinPoint) {
        val startTime = System.currentTimeMillis();
        proceeding.proceed()
        val diff = System.currentTimeMillis() - startTime
        Log.e("ZeTaDot", "耗时---》$diff")
    }

    //写法二 ----------------
    @Pointcut("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    fun getTime(){
    }
    @Around("getTime()")
    fun getExecuteTime2(proceeding: ProceedingJoinPoint) {
        val startTime = System.currentTimeMillis();
        proceeding.proceed()
        val diff = System.currentTimeMillis() - startTime
        Log.e("ZeTaDot", "耗时---》$diff")
    }
}
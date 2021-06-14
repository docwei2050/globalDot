package com.docwei.globaldot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val tv=findViewById<TextView>(R.id.tv)
       Log.e("ZeTaDot","${ tv.isClickable}")
        tv.setOnClickListener {
            Thread.sleep(4000)
            val intent= Intent(this@MainActivity,FirstActivity::class.java)
            startActivity(intent)
        }
        Log.e("ZeTaDot","${ tv.isClickable}")

    }
}
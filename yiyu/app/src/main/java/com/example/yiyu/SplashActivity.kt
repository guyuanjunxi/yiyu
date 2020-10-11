package com.example.yiyu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.util.*
import androidx.core.content.ContextCompat.getSystemService
import android.R.string.no
import android.R.attr.name
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


            val intent = Intent(this, Login2Activity::class.java)
            startActivity(intent)

    }
}

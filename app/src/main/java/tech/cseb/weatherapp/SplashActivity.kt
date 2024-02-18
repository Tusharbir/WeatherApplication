package tech.cseb.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Handler(mainLooper).postDelayed({
                startActivity(Intent(this, tech.cseb.weatherapp.MainActivity::class.java))
                finish()
        }, 2000)
        setContentView(R.layout.activity_splash)
    }

}
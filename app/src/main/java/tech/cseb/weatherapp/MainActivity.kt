package tech.cseb.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.cseb.weatherapp.databinding.ActivityMainBinding
import tech.cseb.weatherapp.fragments.RootBlankFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)



        supportFragmentManager.beginTransaction().replace(R.id.container, RootBlankFragment()).commit()

        setContentView(binding.root)
    }
}
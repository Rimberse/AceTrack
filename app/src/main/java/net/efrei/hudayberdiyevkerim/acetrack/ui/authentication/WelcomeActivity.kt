package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityWelcomeBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class WelcomeActivity: AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            Log.i("Top app bar navigation button","clicked")
        }

        binding.loginButton.setOnClickListener {
            Log.i("Login button","clicked")
        }

        binding.registerButton.setOnClickListener {
            Log.i("Register button","clicked")
        }

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()
    }
}

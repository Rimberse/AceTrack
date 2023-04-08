package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityWelcomeBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class WelcomeActivity: AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityWelcomeBinding

    fun displayRules(item: MenuItem) {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bar_top_app, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
    }
}

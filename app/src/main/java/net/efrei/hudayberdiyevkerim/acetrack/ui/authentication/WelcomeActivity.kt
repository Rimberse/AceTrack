package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityWelcomeBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.ui.home.HomeActivity
import net.efrei.hudayberdiyevkerim.acetrack.ui.rules.RulesActivity

class WelcomeActivity: AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var authenticationViewModel : AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()

        authenticationViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        authenticationViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null)
                startActivity(Intent(this, HomeActivity::class.java))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_top, menu)
        return true
    }

    fun displayRules(item: MenuItem) {
        val intent = Intent(this, RulesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

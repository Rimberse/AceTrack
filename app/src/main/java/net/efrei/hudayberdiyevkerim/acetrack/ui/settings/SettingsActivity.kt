package net.efrei.hudayberdiyevkerim.acetrack.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRulesBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.WelcomeActivity

class SettingsActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()
    }
}

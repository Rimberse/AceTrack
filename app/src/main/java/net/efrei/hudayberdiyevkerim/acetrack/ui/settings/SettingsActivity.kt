package net.efrei.hudayberdiyevkerim.acetrack.ui.settings

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivitySettingsBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.LocaleService
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.WelcomeActivity


class SettingsActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.englishButton.setOnClickListener(OnClickListener {
            LocaleService.setLocale(this, "en")
            recreate()
            startActivity(Intent(this, WelcomeActivity::class.java))
        })

        binding.frenchButton.setOnClickListener(OnClickListener {
            LocaleService.setLocale(this, "fr")
            recreate()
            startActivity(Intent(this, WelcomeActivity::class.java))
        })

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()
    }
}

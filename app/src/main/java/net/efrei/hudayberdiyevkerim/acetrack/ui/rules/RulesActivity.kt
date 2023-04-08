package net.efrei.hudayberdiyevkerim.acetrack.ui.rules

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRulesBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class RulesActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()
    }
}
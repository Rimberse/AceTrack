package net.efrei.hudayberdiyevkerim.acetrack.ui.rules

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class RulesActivity : AppCompatActivity() {
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as MainApp
    }

    override fun onStart() {
        super.onStart()
    }
}
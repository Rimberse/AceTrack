package net.efrei.hudayberdiyevkerim.acetrack.main

import android.app.Application
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerJSONStore
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerStore
import timber.log.Timber

class MainApp : Application() {
    lateinit var players: PlayerStore
    override fun onCreate() {
        super.onCreate()
        players = PlayerJSONStore(applicationContext)
        Timber.plant(Timber.DebugTree())
        Timber.i("Ace Track app started")
    }
}

package net.efrei.hudayberdiyevkerim.acetrack.main

import android.app.Application
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerJSONStore
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerStore
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultJSONStore
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultStore
import timber.log.Timber

class MainApp : Application() {
    lateinit var results: ResultStore
    lateinit var players: PlayerStore
    override fun onCreate() {
        super.onCreate()
        results = ResultJSONStore(applicationContext)
        players = PlayerJSONStore(applicationContext)
        Timber.plant(Timber.DebugTree())
        Timber.i("Ace Track app started")
    }
}

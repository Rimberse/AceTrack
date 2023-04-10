package net.efrei.hudayberdiyevkerim.acetrack.main

import android.app.Application
import net.efrei.hudayberdiyevkerim.acetrack.models.UserJSONStore
import net.efrei.hudayberdiyevkerim.acetrack.models.UserModel
import net.efrei.hudayberdiyevkerim.acetrack.models.UserStore
import timber.log.Timber

class MainApp : Application() {
    lateinit var users: UserStore
    override fun onCreate() {
        super.onCreate()
        users = UserJSONStore(applicationContext)
        Timber.plant(Timber.DebugTree())
        Timber.i("Ace Track app started")
    }
}

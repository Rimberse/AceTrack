package net.efrei.hudayberdiyevkerim.acetrack.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.LoggedInViewModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.WelcomeActivity

class Home : AppCompatActivity() {
    private lateinit var loggedInViewModel : LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public override fun onStart() {
        super.onStart()

        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)

        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->

        })

        loggedInViewModel.loggedOut.observe(this, Observer { loggedOut ->
            if (loggedOut) {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
        })
    }
}

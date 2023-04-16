package net.efrei.hudayberdiyevkerim.acetrack.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityHomeBinding
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.LoggedInViewModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.WelcomeActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : ActivityHomeBinding
    private lateinit var loggedInViewModel : LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView = homeBinding.navView
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

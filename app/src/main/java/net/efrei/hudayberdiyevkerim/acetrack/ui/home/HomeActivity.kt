package net.efrei.hudayberdiyevkerim.acetrack.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityHomeBinding
import net.efrei.hudayberdiyevkerim.acetrack.databinding.NavigationHeaderBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.customTransformation
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.LoggedInViewModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.WelcomeActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : ActivityHomeBinding
    private lateinit var navHeaderBinding : NavigationHeaderBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loggedInViewModel : LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.resultsFragment, R.id.playersFragment, R.id.contactFragment, R.id.logOut), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = homeBinding.navView
        navView.setupWithNavController(navController)
    }

    public override fun onStart() {
        super.onStart()

        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]

        loggedInViewModel.liveFirebaseUser.observe(this, Observer {
            updateNavHeader(loggedInViewModel.liveFirebaseUser.value!!)
        })

        loggedInViewModel.loggedOut.observe(this, Observer { loggedOut ->
            if (loggedOut) {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
        })
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        var headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavigationHeaderBinding.bind(headerView)
        navHeaderBinding.navHeaderEmail.text = currentUser.email

        if(currentUser.photoUrl != null && currentUser.displayName != null) {
            navHeaderBinding.navHeaderName.text = currentUser.displayName

            Picasso.get().load(currentUser.photoUrl)
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(navHeaderBinding.navHeaderImage)
        }
    }

    fun logOut(item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

package net.efrei.hudayberdiyevkerim.acetrack.ui.splash_screen

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import net.efrei.hudayberdiyevkerim.acetrack.R

// Implemented with the help of following article
// https://www.geeksforgeeks.org/how-to-create-a-splash-screen-in-android-using-kotlin/

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}

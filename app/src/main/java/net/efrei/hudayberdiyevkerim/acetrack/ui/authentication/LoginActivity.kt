package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityLoginBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.ui.home.Home
import timber.log.Timber

class LoginActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var authenticationViewModel : AuthenticationViewModel
    private lateinit var binding : ActivityLoginBinding
    lateinit var app: MainApp
    private lateinit var authentication: FirebaseAuth
    private lateinit var togglePasswordVisibilityButton: ImageButton
    private lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.loginButton.setOnClickListener(this)
        binding.togglePasswordVisibilityButton.setOnClickListener(this)

        togglePasswordVisibilityButton = findViewById(R.id.togglePasswordVisibilityButton)
        togglePasswordVisibilityButton.setImageResource(R.drawable.ic_eye)

        app = application as MainApp

        authentication = FirebaseAuth.getInstance()
    }

    private fun logIn(email: String, password: String) {
        Timber.d( "Sign In: $email")

        authenticationViewModel.login(email, password)
    }

    public override fun onStart() {
        super.onStart()

        authenticationViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        authenticationViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null)
                startActivity(Intent(this, Home::class.java))
        })

        authenticationViewModel.firebaseAuthenticationManager.errorStatus.observe(this, Observer { status -> checkStatus(status) })
    }

    private fun checkStatus(error:Boolean) {
        if (error)
            Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cancel, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }
}

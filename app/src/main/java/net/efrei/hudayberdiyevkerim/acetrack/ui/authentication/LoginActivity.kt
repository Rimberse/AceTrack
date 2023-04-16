package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityLoginBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.ui.home.HomeActivity
import timber.log.Timber


class LoginActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var authenticationViewModel : AuthenticationViewModel
    private lateinit var binding : ActivityLoginBinding
    lateinit var app: MainApp
    private lateinit var authentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        binding.loginButton.setOnClickListener(this)

        // Clears error messages on input
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.emailLayout.error = null;
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.passwordLayout.error = null;
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        authentication = FirebaseAuth.getInstance()
    }

    private fun logIn(email: String, password: String) {
        Timber.d( "Sign In: $email")

        if (!validateInput()) {
            return
        }

        authenticationViewModel.login(email, password)
    }

    public override fun onStart() {
        super.onStart()

        authenticationViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        authenticationViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null)
                startActivity(Intent(this, HomeActivity::class.java))
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

    private fun validateInput(): Boolean {
        var isValid = true
        val email = binding.email.text.toString()

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.error = "Email is empty"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        val password = binding.password.text.toString()

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.error = "Password is empty"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        return isValid
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.loginButton -> logIn(binding.email.text.toString(), binding.password.text.toString())
        }
    }
}

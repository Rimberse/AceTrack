package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRegisterBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.displayImagePicker
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.UserModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.home.Home
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class RegisterActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private var user = UserModel()
    private lateinit var app: MainApp
    private var isEditingExistingUser = false
    private lateinit var authentication: FirebaseAuth
    private var calendar: Calendar = Calendar.getInstance()
    private var userDateOfBirth: TextView? = null
    private var buttonSelectDate: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        registerImagePickerCallback()
        app = application as MainApp

        val selectExperienceString = getString(R.string.select_experience)
        val experienceOptions = listOf(selectExperienceString, "Beginner", "Intermediate", "Experienced")

        val experienceSpinner = findViewById<Spinner>(R.id.experienceSpinner)

        if (experienceSpinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, experienceOptions)
            experienceSpinner.adapter = adapter
        }

        experienceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                user.experience = experienceOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        userDateOfBirth = binding.resultDate
        buttonSelectDate = binding.selectDateButton

        userDateOfBirth!!.text = getString(R.string.date_of_birth_placeholder)

        // Date picker implemented with reference to https://www.tutorialkart.com/kotlin-android/android-datepicker-kotlin-example/
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        buttonSelectDate!!.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.datePickerStyle,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        if (intent.hasExtra("user_edit")) {
            isEditingExistingUser = true
            user = intent.extras?.getParcelable("user_edit")!!
            binding.firstName.setText(user.firstName)
            binding.lastName.setText(user.lastName)
            binding.email.setText(user.email)
            binding.password.setText(user.password)
            userDateOfBirth!!.text = user.dateOfBirth.toString()
            binding.experienceSpinner.setSelection(experienceOptions.indexOf(user.experience))
            binding.registerButton.setText(R.string.update_member)

            Picasso.get()
                .load(user.image)
                .into(binding.memberImage)

            if (user.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_member_image)
            }
        }

        binding.registerButton.setOnClickListener(this)
        binding.chooseImage.setOnClickListener(this)

        // Clears error messages on input
        binding.firstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.firstNameLayout.error = null;
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding.lastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.lastNameLayout.error = null;
            }

            override fun afterTextChanged(s: Editable?) { }
        })

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

    private fun updateDateInView() {
        userDateOfBirth!!.error = null
        val format = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        userDateOfBirth!!.text = simpleDateFormat.format(calendar.time)
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

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Image loaded ${result.data!!.data}")
                            user.image = result.data!!.data!!

                            Picasso.get()
                                .load(user.image)
                                .into(binding.memberImage)

                            binding.chooseImage.setText(R.string.change_member_image)
                        }
                    }

                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun createAccount(email: String, password: String) {
        Timber.d( "Create account: $email")

        if (!validateForm()) {
            return
        }

        user.firstName = binding.firstName.text.toString()
        user.lastName = binding.lastName.text.toString()
        user.email = binding.email.text.toString()
        user.password = binding.password.text.toString()
        user.dateOfBirth = LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).toLocalDate()

        if (isEditingExistingUser) {
            authentication.currentUser!!.updateEmail(user.email)
            authentication.currentUser!!.updatePassword(user.password)
            app.users.update(user.copy())

            Toast.makeText(baseContext, "User details updated", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Home::class.java))
            setResult(RESULT_OK)
        } else {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Timber.d( "Create user with email: success")
                        user.uuid = authentication.currentUser!!.uid
                        app.users.create(user.copy())

                        Toast.makeText(baseContext, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))
                        setResult(RESULT_OK)
                    } else {
                        Timber.w( "Create user with email: failure - ${task.exception}")
                        Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                        Toast.makeText(baseContext, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val firstName = binding.firstName.text.toString()
        if (TextUtils.isEmpty(firstName)) {
            binding.firstNameLayout.error = "Required"
            isValid = false
        } else {
            binding.firstNameLayout.error = null
        }

        val lastName = binding.lastName.text.toString()
        if (TextUtils.isEmpty(lastName)) {
            binding.lastNameLayout.error = "Required"
            isValid = false
        } else {
            binding.lastNameLayout.error = null
        }

        val email = binding.email.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.error = "Required"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        val password = binding.password.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.error = "Required"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        val dateOfBirth = userDateOfBirth!!.text.toString()
        if (dateOfBirth === getString(R.string.date_of_birth_placeholder)) {
            userDateOfBirth!!.error = "Required"
            isValid = false
        } else {
            userDateOfBirth!!.error = null
        }

        if (user.experience === getString(R.string.select_experience)) {
            val errorText = binding.experienceSpinner.selectedView as TextView
            errorText.error = "Required"
            errorText.requestFocus()
            isValid = false
        }

        return isValid
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.registerButton -> createAccount(binding.email.text.toString(), binding.password.text.toString())
            R.id.chooseImage -> displayImagePicker(imageIntentLauncher)
        }
    }
}

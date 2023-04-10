package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class RegisterActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private var user = UserModel()
    private lateinit var app: MainApp
    private var edit = false
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

        userDateOfBirth = binding.resultDate
        buttonSelectDate = binding.selectDateButton

        userDateOfBirth!!.text = "DD/MM/YYYY"

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

        if (intent.hasExtra("member_edit")) {
            edit = true
            user = intent.extras?.getParcelable("member_edit")!!
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

        experienceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                user.experience = experienceOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.registerButton.setOnClickListener(this)
        binding.chooseImage.setOnClickListener(this)

        authentication = FirebaseAuth.getInstance()
    }

    private fun updateDateInView() {
        val format = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        userDateOfBirth!!.text = simpleDateFormat.format(calendar.time)
        Log.i("LocalDate", LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).toLocalDate().toString());
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.chooseImage -> displayImagePicker(imageIntentLauncher)
        }
    }
}

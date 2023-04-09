package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRegisterBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRegisterBinding
    lateinit var app: MainApp
    private lateinit var authentication: FirebaseAuth
    var calendar: Calendar = Calendar.getInstance()
    private var userDateOfBirth: TextView? = null
    private var buttonSelectDate: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

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

        experienceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Clicked on experience option", experienceOptions[position])
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

    }
}

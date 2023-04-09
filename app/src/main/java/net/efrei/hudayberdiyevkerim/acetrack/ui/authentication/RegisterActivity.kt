package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRegisterBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class RegisterActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRegisterBinding
    lateinit var app: MainApp
    private lateinit var authentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        val selectExperienceString = getString(R.string.select_experience)
        val experienceOptions = listOf(selectExperienceString, "Beginner", "Intermediate", "Experienced")

        val experienceSpinner = findViewById<Spinner>(R.id.experienceSpinner)

        if (experienceSpinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, experienceOptions)
            experienceSpinner.adapter = adapter
        }

        app = application as MainApp

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

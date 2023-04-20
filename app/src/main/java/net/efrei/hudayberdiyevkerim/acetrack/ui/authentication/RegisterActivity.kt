package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.ActivityRegisterBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.displayImagePicker
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.home.HomeActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.ContentValues
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import java.util.Locale

typealias LumaListener = (luma: Double) -> Unit

class RegisterActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private var player = PlayerModel()
    private lateinit var app: MainApp
    private var isEditingExistingPlayer = false
    private lateinit var authentication: FirebaseAuth
    private var calendar: Calendar = Calendar.getInstance()
    private var playerDateOfBirth: TextView? = null
    private var buttonSelectDate: Button? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

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
                player.experience = experienceOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        playerDateOfBirth = binding.resultDate
        buttonSelectDate = binding.selectDateButton

        playerDateOfBirth!!.text = getString(R.string.date_placeholder)

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

        if (intent.hasExtra("player_edit")) {
            isEditingExistingPlayer = true
            player = intent.extras?.getParcelable("player_edit")!!
            binding.firstName.setText(player.firstName)
            binding.lastName.setText(player.lastName)
            binding.email.setText(player.email)
            binding.password.setText(player.password)
            playerDateOfBirth!!.text = player.dateOfBirth.toString()
            binding.experienceSpinner.setSelection(experienceOptions.indexOf(player.experience))
            binding.registerButton.setText(R.string.update_player)

            Picasso.get()
                .load(player.image)
                .into(binding.playerImage)

            if (player.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_player_image)
            }
        }

        binding.registerButton.setOnClickListener(this)
        binding.chooseImage.setOnClickListener(this)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()

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
        playerDateOfBirth!!.error = null
        val format = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        playerDateOfBirth!!.text = simpleDateFormat.format(calendar.time)
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
                            player.image = result.data!!.data!!

                            Picasso.get()
                                .load(player.image)
                                .into(binding.playerImage)

                            binding.chooseImage.setText(R.string.change_player_image)
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

        player.firstName = binding.firstName.text.toString()
        player.lastName = binding.lastName.text.toString()
        player.email = binding.email.text.toString()
        player.password = binding.password.text.toString()
        player.dateOfBirth = LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).toLocalDate()

        if (isEditingExistingPlayer) {
            authentication.currentUser!!.updateEmail(player.email)
            authentication.currentUser!!.updatePassword(player.password)
            app.players.update(player.copy())

            Toast.makeText(baseContext, "Player details updated", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            setResult(RESULT_OK)
        } else {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Timber.d( "Create player with email: success")
                        player.uuid = authentication.currentUser!!.uid
                        app.players.create(player.copy())

                        Toast.makeText(baseContext, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        setResult(RESULT_OK)
                    } else {
                        Timber.w( "Create player with email: failure - ${task.exception}")
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

        val dateOfBirth = playerDateOfBirth!!.text.toString()
        if (dateOfBirth === getString(R.string.date_placeholder)) {
            playerDateOfBirth!!.error = "Required"
            isValid = false
        } else {
            playerDateOfBirth!!.error = null
        }

        if (player.experience === getString(R.string.select_experience)) {
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

    /* CAMERA */
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun startCamera() {
        // Check if any camera exists
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

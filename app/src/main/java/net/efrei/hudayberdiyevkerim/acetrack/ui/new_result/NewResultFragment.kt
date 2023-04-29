package net.efrei.hudayberdiyevkerim.acetrack.ui.new_result

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentNewResultBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.LocationModel
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class NewResultFragment : Fragment(),
    OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    lateinit var app: MainApp
    private var _fragmentBinding: FragmentNewResultBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!
    private var result = ResultModel()
    private var matchLocation = LocationModel()
    private var isEditingExistingResult = false
    private var calendar: Calendar = Calendar.getInstance()
    private var resultDate: TextView? = null
    private var buttonAddDate: Button? = null

    private var mMap: GoogleMap? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }

        // Displays location for existing match result (if result is being modified)
        if (result.location != null) {
            val latLng = LatLng(result.location!!.latitude, result.location!!.longitude)
            mMap!!.addMarker(MarkerOptions().position(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(1000)
        mLocationRequest!!.setFastestInterval(1000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient!!,
                mLocationRequest!!,
                this
            )
        }
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onLocationChanged(location: Location) {
        mLastLocation = location

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        // Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        // move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

        // stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient!!, this)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}

    private fun searchLocation() {
        val locationSearch = fragmentBinding.editText as EditText
        val location = locationSearch.text.toString()
        var addressList: List<Address>? = null

        if (location != null || location != "") {
            val geocoder = Geocoder(requireActivity())

            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val address: Address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            matchLocation.latitude = address.latitude
            matchLocation.longitude = address.longitude
            result.location = matchLocation

            Toast.makeText(
                requireContext(),
                address.latitude.toString() + " " + address.longitude.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentBinding = FragmentNewResultBinding.inflate(inflater, container, false)
        val root = fragmentBinding.root

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val button = fragmentBinding.searchButton as Button
        button.setOnClickListener {
            searchLocation()
        }

        if (arguments?.get("result") !== null) {
            isEditingExistingResult = true
        }

        if (isEditingExistingResult) {
            activity?.title = getString(R.string.menu_editResult)
        } else {
            activity?.title = getString(R.string.new_result)
        }

        val allPlayers = app.players.findAll()
        var playerNames: MutableList<String> = allPlayers.map{it.firstName + " " + it.lastName} as MutableList<String>
        playerNames.add(0, getString(R.string.select_a_player))

        val playerOneSpinner = fragmentBinding.playerOneSpinner
        val playerTwoSpinner = fragmentBinding.playerTwoSpinner

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, playerNames)
        playerOneSpinner.adapter = adapter
        playerTwoSpinner.adapter = adapter

        resultDate = fragmentBinding.resultDate
        buttonAddDate = fragmentBinding.addDateButton
        resultDate!!.text = getString(R.string.date_placeholder)

        // Date picker implemented with reference to https://www.tutorialkart.com/kotlin-android/android-datepicker-kotlin-example/
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        buttonAddDate!!.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        if (isEditingExistingResult) {
            val resultId: String = arguments?.get("result") as String
            result = app.results.findById(resultId.toLong())!!

            fragmentBinding.playerOneSpinner.setSelection(playerNames.indexOf(result.playerOne))
            fragmentBinding.playerTwoSpinner.setSelection(playerNames.indexOf(result.playerTwo))
            fragmentBinding.playerOneScore.setText(result.playerOneScore.toString())
            fragmentBinding.playerTwoScore.setText(result.playerTwoScore.toString())
            val date = Instant.ofEpochSecond(result.date).atZone(ZoneId.systemDefault()).toLocalDate()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.monthValue - 1)
            calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            resultDate!!.text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            fragmentBinding.addResultButton.setText(R.string.update_result)
        }

        playerOneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                result.playerOne = playerNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        playerTwoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                result.playerTwo = playerNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        setButtonListener(fragmentBinding)
        return root
    }

    private fun updateDateInView() {
        val format = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        resultDate!!.text = simpleDateFormat.format(calendar.time)
    }

    private fun setButtonListener(layout: FragmentNewResultBinding) {
        layout.addResultButton.setOnClickListener() {
            val playerOneScore = layout.playerOneScore.text.toString()
            val playerTwoScore = layout.playerTwoScore.text.toString()
            val resultDate = layout.resultDate.text
            val selectPlayerString = requireActivity().getString(R.string.select_a_player)

            if (result.playerOne == selectPlayerString || result.playerTwo == selectPlayerString || playerOneScore.isEmpty() || playerTwoScore.isEmpty() || resultDate.isEmpty()) {
                Snackbar
                    .make(it, R.string.fill_in_all_fields, Snackbar.LENGTH_LONG)
                    .show()
            } else if (result.playerOne == result.playerTwo) {
                Snackbar
                    .make(it, R.string.select_another_player, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                result.playerOneScore = playerOneScore.toInt()
                result.playerTwoScore = playerTwoScore.toInt()
                result.date = LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

                if (isEditingExistingResult) {
                    app.results.update(result.copy())
                } else {
                    app.results.create(result.copy())
                }

                findNavController().popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NewResultFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}

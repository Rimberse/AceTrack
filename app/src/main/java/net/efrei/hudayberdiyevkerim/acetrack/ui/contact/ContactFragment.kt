package net.efrei.hudayberdiyevkerim.acetrack.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentContactBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class ContactFragment : Fragment() {
    lateinit var app: MainApp
    private var _fragmentBinding: FragmentContactBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentBinding = FragmentContactBinding.inflate(inflater, container, false)
        val root = fragmentBinding.root
        activity?.title = getString(R.string.menu_contact)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync { map ->
            map.mapType = GoogleMap.MAP_TYPE_NORMAL

            val location = LatLng(48.78873349987668, 2.3637448505183554)

            map.addMarker(MarkerOptions()
                .position(location)
                .title(getString(R.string.contact_location_title))
            )

            map.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}

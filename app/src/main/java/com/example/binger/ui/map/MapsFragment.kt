package com.example.binger.ui.map
import android.content.ContentValues.TAG
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.binger.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        val tarumt = LatLng(3.2155573,101.7281073)
        googleMap.addMarker(MarkerOptions().position(tarumt).title("TARUMT"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tarumt, 15f))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = "AIzaSyBzuVq6wnf2ZmW4vVIloarmXT4e2_NqDpY"
        Places.initialize(requireContext(), apiKey)
        placesClient = Places.createClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?

        autocompleteFragment?.setCountry("MY")

        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment?.setActivityMode(AutocompleteActivityMode.OVERLAY)
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                googleMap.clear() // Clear existing markers
                googleMap.addMarker(MarkerOptions().position(latLng!!).title(place.name))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                // Zoom the camera to the selected location
                val zoomLevel = 15.0f
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
                googleMap.moveCamera(cameraUpdate)

                // Extract the city from the selected place
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    val town = addresses[0].adminArea
                    if (town != null) {
                        // Use the extracted city
                        // You can do further processing with the city
                        Log.d("--------------------------------------Selected City", town)
                    }
                }
            }

            override fun onError(status: Status) {
                // Handle error
                val errorMessage = status.statusMessage ?: "Error occurred"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                Log.v(TAG,errorMessage)
            }
        })
    }

}
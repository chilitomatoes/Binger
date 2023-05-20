package com.example.binger.ui.address

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Address

import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.binger.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
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
import java.io.IOException
import java.util.*

class MapsFragment : Fragment(), LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private lateinit var mMap:GoogleMap
    var currentLocation: Location?= null
    var currentMarker: Marker?= null
    var fusedLocationProviderClient: FusedLocationProviderClient?= null
    var googleApiClient: GoogleApiClient?= null
    var locationRequest: LocationRequest?= null


    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap



        val latlong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latlong)

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragEnd(p0: Marker) {

                if(currentMarker != null){
                    currentMarker?.remove()

                    val newLatLng = LatLng(p0?.position!!.latitude, p0?.position!!.longitude)
                    drawMarker(newLatLng)


                }

            }

            override fun onMarkerDragStart(p0: Marker) {

            }

        })
    }

    protected fun buildGoogleApiClient(){
        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        googleApiClient!!.connect()
    }

    private fun drawMarker(latlong: LatLng){
        var markerOption = MarkerOptions().position(latlong).title("I am here").snippet(getAddress(latlong.latitude, latlong.longitude))
        markerOption.draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong,15f))
        currentMarker= mMap.addMarker((markerOption))
        currentMarker?.showInfoWindow()
    }

    private fun getAddress(lat:Double, lon:Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat,lon,1)
        return addresses?.get(0)?.getAddressLine(0).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocation()
    }

    private fun fetchLocation(){
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1000)
            return
        }
        val task = fusedLocationProviderClient?.lastLocation
        Log.v("---------------------------------------------------------------------"+ TAG,task.toString())
        task?.addOnSuccessListener { location->
            if(location != null){
                this.currentLocation = location
                Log.v(TAG,"---------------------------------------------------------------------"+currentLocation.toString())
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1000 -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        if(currentMarker!=null){
            currentMarker!!.remove()
        }

        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        currentMarker = mMap!!.addMarker(markerOptions)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(11f))

        if(googleApiClient!=null){
            LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }

    override fun onConnected(p0: Bundle?) {
        locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest!!.interval = 1000
        locationRequest!!.fastestInterval = 1000
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.getFusedLocationProviderClient(requireContext())
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    fun searchLocation(view: View){
        val locationSearch: EditText = requireActivity().findViewById(R.id.searchEditText)
        var location: String
        location = locationSearch.text.toString().trim()
        var addressList: List<Address>?= null

        if(location == null || location == ""){
            Toast.makeText(requireContext(),"provide location", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(requireContext())
            try {
                addressList = geoCoder.getFromLocationName(location,1)
            }catch (e:IOException){
                e.printStackTrace()
            }

            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }
}
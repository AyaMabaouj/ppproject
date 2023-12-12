package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class Map : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        searchView = findViewById(R.id.searchView)

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCurrentLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search submission
                if (query != null && query.isNotBlank()) {
                    searchLocation(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text change
                return true
            }
        })

        val voiceIcon = findViewById<ImageView>(R.id.voiceSearchIcon)
        voiceIcon.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCurrentLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showCurrentLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get the last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null && mGoogleMap != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)

                        // Move the camera to the current location with a zoom level of 15
                        mGoogleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                15f
                            )
                        )

                        // Add a marker with a red color and title
                        mGoogleMap?.addMarker(
                            MarkerOptions()
                                .position(currentLatLng)
                                .title("Your Location")
                                .icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_RED
                                    )
                                )
                        )
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            // Handle the case where the location permission is not granted
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchLocation(location: String) {
        // Handle the logic for searching the location and updating the map
        // For simplicity, let's assume you have a method searchLocationByName
        val latLng = searchLocationByName(location)
        if (latLng != null) {
            mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchLocationByName(locationName: String): LatLng? {
        val geocoder = Geocoder(this)

        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)

            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                return LatLng(address.latitude, address.longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.isNotEmpty()) {
                val query = matches[0]
                // Handle the voice search query as needed
                searchView.setQuery(query, true)
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val VOICE_RECOGNITION_REQUEST_CODE = 2
    }
}

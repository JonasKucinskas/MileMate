package com.example.milemate
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*


class GPS(private val context: Context, private val locationListener: LocationListener) : LocationListener {

    
    private var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest
    private var locationCallback: LocationCallback
    private var location: Location? = null
    private var lastUpdateTime: Long = 0
    private var lastLocation: Location? = null

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(MIN_TIME_BW_UPDATES)
            .setFastestInterval(MIN_TIME_BW_UPDATES)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { onLocationChanged(it) }
            }
        }
    }

    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    fun getLatitude(): Double {
        return location?.latitude ?: 0.0
    }

    fun getLongitude(): Double {
        return location?.longitude ?: 0.0
    }

    fun getSpeed(): Float {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastUpdateTime

        if (elapsedTime >= MIN_TIME_BW_UPDATES && lastLocation != null) {
            val distance = location?.let { lastLocation?.distanceTo(it) }
            val speed = distance?.div(elapsedTime.toFloat()) ?: 0f

            lastUpdateTime = currentTime
            lastLocation = location

            return speed
        }

        return 0f
    }

    fun getLocationInfo(): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocation(
                getLatitude(),
                getLongitude(),
                1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return "exceptionas"
        }

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val city = address.locality
            val state = address.adminArea
            val country = address.countryName

            return "$city, $state, $country"
        }

        return "nera"
    }

    override fun onLocationChanged(newLocation: Location) {
        location = newLocation
        locationListener.onLocationChanged(newLocation)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    }
}
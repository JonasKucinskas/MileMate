package com.example.milemate
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*
import kotlin.math.*


interface LocationListenerr {
    fun onLocationChanged(newLocation: Location)
    fun onSpeedChanged(speed: Double)
    fun onDistanceChanged(distance: Double)
}

class GPS(private val context: Context, private val locationListener: LocationListenerr) : LocationListener {


    private var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest
    private var locationCallback: LocationCallback
    private var location: Location? = null
    private var lastUpdateTime: Long = 0
    private var lastLocation: Location? = null
    private var traveledDistance: Double = 0.0

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


    fun getLatitude(): Double {
        return location?.latitude ?: 0.0
    }

    fun getLongitude(): Double {
        return location?.longitude ?: 0.0
    }

    fun getTraveledDistance(): Double {
        return traveledDistance
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    fun getSpeed(): Double {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastUpdateTime


        if (elapsedTime >= MIN_TIME_BW_UPDATES) {
            val distance = location?.let { lastLocation?.distanceTo(it) }
            val elapsedTimeInSeconds = elapsedTime / 1000.0
            val speedMetersPerSecond = distance?.div(elapsedTimeInSeconds) ?: 0.0
            val speedKilometersPerHour = speedMetersPerSecond * 3.6

            lastUpdateTime = currentTime
            lastLocation = location

            Log.d("Speed", "Calculated Distance: $distance")
            Log.d("Speed", "Last Update Time: $lastUpdateTime, Last Location: $lastLocation")

            if (distance != null) {
                traveledDistance += distance
            }

            return speedKilometersPerHour
        }

        return 0.0
    }


    override fun onLocationChanged(newLocation: Location) {
        location = newLocation
        locationListener.onLocationChanged(newLocation)

        val speed = getSpeed()
        locationListener.onSpeedChanged(speed)

        val distance = getTraveledDistance()
        locationListener.onDistanceChanged(distance)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0.5f
        private const val MIN_TIME_BW_UPDATES: Long = 200
    }
}



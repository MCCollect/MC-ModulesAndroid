package com.mc.mcmodules.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

class GpsUtils(
    private val activity: Activity,
    private val locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val callbackSuccess: (() -> Unit)? = null,
    private val callbackNotFoundLocation: (() -> Unit)? = null
) {
    //region TURN_GPS_GOOGLE_SERVICES
    @SuppressLint("VisibleForTests")
    fun checkLocationSettings() {
        if (checkLocationPermission()) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(activity)

            client.checkLocationSettings(builder.build())
                .addOnSuccessListener(activity) {
                    if (callbackSuccess == null) activeGPS()
                    else callbackSuccess.invoke()
                }
                .addOnFailureListener(activity) { exception ->
                    if (exception is ApiException) {
                        val statusCode = exception.statusCode
                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                showGoogleServicesAlert(exception)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            if (callbackNotFoundLocation == null) showAlertUserNotFoundLocation()
                            else callbackNotFoundLocation.invoke()
                        }
                    }
                }
        } else requestLocationPermission()
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    private fun showAlertUserNotFoundLocation() {
        Toast.makeText(
            activity,
            "El dispositivo no tiene la opción de activar la ubicación.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun activeGPS() = println("GPS activo")

    private fun showGoogleServicesAlert(apiException: ApiException) {
        val resolvable = apiException as ResolvableApiException
        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
        locationSettingsLauncher.launch(intentSenderRequest)
    }
    //endregion
}
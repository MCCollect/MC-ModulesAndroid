package com.mc.mcmodules.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.lang.ref.WeakReference

/**
 * Utility class for GPS functionalities. In the following class, the following functionalities
 * can be performed:
 *
 *checkLocationSettings -> Activate GPS if necessary.
 *
 * @param activity Create a WeakReference to maintain a weak reference to the Activity object.
 * */
class GpsUtils(activity: Activity) {
    private val weakActivity = WeakReference(activity)

    //region TURN_GPS_FROM ALERT_GOOGLE_SERVICES
    /**
     * The Google Services alert is used to prompt the user to enable their GPS.
     * If an exception occurs, the user is redirected to the location settings so they
     * can manually activate it.
     *
     * @param locationSettingsLauncher Register for activity result for IntentSenderForResult.
     * If RESULT_OK GPS is active.
     * @param callbackSuccess Action to be performed if the GPS is active.
     * @param callbackNotFoundLocation Action to be taken if the device does not have
     * the option to enable location.
     *
     * @throws goToSettingsLocation Exception origin location source settings
     *
     * */
    @Throws(Exception::class)
    fun checkLocationSettings(
        locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>,
        callbackSuccess: (() -> Unit)? = null,
        callbackNotFoundLocation: (() -> Unit)? = null
    ) {
        try {
            createAlertFromGoogleServices(
                locationSettingsLauncher, callbackSuccess, callbackNotFoundLocation
            )
        } catch (e: Exception) {
            goToSettingsLocation()
        }
    }

    @SuppressLint("VisibleForTests")
    private fun createAlertFromGoogleServices(
       locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>,
       callbackSuccess: (() -> Unit)? = null,
       callbackNotFoundLocation: (() -> Unit)? = null
    ) {
        weakActivity.get()?.let { activity ->
            if (checkLocationPermission(activity)) {
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
                        Utils.freeMemory()
                    }
                    .addOnFailureListener(activity) { exception ->
                        if (exception is ApiException) {
                            val statusCode = exception.statusCode
                            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                showGoogleServicesAlert(exception, locationSettingsLauncher)
                            } else {
                                if (callbackNotFoundLocation == null) showAlertUserNotFoundLocation()
                                else callbackNotFoundLocation.invoke()
                            }
                        }
                        Utils.freeMemory()
                    }
            } else Toast.makeText(
                activity, "Se requiere permiso de ubicación.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun goToSettingsLocation() = weakActivity.get()?.startActivity(
        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    )

    private fun checkLocationPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showAlertUserNotFoundLocation() {
        weakActivity.get()?.let { activity ->
            Toast.makeText(
                activity,
                "El dispositivo no tiene la opción de activar la ubicación.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun activeGPS() = println("GPS activo")

    private fun showGoogleServicesAlert(
        apiException: ApiException,
        locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val resolvable = apiException as ResolvableApiException
        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
        locationSettingsLauncher.launch(intentSenderRequest)
    }
    //endregion
}
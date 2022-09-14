package com.softim.upaxtecnica.domain

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.ui.MainActivity

class LocationsService : Service() {

    private lateinit var locationCallback: LocationCallback
    private val CHANNEL_ID = "MOVIESAPI"
    private val LOG_TAG = "EnviarUbicacion"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /*
    * Servicio que muestra las localizacion del ususario cada 5 minutos
    * Utiliza una notificationCompact para mostrar la ubicacion actual
    * Falta una pequeña parte para subir la informacion a la Firestore aqui.
    * */
    private val TAG = "ServiceNotification"

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "Service onStartCommand " + startId)

        var lastLocation = ""
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                getActivity(this, 0, notificationIntent, FLAG_ONE_SHOT)
            }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    lastLocation = "Latitude ${it.latitude} \nLongitude${it.longitude}"
                    notifica(lastLocation, pendingIntent)
                } else {
                    Log.d(LOG_TAG, "No se pudo obtener la ubicación")
                }
            }
            val locationRequest = LocationRequest.create().apply {
                interval = 300000
                fastestInterval = 300000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    Log.d(LOG_TAG, "Se recibió una actualización")
                    for (location in p0.locations) {
                        lastLocation = "Latitude ${location.latitude} \nLongitude${location.longitude}"
                        notifica(lastLocation, pendingIntent)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.d(LOG_TAG, "Tal vez no solicitaste permiso antes")
        }


        return START_STICKY
    }
    private fun notifica(msj:String, pendingIntent: PendingIntent){
        val ID_NOTIFICATION = (Math.random() * 100).toInt()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Your Location is")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentText(msj)
            .build()
        startForeground(ID_NOTIFICATION, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "Service onBind")
        return null
    }

}
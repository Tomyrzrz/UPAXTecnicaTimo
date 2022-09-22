package com.softim.upaxtecnica.domain.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.data.models.UserLocations
import java.util.*


class LocationService: Service(){
    private val TAG = "BOOMBOOMTESTGPS"
    private var mLocationManager: LocationManager? = null
    private val LOCATION_DISTANCE = 10f
    private val CHANNEL_ID = "MOVIESAPI"

    companion object{
        lateinit var mLastLocation: Location
    }

    private lateinit var fRef : FirebaseFirestore
    private lateinit var ref: DocumentReference

    class LocationListener(provider: String) : android.location.LocationListener {
        private val TAGS = "BOOMBOOMTESTGPS"

        init {
            Log.e(TAGS, "LocationListener $provider")
            mLastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            Log.e(TAGS, "onLocationChanged: $location")
            mLastLocation.set(location)
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAGS, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAGS, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAGS, "onStatusChanged: $provider")
        }

    }

    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )

    fun notifica(msj:String){
        val ID_NOTIFICATION = (Math.random() * 1000).toInt()
        val notification = NotificationCompat.Builder( this, CHANNEL_ID)
            .setContentTitle("Your Location is")
            .setSmallIcon(R.drawable.ic_location)
            .setAutoCancel(true)
            .setContentText(msj)
            //.setContentIntent(pendingIntent)
            .build()
        startForeground(ID_NOTIFICATION, notification)
    }

    private fun saveLocations(location: Location?) {
        val preferences = applicationContext.getSharedPreferences("user_gallery", Context.MODE_PRIVATE)
        val user_local = preferences?.getString("user", "")
        val uniqueID = UUID.randomUUID().toString()

        if (user_local == "") {
            val editor = preferences.edit()
            editor.putString("user", uniqueID)
            editor.apply()
            ref = fRef.collection("moviesAPIuser").document(uniqueID)
                .collection("locations").document()
        } else {
            ref = fRef.collection("moviesAPIuser").document(user_local!!)
                .collection("locations").document()
        }
        val user = UserLocations(uniqueID, location!!.latitude, location.longitude)
        ref.set(user)
            .addOnSuccessListener {
                Log.e(TAG, "Location SAVE")
            }.addOnFailureListener {
                Log.e(TAG, "Location NOT Save")
            }
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        val myHandler = Handler(Looper.getMainLooper())

        myHandler.post(object : Runnable {
            override fun run() {
                Thread.sleep(60000)
                notifica("Latitude: ${mLastLocation.latitude} Longitude: ${mLastLocation.longitude}")
                saveLocations(mLastLocation)
                myHandler.postDelayed(this, 300000 )
            }
        })
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {

        Log.e(TAG, "onCreate")
        fRef = FirebaseFirestore.getInstance()
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 30000, LOCATION_DISTANCE,
                mLocationListeners[1]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex)
                }
            }
        }
    }

    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        }
    }
}
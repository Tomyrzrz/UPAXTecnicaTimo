package com.softim.upaxtecnica.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.data.models.UserLocations
import com.softim.upaxtecnica.data.utils.ExceptionDialogFragment
import com.softim.upaxtecnica.domain.core.LocationService

class MapsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private var longitud: Double = 0.0
    private var latitud: Double = 0.0

    private val CHANNEL_ID = "MOVIESAPI"
    private lateinit var fRef : FirebaseFirestore
    private lateinit var ref: DocumentReference


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        enableMyLocation()
        map.setMaxZoomPreference(12F)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)

        val myHandler = Handler(Looper.getMainLooper())

        myHandler.post(object : Runnable {
            override fun run() {
                fRef.collectionGroup("locations").orderBy("time").limitToLast(1)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            for (doc in value){
                                val user = doc.toObject(UserLocations::class.java)
                                val location = LatLng(user.latitude!!, user.longitude!!)
                                map.addMarker(
                                    MarkerOptions()
                                        .position(location)
                                        .title("You")
                                )
                                map.moveCamera(CameraUpdateFactory.newLatLng(location))
                            }
                        } else {
                            Log.d("Hola", "Current data: null")
                        }
                    }
                myHandler.postDelayed(this, 300000 )
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    private lateinit var mapFragment: SupportMapFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
        requestPermissions()

        fRef = FirebaseFirestore.getInstance()
        ref = fRef.collection("moviesAPIuser").document()

    }


    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }
        else
            requestPermissions()
    }

    private fun requestPermissions() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        val message = "Debes aceptar los permisos de ubicacion."
                        ExceptionDialogFragment(message, "Warming").show(
                            parentFragmentManager,
                            ExceptionDialogFragment.TAG
                        )
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                val message = "Error"
                ExceptionDialogFragment(message,"Error").show(
                    parentFragmentManager,
                    ExceptionDialogFragment.TAG
                )
            }            .onSameThread().check()
    }

    /*override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = false
        }
    }*/

    private fun notificationLocation(msj: String) {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Your Location is")
            .setContentText(msj)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(2, builder.build())
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        latitud = p0.latitude
        longitud = p0.longitude
        notificationLocation("Latitude: ${p0.latitude}, Longitude: ${p0.longitude}")
    }

}
package com.softim.upaxtecnica.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*

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
import com.softim.upaxtecnica.domain.LocationsService
import java.util.*

class MapsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private var longitud: Double = 0.0
    private var latitud: Double = 0.0
    private var nombre: String = ""
    private lateinit var locationCallback: LocationCallback
    private val CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO = 2106
    private val LOG_TAG = "EnviarUbicacion"

    private val CHANNEL_ID = "MOVIESAPI"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fRef : FirebaseFirestore
    private lateinit var ref: DocumentReference


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        enableMyLocation()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)


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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    saveLocations(location)
                }
        }
        fRef = FirebaseFirestore.getInstance()

    }

    private fun saveLocations(location: Location?) {
        val sharedPreferences = activity?.getSharedPreferences("user_movies", AppCompatActivity.MODE_PRIVATE)
        val user_local = sharedPreferences?.getString("user", "")
        val uniqueID = UUID.randomUUID().toString()

        if (user_local == "") {
            val editor = sharedPreferences.edit()
            editor.putString("user", uniqueID)
            editor.apply()
            ref = fRef.collection("moviesAPIuser").document(uniqueID)
                .collection("locations").document()
        } else {
            ref = fRef.collection("moviesAPIuser").document(user_local!!)
                .collection("locations").document()
        }
        val user = UserLocations(uniqueID, location!!.latitude, location.longitude)
        val ubica = LatLng(location.latitude, location.longitude)
        map.addMarker(
            MarkerOptions()
                .position(ubica)
                .title(nombre)
                .alpha(0.8f)
                .flat(true)
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(ubica))
        ref.set(user)
            .addOnSuccessListener {
                val message = "Latitude: ${location.latitude} \nLongitude: ${location.longitude}"
                notificationLocation(message)
            }.addOnFailureListener {
                val message = "Upload Location Failed"
                notificationLocation(message)
            }
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

    override fun onResume() {
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
    }

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
        nombre = "You"
        saveLocations(p0)
        notificationLocation("Latitude: ${p0.latitude}, Longitude: ${p0.longitude}")
    }

}
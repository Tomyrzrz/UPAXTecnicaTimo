package com.softim.upaxtecnica.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.data.utils.ExceptionDialogFragment
import com.softim.upaxtecnica.databinding.ActivityMainBinding
import com.softim.upaxtecnica.domain.core.LocationService
import com.softim.upaxtecnica.ui.gallery.GalleryFragment
import com.softim.upaxtecnica.ui.maps.MapsFragment
import com.softim.upaxtecnica.ui.movies.MoviesFragment
import com.softim.upaxtecnica.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CHANNELID = "123456"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        replaceFragment(ProfileFragment())
        setupMenu()
        requestPermissions()
        Intent(this, LocationService::class.java).also { intent ->
            startService(intent)

        }
    }

    private fun setupMenu() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
                R.id.navigation_movies -> replaceFragment(MoviesFragment())
                R.id.navigation_maps -> replaceFragment(MapsFragment())
                else -> {
                    replaceFragment(GalleryFragment())
                }
            }
        true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNELID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activity_main_nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    private fun requestPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        val message = "Debes aceptar los permisos de ubicacion."
                        ExceptionDialogFragment(message, "Warming").show(
                            supportFragmentManager,
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
                    supportFragmentManager,
                    ExceptionDialogFragment.TAG
                )
            }            .onSameThread().check()
    }

}
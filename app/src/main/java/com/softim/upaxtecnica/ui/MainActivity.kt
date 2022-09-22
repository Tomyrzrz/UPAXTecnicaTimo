package com.softim.upaxtecnica.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.databinding.ActivityMainBinding
import com.softim.upaxtecnica.domain.core.LocationService
import com.softim.upaxtecnica.ui.gallery.GalleryFragment
import com.softim.upaxtecnica.ui.maps.MapsFragment
import com.softim.upaxtecnica.ui.movies.MoviesFragment
import com.softim.upaxtecnica.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_ID = "MOVIESAPI"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        replaceFragment(ProfileFragment())
        setupMenu()
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
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
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
}
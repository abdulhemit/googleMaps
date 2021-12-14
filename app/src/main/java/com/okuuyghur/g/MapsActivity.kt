package com.okuuyghur.g

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.okuuyghur.g.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationlistener : LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)


        // Latitude -> enlem
        // Longitude -> boylam

        // 37.1779519443868, 79.89519023304409
       /* val hoten = LatLng(37.1779519443868, 79.89519023304409)
        mMap.addMarker(MarkerOptions().position(hoten).title(" hoten in laskuy"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hoten,5f))

        */

        // casting -> as
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationlistener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                // locasyon , konu, degisince yapilacak islemler
                //println(p0.latitude)
                //println(p0.longitude)

                mMap.clear()
                val guncelKonum = LatLng(p0.latitude,p0.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Guncen konum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())

                try {
                   val adressListesi= geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                   if (adressListesi.size > 0 ){
                       println(adressListesi.get(0).toString())
                   }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            // izin verilmemix
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        } else {
            // izin verilmixse
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,2f,locationlistener)
            val sonbillinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonbillinenKonum != null){
                val sonbilinenLatLng = LatLng(sonbillinenKonum.latitude,sonbillinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonbilinenLatLng).title( "son bilinen konum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonbilinenLatLng,15f))
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1){
            if ( grantResults.size > 0 ){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    // izin verildi
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,2f,locationlistener)
                }
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())
            if (p0 != null){
                var adres = ""

                try {
                   val adreslistesi = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                   if (adreslistesi.size > 0 ){
                       if (adreslistesi.get(0).thoroughfare != null){
                           adres += adreslistesi.get(0).thoroughfare
                           if (adreslistesi.get(0).subThoroughfare != null){
                               adres += adreslistesi.get(0).subThoroughfare
                           }
                       }
                   }





                }catch (e:Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }

    }
}
/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.annotation.IntDef
import android.view.View
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_find_your_city.*
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_CITY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_DISTANCE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LATITUDE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LONGITUDE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.UNUSED_PARAMETER
import java.util.*


class FindCityActivity : BaseMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_your_city)
        showDistanceDialog()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        currentCity()
        initializeFragment()
    }


    fun lookForRestaurants(@Suppress(UNUSED_PARAMETER) view: View) {
        val findRestaurants = Intent(this, RestaurantListActivity::class.java)
        findRestaurants.putExtra(PARAM_CITY, city)
        findRestaurants.putExtra(PARAM_DISTANCE, distance.text.toString())
        findRestaurants.putExtra(PARAM_LATITUDE, latitude)
        findRestaurants.putExtra(PARAM_LONGITUDE, longitude)
        showProgressBar()
        startActivity(findRestaurants)
        finish()
    }

    fun askForLocation(@Suppress(UNUSED_PARAMETER) view: View) {
        askForPermission()
    }

    private fun initializeFragment() {
        autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
        autocompleteFragment.setHint(getString(R.string.find_city))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                city = p0?.name.toString()
            }

            override fun onError(p0: Status?) {
                //TODO LOG IT p0?.statusMessage
            }

        })
    }


    private fun showDistanceDialog() {
        @IntDef when (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                distance_layout.visibility = View.VISIBLE
                activity_find_city_location_denied.visibility = View.GONE
            }
            else -> {
                activity_find_city_location_denied.visibility = View.VISIBLE
                distance_layout.visibility = View.GONE
            }
        }
    }

    private fun currentCity() {
        val gcd = Geocoder(this, Locale.getDefault())
        @IntDef when (checkSelfPermission(ACCESS_COARSE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            city = gcd.getFromLocation(latitude, longitude, 1)[0].locality
                            autocompleteFragment.setText(city)
                        }
                    }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    private var city: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
}

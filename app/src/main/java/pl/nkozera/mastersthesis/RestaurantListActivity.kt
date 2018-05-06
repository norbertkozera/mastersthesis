/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.place.ApiRequest
import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.PlacesList

class RestaurantListActivity : BaseMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)
        API_KEY = getString(R.string.google_places_api_key)
        distance = intent.getStringExtra("distance")
        city = intent.getStringExtra("city")
        location = LocationCoordinates(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
        setUrl()
    }

    private fun setUrl() {
        url.append("https://maps.googleapis.com/maps/api/place/textsearch/json?key=$API_KEY&query=restaurant")

        when {
            !distance.isEmpty() -> {
                url.append("&location=${location.getLatitude()},${location.getLongitude()}&radius=$distance")
             //
            }
            !city.isEmpty() -> {
                //url.append("-${}")
             //   ApiRequest().request(this,url.toString())
                ApiRequest(this).search(location,city)
            }
            else -> {
                makeToast(getString(R.string.error_no_search_data))
                startActivity(Intent(this, FindCityActivity::class.java))
                finish()
            }
        }

    }

    private fun radius(x: Double): Double {
        return x * Math.PI / 180
    }

    private fun getDistance(myLocation: LocationCoordinates, secondLocation: LocationCoordinates): Double {
        val R = 6378137 // Earth’s mean radius in meter
        val dLat = secondLocation.getLatitude() - myLocation.getLatitude()
        val dLong = secondLocation.getLongitude() - myLocation.getLongitude()
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(radius(myLocation.getLatitude())) * Math.cos(radius(secondLocation.getLatitude())) *
                Math.sin(dLong / 2) * Math.sin(dLong / 2)
        val c = 2 * Math.asin(Math.sqrt(a))
        val d = R * c
        return d
    }

    private lateinit var API_KEY: String
    private lateinit var distance: String
    private lateinit var city: String
    private lateinit var location: LocationCoordinates
    private var url = StringBuilder()

}
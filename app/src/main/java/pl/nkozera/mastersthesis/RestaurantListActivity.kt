/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.bumptech.glide.Glide
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.Place
import pl.nkozera.mastersthesis.place.Places


class RestaurantListActivity : BaseMenuActivity() {


    private lateinit var places: Places
    private lateinit var placesList: List<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)
        findPlaces()
        printPlaces()
    }

    private fun printPlaces() {
        placesList = places.getPlaces()

        val restaurantListProgress = findViewById<View>(R.id.restaurantList_progress) as ProgressBar
        restaurantListProgress.visibility = View.GONE

        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.activity_restaurant_list, null)

        val rv = v.findViewById(R.id.restaurant_list) as LinearLayout

        for (i in 0 until placesList.size) {
            val place = placesList.get(i)

            val rl = RelativeLayout(this)
            rl.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            rl.background = resources.getDrawable(R.drawable.custom_background)
            val rlParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            rlParams.height = resources.getDimensionPixelSize(R.dimen.restaurant_list_element_height)
            rl.layoutParams = rlParams
            rl.setOnClickListener {
                onClick(i)
            }

            val tvPlaceName = TextView(this)
            tvPlaceName.text = place.getPlaceName()
            tvPlaceName.gravity = Gravity.CENTER_HORIZONTAL
            tvPlaceName.id = View.generateViewId()
            tvPlaceName.textSize = resources.getDimension(R.dimen.restaurant_list_name_size)
            tvPlaceName.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            rl.addView(tvPlaceName)

            val tvDistance = TextView(this)
            tvDistance.text = place.getLocation().toString()
            tvDistance.textSize = resources.getDimension(R.dimen.restaurant_list_others)
            tvDistance.id = View.generateViewId()
            val tvDistanceParams = RelativeLayout.LayoutParams(R.dimen.restaurant_list_width_location, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvDistanceParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            tvDistanceParams.addRule(RelativeLayout.BELOW, tvPlaceName.id)
            tvDistance.layoutParams = tvDistanceParams
            rl.addView(tvDistance)

            val tvOpenedNow = TextView(this)
            val openedNowString = place.getOpenedNow()
            val openedNowMessage = if ("".equals(openedNowString)) {
                "Skontaktuj sie z knajpa"
            } else {
                when (openedNowString.toBoolean()) {
                    true -> "OTWARTE!"
                    false -> "ZAMKNIĘTE!"
                }
            }
            tvOpenedNow.text = openedNowMessage
            tvOpenedNow.textSize = resources.getDimension(R.dimen.restaurant_list_others)
            tvOpenedNow.id = View.generateViewId()
            val tvOpenedNowParams = RelativeLayout.LayoutParams(R.dimen.restaurant_list_width_location, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvOpenedNowParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//            tvOpenedNowParams.addRule(RelativeLayout.BELOW, tvDistance.id)
            tvOpenedNow.layoutParams = tvOpenedNowParams
            rl.addView(tvOpenedNow)

            val iwIcon = ImageView(this)
            iwIcon.id = View.generateViewId()
            val iwIconParams = RelativeLayout.LayoutParams(100, 100)
            iwIconParams.addRule(RelativeLayout.BELOW, tvPlaceName.id)
            iwIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            iwIcon.layoutParams = iwIconParams
            rl.addView(iwIcon)
            Glide.with(this).load(place.getIconUri()).into(iwIcon)

            val tvRating = TextView(this)
            tvRating.text = place.getRating().toString()
            tvRating.textSize = resources.getDimension(R.dimen.restaurant_list_others)
            tvRating.id = View.generateViewId()
            val tvRatingParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvRatingParams.addRule(RelativeLayout.BELOW, iwIcon.id)
            tvRatingParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            tvRating.layoutParams = tvRatingParams
            rl.addView(tvRating)

            rv.addView(rl)
        }


        setContentView(v)

        print("")
    }

    private fun onClick(i: Int) {
        Toast.makeText(this, "Klikam w " + placesList[i].getPlaceName(), Toast.LENGTH_LONG).show()
    }

    private fun findPlaces() {

        places = Places(this)
        API_KEY = getString(R.string.google_places_api_key)
        distance = intent.getStringExtra("distance")
        city = intent.getStringExtra("city")
        location = LocationCoordinates(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))

        when {
            !distance.isEmpty() -> {
                places.findPlaces(location, distance)
            }
            !city.isEmpty() -> {
                places.findPlaces(city)
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
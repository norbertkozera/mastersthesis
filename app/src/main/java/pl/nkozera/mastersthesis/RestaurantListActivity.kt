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
import pl.nkozera.mastersthesis.place.Distance
import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.Place
import pl.nkozera.mastersthesis.place.PlacesList


class RestaurantListActivity : BaseMenuActivity() {


    private lateinit var places: PlacesList
    private lateinit var placesList: List<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgressBar()
        places = PlacesList(this)
        API_KEY = getString(R.string.google_places_api_key)
        distance = intent.getStringExtra("distance")
        city = intent.getStringExtra("city")
        location = LocationCoordinates(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
        findPlaces()
        hideProgressBar(R.layout.activity_restaurant_list)
        printPlaces()

    }

    public override fun onStart() {
        super.onStart()

    }

    private fun printPlaces() {
        placesList = places.getPlaces()

//        val restaurantListProgress = findViewById<View>(R.id.restaurantList_progress) as ProgressBar
//        restaurantListProgress.visibility = View.GONE

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
            tvDistance.text = "Około ${Distance().getDistance(location, place.getLocation())} kilometrów"
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
            tvOpenedNowParams.addRule(RelativeLayout.BELOW, tvDistance.id)
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
        val placeDetailsActivity = Intent(this, PlaceDetailsActivity::class.java)
        placeDetailsActivity.putExtra("placeId", placesList[i].getPlaceId())
        showProgressBar()
        startActivity(placeDetailsActivity)
        finish()
        //flush
    }

    private fun findPlaces() {
        places = PlacesList(this)

        when {
            !distance.isEmpty() -> {
                if (location.isEmpty()) {
                    Toast.makeText(this, "Nie pozwoliles na lokalizację", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, FindCityActivity::class.java))
                    finish()
                } else {
                    places.findPlaces(location, distance)
                }
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


    private lateinit var API_KEY: String
    private lateinit var distance: String
    private lateinit var city: String
    private lateinit var location: LocationCoordinates
    private var url = StringBuilder()

}
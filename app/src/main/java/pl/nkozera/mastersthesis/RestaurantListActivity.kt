/*
 * Master Thesis project
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_DOUBLE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_CITY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_DISTANCE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LATITUDE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LONGITUDE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID
import pl.nkozera.mastersthesis.location.Distance
import pl.nkozera.mastersthesis.location.LocationCoordinates
import pl.nkozera.mastersthesis.place.PlacesCache
import pl.nkozera.mastersthesis.place.PlacesList
import pl.nkozera.mastersthesis.place.asynctasks.GenerateUrl
import pl.nkozera.mastersthesis.place.asynctasks.GetPlacesAsyncTask
import pl.nkozera.mastersthesis.place.asynctasks.OnTaskCompleted
import pl.nkozera.mastersthesis.place.objects.Place
import java.util.*


class RestaurantListActivity : BaseMenuActivity(), OnTaskCompleted {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgressBar()
        API_KEY = getString(R.string.google_places_api_key)
        distance = intent.getStringExtra(PARAM_DISTANCE)
        city = intent.getStringExtra(PARAM_CITY)
        location = LocationCoordinates(intent.getDoubleExtra(PARAM_LATITUDE, DEFAULT_DOUBLE), intent.getDoubleExtra(PARAM_LONGITUDE, DEFAULT_DOUBLE))
        val placesFromCache = PlacesCache.getPlacesInCityFromCache(city)
        if (!distance.isEmpty()) {
            val placesInLocation = PlacesCache.getPlacesInLocation(location, distance)
            when {
                placesInLocation!!.isNotEmpty() -> {
                    placesList.addAll(placesInLocation)
                    hideProgressBar(R.layout.activity_restaurant_list)
                    printPlaces()
                }
                else -> GetPlacesAsyncTask(places, true, this).execute(GenerateUrl(this).findPlacesUrl(location, distance, city, EMPTY_STRING))
            }
        } else if (placesFromCache != null) {
            placesList.addAll(placesFromCache)
            hideProgressBar(R.layout.activity_restaurant_list)
            printPlaces()
        } else {
            GetPlacesAsyncTask(places, true, this).execute(GenerateUrl(this).findPlacesUrl(location, distance, city, EMPTY_STRING))
        }
    }


    override fun onTaskCompleted() {
        placesList.addAll(filterList(places.getPlaces()))
        if (EMPTY_STRING != places.getNextPageToken()) {
            GetPlacesAsyncTask(places, false, this).execute(GenerateUrl(this).findPlacesUrl(location, distance, city, places.getNextPageToken()))
        } else {
            if (distance.isEmpty()) {
                PlacesCache.addPlacesInCityToCache(city, placesList)
            } else {
                PlacesCache.addPlacesInLocation(location, distance, placesList)
            }
            hideProgressBar(R.layout.activity_restaurant_list)
            printPlaces()
        }


    }

    private fun filterList(places: LinkedList<Place>): List<Place> {
        return if (distance.isEmpty()) {
            places
        } else {
            val retList = LinkedList<Place>()
            for (place in places) {
                if (Distance().getDistance(location, place.getLocation()) <= (distance.toDouble() / 1000)) {
                    retList.add(place)
                }
            }
            retList
        }
    }

    private fun printPlaces() {

        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.activity_restaurant_list, null) //FIXME Do not use null ViewGroup

        val rv = v.findViewById(R.id.restaurant_list) as LinearLayout

        for (i in 0 until placesList.size) {
            val place = placesList[i]

            val rl = RelativeLayout(this)
            rl.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            rl.background = resources.getDrawable(R.drawable.custom_background, theme)
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
            tvDistance.text = getString(R.string.distance_about_kilometers, Distance().getDistance(location, place.getLocation()).toString())
            tvDistance.textSize = resources.getDimension(R.dimen.restaurant_list_others)
            tvDistance.id = View.generateViewId()
            val tvDistanceParams = RelativeLayout.LayoutParams(R.dimen.restaurant_list_width_location, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvDistanceParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            tvDistanceParams.addRule(RelativeLayout.BELOW, tvPlaceName.id)
            tvDistance.layoutParams = tvDistanceParams
            rl.addView(tvDistance)

            val tvOpenedNow = TextView(this)
            val openedNowString = place.getOpenedNow()
            val openedNowMessage = if (EMPTY_STRING == openedNowString) {
                EMPTY_STRING
            } else {
                when (openedNowString.toBoolean()) {
                    true -> getString(R.string.opened)
                    false -> getString(R.string.closed)
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
            val iwIconParams = RelativeLayout.LayoutParams(100, 100) //FiXME change to dimmens
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
    }

    private fun onClick(i: Int) {
        val placeDetailsActivity = Intent(this, PlaceDetailsActivity::class.java)
        placeDetailsActivity.putExtra(PARAM_PLACE_ID, placesList[i].getPlaceId())
        showProgressBar()
        startActivity(placeDetailsActivity)
        finish()
    }

    private lateinit var API_KEY: String
    private lateinit var distance: String
    private lateinit var city: String
    private lateinit var location: LocationCoordinates
    private var places = PlacesList()
    private var placesList = LinkedList<Place>()
}

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
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues
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
//        if (!distance.isEmpty()) {
//            val placesInLocation = PlacesCache.getPlacesInLocation(location, distance)
//            when {
//                placesInLocation!!.isNotEmpty() -> {
//                    placesList.addAll(placesInLocation)
//                    hideProgressBar(R.layout.activity_restaurant_list)
//                    printPlaces()
//                }
//                else -> GetPlacesAsyncTask(places, true, this).execute(GenerateUrl(this).findPlacesUrl(location, distance, city, EMPTY_STRING))
//            }
//        } else if (placesFromCache != null) {
//            placesList.addAll(placesFromCache)
//            hideProgressBar(R.layout.activity_restaurant_list)
//            printPlaces()
//        } else {
        val url = GenerateUrl (this).findPlacesUrl(location, distance, city, EMPTY_STRING);
        GetPlacesAsyncTask(places, true, this).execute(url)
//        }
    }


    override fun onTaskCompleted() {
        places.addToPlaceList(prepareArray()) //FIXME OnlyForDevelopemnt
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





//FIXME ONLY FOR TEST



    fun prepareArray(): JsonArray {
        val parser = JsonParser()
        val o = parser.parse(jsonString).asJsonObject
        return o.get(BaseValues.PARAM_RESULTS).asJsonArray
    }

    private val jsonString = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"next_page_token\" : \"CuQD0wEAAID36QJT1_KRXesE9E3W99V8PZlTKi43K1ZLbBUIJMg9Gyp2EHsX08kQSZD75VgMNJAQwId-rkdwfkpFF6xN26FxmA-WMOJAfXEGTKqjEPSslsPhxIqALnq0G9Nou429TozSe0-bajjeMBEsVAcommAXwBGuHeYGSDjiQXWLBsOLPgM4O4h60ynvKcH_Andoao3h-jDvVVaWkQSnV6YlhUO3YUQbv-URoWyUVC-0F_HpTNTL-4cF1QqfSsrDqXUIj6X55LrByhT417WHQb1qddToNfVkn3KgOzYyL18O1DJnPJI6Zub8eFxbOuaqBhQCKKLxs9FdVas0BDrJFv8x5WJb3Q8d0Rad_p2PZlJeN-6adVVi6EwTtvEfvSUBjt7m8jDO3tMndAuG2J-NExL-MUBoAo5QjDcJ2WqKUXiL9OIN-Av4dbV9Y5vgFNfULXmGxLD4FbE0_JIw7DXOqIZs0stHfEsKOWOfEcTH2uvywppNfWx2JrefNLNNDg2operaJZhsvc4Z7zc1rzBNBWp8epW6gtn7WATh29zzKGj7Nv18GSDtF8gV2LpVoKyD072FOKKGaaMH0Rxq6MRSBMhEREwivpSc57Xam_RxW0mCnh3O3ovG7jGNkNaWjmhdFhvB1BIQYNP3mnrXXUZy2iuYpc8ExRoUwYYe58wfrtApDmDdif4xh8zXXHI\",\n" +
            "   \"results\" : [\n" +
            "      {\n" +
            "         \"formatted_address\" : \"Józefa 14, 31-056 Kraków, Polska\",\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 50.05062299999999,\n" +
            "               \"lng\" : 19.9444188\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 50.05199087989272,\n" +
            "                  \"lng\" : 19.94575982989272\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 50.04929122010728,\n" +
            "                  \"lng\" : 19.94306017010728\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"24d6031ed5243f1c9627f9f3a37989ec3e584951\",\n" +
            "         \"name\" : \"Starka\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : false,\n" +
            "            \"weekday_text\" : []\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 3120,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117317344038593744956/photos\\\"\\u003eA Google User\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAnfVQLz_CC508YVMCzGg7aA6SqdisUFbsj39QggtKq1_DKPaXaZMxpgiKKKU7hzJ8kkbIebtdvZA_WTFv8vV4zGuq4uLkp0bvAJWZoTENIE8l_W5dhpRh8nGCaUnYQGynEhAfoDizpjNHxfGOkWvsPF1IGhTnLNIEqBX8ObAGoorj0Jl22ETwMA\",\n" +
            "               \"width\" : 4160\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJuaKgPWpbFkcR1BEMavHF11Y\",\n" +
            "         \"rating\" : 4.6,\n" +
            "         \"reference\" : \"CmRbAAAAxLQYJ-gBbSNYgSQ8YIYlxxeUxi8LRDy5fy2U0fNIFa__D4Un9N5Hs4xZOUlQ4Jgjnz_QzgK1avFBXsCVgDb6-ah4b1dc3z0OEJ1pFV4jr_GEcQOYZd4dy0O8Ieu6pZNLEhAmCr8-Say-hY25HdD5PqKJGhQ42qCuJO2KMIwy7nvekkSaYXe1zw\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ]\n" +
            "      } \n" +
            "\t],\n" +
            "   \"status\" : \"OK\"\n" +
            "}"
}

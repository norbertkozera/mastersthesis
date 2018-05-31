/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import com.google.gson.*
import pl.nkozera.mastersthesis.base.BaseValues.Companion.FORMATTED_ADDRESS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPEN_NOW
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_GEOMETRY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_ICON
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LOCATION
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPENING_HOURS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID_GOOGLE_API
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RATING
import pl.nkozera.mastersthesis.location.LocationCoordinates
import pl.nkozera.mastersthesis.place.objects.Place
import java.util.*


class PlacesList : Common() {


    fun getPlaces(): LinkedList<Place> {
        return placesList
    }


    fun addToPlaceList(results: JsonArray) {
        val jsonArray = Gson().fromJson(results, JsonArray::class.java)
        for (i in 0 until jsonArray.size()) {
            val placeId = returnString(jsonArray.get(i), PARAM_PLACE_ID_GOOGLE_API)
            val location: LocationCoordinates = returnLocationCoordinates(jsonArray.get(i), PARAM_GEOMETRY, PARAM_LOCATION)
            val placeName = returnString(jsonArray.get(i), PARAM_NAME)
            val address = returnString(jsonArray.get(i), FORMATTED_ADDRESS)
            val iconUri = returnString(jsonArray.get(i), PARAM_ICON)
            val openedNow = returnString(jsonArray.get(i), PARAM_OPENING_HOURS, PARAM_OPEN_NOW)
            val rating = returnDouble(jsonArray.get(i), PARAM_RATING)

            placesList.add(Place(
                    placeId,
                    location,
                    placeName,
                    address,
                    iconUri,
                    openedNow,
                    rating
            ))
        }
    }

    private var placesList = LinkedList<Place>()
    private var nextPageToken = ""

    fun setNextPaheToken(token: String){
        this.nextPageToken = token
    }

    fun getNextPageToken(): String {
        return nextPageToken
    }


}
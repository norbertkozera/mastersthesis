/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import com.google.gson.*
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_PLACE_DETAILS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.FORMATTED_ADDRESS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPEN_NOW
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_AUTHOR_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_COMMENTS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_GEOMETRY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LOCATION
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPENING_HOURS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHONE_NUMBER
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTOS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTO_REFERENCE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTO_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID_GOOGLE_API
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RATING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_TEXT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_TIME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_WEBPAGE
import pl.nkozera.mastersthesis.place.objects.PlaceComment
import pl.nkozera.mastersthesis.place.objects.PlaceDetails


class ChoosenPlaceDetails : Common() {


    fun getCurrentPlaceDetails(): PlaceDetails {
        return placeDetails
    }


    fun createPlaceDetails(detailsJson: JsonElement) {
        returnJsonArray(detailsJson, arrayOf(PARAM_PHOTOS))
        val placeId = returnString(detailsJson, PARAM_PLACE_ID_GOOGLE_API)
        val location = returnLocationCoordinates(detailsJson, PARAM_GEOMETRY, PARAM_LOCATION)
        val placeName = returnString(detailsJson, PARAM_NAME)
        val photoRef = returnString(returnJsonArray(detailsJson, arrayOf(PARAM_PHOTOS)).get(0), PARAM_PHOTO_REFERENCE)
        val address = returnString(detailsJson, FORMATTED_ADDRESS)
        val phoneNumber = returnString(detailsJson, PARAM_PHONE_NUMBER)
        val website = returnString(detailsJson, PARAM_WEBPAGE)
        val openedNow = returnString(detailsJson, PARAM_OPENING_HOURS, PARAM_OPEN_NOW)
        val rating = returnDouble(detailsJson, PARAM_RATING)
        val comments = getComments(returnJsonArray(detailsJson, arrayOf(PARAM_COMMENTS)))

        placeDetails = PlaceDetails(
                placeId,
                location,
                placeName,
                photoRef,
                address,
                phoneNumber,
                website,
                openedNow,
                rating,
                comments
        )
    }

    private fun getComments(commentsJsonArray: JsonArray): List<PlaceComment> {
        val list: MutableList<PlaceComment> = mutableListOf()

        for (comment in commentsJsonArray) {
            list.add(PlaceComment(
                    returnString(comment, PARAM_AUTHOR_NAME),
                    returnString(comment, PARAM_TEXT),
                    returnString(comment, PARAM_TIME),
                    returnString(comment, PARAM_PHOTO_URL),
                    returnDouble(comment, PARAM_RATING)
            ))
        }
        return list
    }



    private var placeDetails = EMPTY_PLACE_DETAILS

}
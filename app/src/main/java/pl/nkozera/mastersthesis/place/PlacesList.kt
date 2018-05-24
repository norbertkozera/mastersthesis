/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import android.content.Context
import com.google.gson.*
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_BOOLEAN
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_DOUBLE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_LOCATION_COORDINATES
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_PLACE_DETAILS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.FORMATTED_ADDRESS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.OK
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPEN_NOW
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_AUTHOR_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_COMMENTS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_GEOMETRY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_ICON
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LATITUDE_SHORT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LOCATION
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LONGITUDE_SHORT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_NEXT_PAGE_TOKEN
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_OPENING_HOURS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHONE_NUMBER
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTOS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTO_REFERENCE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTO_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID_GOOGLE_API
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RATING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RESULT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RESULTS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_STATUS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_TEXT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_TIME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_WEBPAGE
import java.util.*


class PlacesList(private val context: Context) {

    fun getPlaces(): LinkedList<Place> {
        return placesList
    }

    fun getCurrentPlaceDetails(): PlaceDetails {
        return placeDetails
    }

    fun findPlaces(city: String) {
        var nextPageToken = EMPTY_STRING
        placesList = LinkedList()
        do {
            Thread.sleep(4000)
            val response = ApiRequest(context).findInCity(city, nextPageToken)
            val jsonObject = Gson().fromJson(response, JsonObject::class.java)
            nextPageToken = when {
                jsonObject.get(PARAM_NEXT_PAGE_TOKEN) == null -> EMPTY_STRING
                else -> jsonObject.get(PARAM_NEXT_PAGE_TOKEN).asString
            }
            if (OK == jsonObject.get(PARAM_STATUS).asString) {
                addToPlaceList(jsonObject.get(PARAM_RESULTS).asJsonArray)
            }
        } while (!nextPageToken.isEmpty())
    }

    fun findDetails(placeId: String) {
        val response = ApiRequest(context).placeDetails(placeId)
        val jsonObject = Gson().fromJson(response, JsonObject::class.java)
        if (OK == jsonObject.get(PARAM_STATUS).asString) {
            createPlaceDetails(jsonObject.get(PARAM_RESULT))
        }
    }

    private fun createPlaceDetails(detailsJson: JsonElement) {
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

    private fun addToPlaceList(results: JsonArray) {
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

    private fun addToPlaceList(userLocation: LocationCoordinates, distance: String, results: JsonArray) {
        val jsonArray = Gson().fromJson(results, JsonArray::class.java)
        for (i in 0 until jsonArray.size()) {

            val restaurantLocation: LocationCoordinates = returnLocationCoordinates(jsonArray.get(i), PARAM_GEOMETRY, PARAM_LOCATION)
            if (Distance().getDistance(userLocation, restaurantLocation) <= (distance.toDouble() / 1000)) {

                val placeId = returnString(jsonArray.get(i), PARAM_PLACE_ID_GOOGLE_API)
                val placeName = returnString(jsonArray.get(i), PARAM_NAME)
                val address = returnString(jsonArray.get(i), FORMATTED_ADDRESS)
                val iconUri = returnString(jsonArray.get(i), PARAM_ICON)
                val openedNow = returnString(jsonArray.get(i), PARAM_OPENING_HOURS, PARAM_OPEN_NOW)
                val rating = returnDouble(jsonArray.get(i), PARAM_RATING)

                placesList.add(Place(
                        placeId,
                        restaurantLocation,
                        placeName,
                        address,
                        iconUri,
                        openedNow,
                        rating
                ))
            }
        }
    }

    private fun returnBoolean(obj: JsonElement, vararg names: String): Boolean {
        val jPrimitive = returnJsonPrimitive(obj, names)
        return try {
            jPrimitive.asBoolean
        } catch (e: Exception) {
            DEFAULT_BOOLEAN
        }
    }

    private fun returnLocationCoordinates(obj: JsonElement, vararg elements: String): LocationCoordinates {
        val jObject = returnJsonObject(obj, elements)
        return try {
            LocationCoordinates(jObject.get(PARAM_LATITUDE_SHORT).asDouble, jObject.get(PARAM_LONGITUDE_SHORT).asDouble)
        } catch (e: Exception) {
            EMPTY_LOCATION_COORDINATES
        }
    }

    private fun returnString(obj: JsonElement, vararg names: String): String {
        val jPrimitive = returnJsonPrimitive(obj, names)
        return try {
            jPrimitive.asString
        } catch (e: Exception) {
            EMPTY_STRING
        }
    }

    private fun returnDouble(obj: JsonElement, vararg names: String): Double {
        val jPrimitive = returnJsonPrimitive(obj, names)
        return try {
            jPrimitive.asDouble
        } catch (e: Exception) {
            DEFAULT_DOUBLE
        }
    }

    private fun returnJsonPrimitive(obj: JsonElement, names: Array<out String>): JsonPrimitive {
        var jObj = obj
        for (name in names) {
            jObj = try {
                jObj.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                if (jObj.isJsonObject && jObj.asJsonObject.get(name) == null) {
                    JsonPrimitive(EMPTY_STRING)
                } else {
                    JsonPrimitive(EMPTY_STRING)
                }

            }
        }
        return jObj.asJsonPrimitive
    }

    private fun returnJsonObject(obj: JsonElement, names: Array<out String>): JsonObject {
        var jObj = obj
        for (name in names) {
            jObj = try {
                jObj.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                JsonObject()
            }
        }
        return jObj.asJsonObject
    }

    private fun returnJsonArray(obj: JsonElement, names: Array<out String>): JsonArray {
        var jArr = obj
        for (name in names) {
            jArr = try {
                jArr.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                JsonArray()
            }
        }
        return jArr.asJsonArray
    }

    fun findPlaces(location: LocationCoordinates, distance: String) {//: List<Place>{
        var nextPageToken = EMPTY_STRING
        placesList = LinkedList()
        do {
            Thread.sleep(4000)
            val response = ApiRequest(context).findNear(location, distance, nextPageToken)
            val jsonObject = Gson().fromJson(response, JsonObject::class.java)
            nextPageToken = when {
                jsonObject.get(PARAM_NEXT_PAGE_TOKEN) == null -> EMPTY_STRING
                else -> jsonObject.get(PARAM_NEXT_PAGE_TOKEN).asString
            }
            if (OK == jsonObject.get(PARAM_STATUS).asString) {
                addToPlaceList(location, distance, jsonObject.get(PARAM_RESULTS).asJsonArray)
            }
        } while (!nextPageToken.isEmpty())
    }

    private var placesList = LinkedList<Place>()
    private var placeDetails = EMPTY_PLACE_DETAILS

}
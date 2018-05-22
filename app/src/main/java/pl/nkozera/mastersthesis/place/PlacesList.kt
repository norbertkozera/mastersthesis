/*
 * Master Thiesis project
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
import java.util.*


class PlacesList(private val context: Context) {

    private var placesList = LinkedList<Place>()
    private var placeDetails = EMPTY_PLACE_DETAILS

    fun getPlaces(): LinkedList<Place> {
        return placesList
    }

    fun getCurrentPlaceDetails(): PlaceDetails {
        return placeDetails
    }

    fun findPlaces(city: String) {//: List<Place>{
        var nextPageToken = ""
        placesList = LinkedList<Place>()
        do {
            Thread.sleep(4000)
            val response = ApiRequest(context).findInCity(city, nextPageToken)
            val jsonObject = Gson().fromJson(response, JsonObject::class.java)
            nextPageToken = when {
                jsonObject.get("next_page_token") == null -> ""
                else -> jsonObject.get("next_page_token").asString
            }
            if ("OK".equals(jsonObject.get("status").asString)) {
                addToPlaceList(jsonObject.get("results").asJsonArray)
            }


        } while (!nextPageToken.isEmpty())

        print("")
    }


    fun findDetails(placeId: String) {//: List<Place>{
        val response = ApiRequest(context).placeDetails(placeId)
        val jsonObject = Gson().fromJson(response, JsonObject::class.java)
        if ("OK".equals(jsonObject.get("status").asString)) {
            createPlaceDetails(jsonObject.get("result"))
        }
    }

    private fun createPlaceDetails(detailsJson: JsonElement) {
        //  val jsonArray = Gson().fromJson(detailsJson, JsonArray::class.java)

        print("")

        returnJsonArray(detailsJson, arrayOf("photos"))
        val placeId = returnString(detailsJson, "place_id")
        val location = returnLocationCoordinates(detailsJson, "geometry", "location")
        val placeName = returnString(detailsJson, "name")
        val photoRef = returnString(returnJsonArray(detailsJson, arrayOf("photos")).get(0), "photo_reference")
        val address = returnString(detailsJson, "formatted_address")
        val phoneNumber = returnString(detailsJson, "international_phone_number")
        val website = returnString(detailsJson, "website")
        val openedNow = returnString(detailsJson, "opening_hours", "open_now")
        val rating = returnDouble(detailsJson, "rating")
        val comments = getComments(returnJsonArray(detailsJson, arrayOf("reviews")))

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
                    returnString(comment, "author_name"),
                    returnString(comment, "text"),
                    returnString(comment, "relative_time_description"),
                    returnString(comment, "profile_photo_url"),
                    returnDouble(comment, "rating")
            ))
        }
        return list
    }


    private fun addToPlaceList(results: JsonArray) {
        val jsonArray = Gson().fromJson(results, JsonArray::class.java)
        for (i in 0 until jsonArray.size()) {
            val placeId = returnString(jsonArray.get(i), "place_id")
            val location: LocationCoordinates = returnLocationCoordinates(jsonArray.get(i), "geometry", "location")
            val placeName = returnString(jsonArray.get(i), "name")
            val address = returnString(jsonArray.get(i), "formatted_address")
            val iconUri = returnString(jsonArray.get(i), "icon")
            val openedNow = returnString(jsonArray.get(i), "opening_hours", "open_now")
            val rating = returnDouble(jsonArray.get(i), "rating")

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

            val restaurantLocation: LocationCoordinates = returnLocationCoordinates(jsonArray.get(i), "geometry", "location")
            if (Distance().getDistance(userLocation, restaurantLocation) <= (distance.toDouble() / 1000)) {

                val placeId = returnString(jsonArray.get(i), "place_id")
                val placeName = returnString(jsonArray.get(i), "name")
                val address = returnString(jsonArray.get(i), "formatted_address")
                val iconUri = returnString(jsonArray.get(i), "icon")
                val openedNow = returnString(jsonArray.get(i), "opening_hours", "open_now")
                val rating = returnDouble(jsonArray.get(i), "rating")

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
        print("")
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
            LocationCoordinates(jObject.get("lat").asDouble, jObject.get("lng").asDouble)
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
        for (name in names) {//TODO ERROR
            jObj = try {
                jObj.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                if (jObj.isJsonObject && jObj.asJsonObject.get(name) == null) {
                    JsonPrimitive("")
                } else {
                    JsonPrimitive("")
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
        var nextPageToken = ""
        placesList = LinkedList<Place>()
        do {
            Thread.sleep(4000)
            val response = ApiRequest(context).findNear(location, distance, nextPageToken)
            val jsonObject = Gson().fromJson(response, JsonObject::class.java)
            nextPageToken = when {
                jsonObject.get("next_page_token") == null -> ""
                else -> jsonObject.get("next_page_token").asString
            }
            if ("OK".equals(jsonObject.get("status").asString)) {
                addToPlaceList(location, distance, jsonObject.get("results").asJsonArray)
            }


        } while (!nextPageToken.isEmpty())

        print("")
    }

}
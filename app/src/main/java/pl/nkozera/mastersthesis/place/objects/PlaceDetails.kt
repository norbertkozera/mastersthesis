/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis.place.objects

import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_DOUBLE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_LOCATION_COORDINATES
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.location.LocationCoordinates

class PlaceDetails(
        private val placeId: String,
        private val location: LocationCoordinates,
        private val placeName: String,
        private val photoRef: String,
        private val formattedAddress: String,
        private val phoneNumber: String,
        private val website: String,
        private val openedNow: String,
        private val rating: Double,
        private val comments: List<PlaceComment>) {


    fun getPlaceId(): String {
        return placeId
    }

    fun getLocation(): LocationCoordinates {
        return location
    }

    fun getPlaceName(): String {
        return placeName
    }

    fun getOpenedNow(): String {
        return openedNow
    }

    fun getRating(): Double {
        return rating
    }

    fun getPhotoRef(): String {
        return photoRef
    }

    fun getAddress(): String {
        return formattedAddress
    }

    fun getPhoneNumber(): String {
        return phoneNumber
    }

    fun getWebsite(): String {
        return website
    }

    fun getComments(): List<PlaceComment> {
        return comments
    }

    companion object {
        fun emptyPlace(): PlaceDetails {
            return PlaceDetails(EMPTY_STRING, EMPTY_LOCATION_COORDINATES, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, DEFAULT_DOUBLE, emptyList())
        }
    }

}
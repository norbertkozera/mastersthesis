/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

class Place(
        private val placeId: String,
        private val location: LocationCoordinates,
        private val placeName: String,
        private val address: String,
        private val iconUri: String,
        private val openedNow: Boolean,
        private val rating: Double) {


    fun getPlaceId(): String {
        return placeId
    }

    fun getLocation(): LocationCoordinates {
        return location
    }

    fun getPlaceName(): String {
        return placeName
    }

    fun getAddress(): String {
        return address
    }

    fun getIconUri(): String {
        return iconUri
    }

    fun getOpenedNow(): Boolean {
        return openedNow
    }

    fun getRating(): Double {
        return rating
    }
}
/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis.location

import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_DOUBLE

class LocationCoordinates(private val latitude: Double, private val longitude: Double) {

    fun getLatitude(): Double {
        return latitude
    }


    fun getLongitude(): Double {
        return longitude
    }

    override fun toString(): String {
        return "$latitude, $longitude"
    }

    fun isEmpty(): Boolean {
        return DEFAULT_DOUBLE.equals(latitude) && DEFAULT_DOUBLE.equals(longitude)
    }

    companion object {
        fun emptyCoordinates(): LocationCoordinates {
            return LocationCoordinates(DEFAULT_DOUBLE, DEFAULT_DOUBLE)
        }
    }

}
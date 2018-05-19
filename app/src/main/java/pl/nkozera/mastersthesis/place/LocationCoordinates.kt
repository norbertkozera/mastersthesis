/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

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

}
/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

class LocationCoordinates(private val latitude: Double, private val longitude: Double) {

    fun getLatitude(): Double{
        return latitude
    }


    fun getLongitude(): Double{
        return longitude
    }

    override fun toString(): String {
        return "$latitude, $longitude"
    }


}
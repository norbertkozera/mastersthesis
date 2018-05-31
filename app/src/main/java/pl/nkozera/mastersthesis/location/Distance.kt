/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis.location

import java.text.DecimalFormat

class Distance {

    fun getDistance(firstLocation: LocationCoordinates, secondLocation: LocationCoordinates): Double {
        //https://www.movable-type.co.uk/scripts/latlong.html
        val R = 6371e3 // Earth’s mean radius in meter
        val φ1 = Math.toRadians(firstLocation.getLatitude())
        val φ2 = Math.toRadians(secondLocation.getLatitude())
        val Δφ = Math.toRadians(secondLocation.getLatitude() - firstLocation.getLatitude())
        val Δλ = Math.toRadians(secondLocation.getLongitude() - firstLocation.getLongitude())

        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val d = (R * c) / 1000
        return DecimalFormat("#.##").format(d).replace(",", ".").toDouble()
    }

}
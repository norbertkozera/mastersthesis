/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import android.util.ArrayMap
import pl.nkozera.mastersthesis.location.Distance
import pl.nkozera.mastersthesis.location.LocationCoordinates
import pl.nkozera.mastersthesis.place.objects.Place
import java.util.*

//not a real cache but will help with development
class PlacesCache {

    companion object {
        private var placesInCity = ArrayMap<String, LinkedList<Place>>()

        fun addPlacesInCityToCache(cityName: String, places: LinkedList<Place>) {
            if (placesInCity[cityName] == null) {
                placesInCity[cityName] = places
            }
        }

        fun getPlacesInCityFromCache(cityName: String): LinkedList<Place>? {
            return placesInCity[cityName]
        }


        private var placesInLocation = ArrayMap<LocationCoordinates, ArrayMap<String, LinkedList<Place>>>()

        fun addPlacesInLocation(location: LocationCoordinates, distance: String, places: LinkedList<Place>) {
            if (placesInLocation[location] == null) {
                val locations = ArrayMap<String, LinkedList<Place>>()
                locations[distance] = places
                placesInLocation[location] = locations
            }
        }

        fun getPlacesInLocation(location: LocationCoordinates, distance: String): LinkedList<Place>? {
            val places = LinkedList<Place>()
            if (placesInLocation.size > 0) {
                for (i in 0 until placesInLocation.size) {
                    val currentCheckLocation = placesInLocation.keyAt(i)
                    if (Distance().getDistance(location, currentCheckLocation) <= 0.1) {
                        val element = placesInLocation[currentCheckLocation]!!
                        val key = element.keyAt(0)
                        if(Math.abs(distance.toDouble() - key.toDouble()) < 100) {
                            element[key]?.let { places.addAll(it) }
                        }
                    }
                }
            }

            return places
        }
    }
}
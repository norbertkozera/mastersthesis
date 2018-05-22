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

package pl.nkozera.mastersthesis.base

import com.google.api.client.http.GenericUrl
import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.PlaceDetails
import java.net.URI

class BaseValues {
    companion object {
        const val DEFAULT_DOUBLE = 0.0
        const val EMPTY_STRING = ""
        val EMPTY_URI = URI("")
        const val DEFAULT_BOOLEAN = false
        val EMPTY_LOCATION_COORDINATES = LocationCoordinates.emptyCoordinates()
        val EMPTY_PLACE_DETAILS = PlaceDetails.emptyPlace()
        val DETAILS_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/details/json?language=pl")
        val TEXTSEARCH_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?")
        val PHOTO_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/photo?")


    }
}
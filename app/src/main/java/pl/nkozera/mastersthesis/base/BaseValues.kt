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

import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.PlaceDetails

class BaseValues {
    companion object {
        const val DEFAULT_DOUBLE = 0.0
        const val DEFAULT_STRING = ""
        const val DEFAULT_BOOLEAN = false
        val EMPTY_LOCATION_COORDINATES = LocationCoordinates.emptyCoordinates()
        val EMPTY_PLACE_DETAILS = PlaceDetails.emptyPlace()
    }
}
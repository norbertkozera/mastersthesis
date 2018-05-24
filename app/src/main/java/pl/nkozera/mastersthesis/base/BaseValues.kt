/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.base

import android.util.ArrayMap
import com.google.api.client.http.GenericUrl
import pl.nkozera.mastersthesis.place.LocationCoordinates
import pl.nkozera.mastersthesis.place.PlaceDetails

class BaseValues {
    companion object {
        const val DEFAULT_DOUBLE = 0.0
        const val DEFAULT_INT = 0
        const val EMPTY_STRING = ""
        const val SPACE = " "
        const val OK = "OK"
        const val DEFAULT_BOOLEAN = false
        const val GOOGLE_MAPS_URL = "http://maps.google.com/maps?"
        const val GOOGLE_MAPS_DIRECT_TO = "daddr="
        const val HTML_TEL = "tel:"


        const val PARAM_PLACE_ID = "placeId"
        const val PARAM_LONGITUDE = "longitude"
        const val PARAM_LONGITUDE_SHORT = "lng"
        const val PARAM_LATITUDE = "latitude"
        const val PARAM_LATITUDE_SHORT = "lat"
        const val PARAM_CITY = "city"
        const val PARAM_DISTANCE = "distance"
        const val PARAM_EMAIL = "email"
        const val PARAM_PUBLIC_PROFILE = "public_profile"
        const val PARAM_NEXT_PAGE_TOKEN = "next_page_token"
        const val PARAM_STATUS = "status"
        const val PARAM_RESULTS = "results"
        const val PARAM_RESULT = "result"
        const val PARAM_AUTHOR_NAME = "author_name"
        const val PARAM_TEXT = "text"
        const val PARAM_TIME = "relative_time_description"
        const val PARAM_PHOTO_URL = "profile_photo_url"
        const val PARAM_RATING = "rating"
        const val PARAM_PLACE_ID_GOOGLE_API = "place_id"
        const val PARAM_GEOMETRY = "geometry"
        const val PARAM_LOCATION = "location"
        const val PARAM_NAME = "name"
        const val FORMATTED_ADDRESS = "formatted_address"
        const val PARAM_ICON = "icon"
        const val PARAM_OPENING_HOURS = "opening_hours"
        const val PARAM_OPEN_NOW = "open_now"
        const val PARAM_PHOTOS = "photos"
        const val PARAM_PHOTO_REFERENCE = "photo_reference"
        const val PARAM_PHONE_NUMBER = "international_phone_number"
        const val PARAM_WEBPAGE = "website"
        const val PARAM_COMMENTS = "reviews"
        const val PARAM_KEY = "key"
        const val PARAM_PLACE_TYPE = "restaurant"
        const val PARAM_QUERY = "query"
        const val PARAM_TYPES = "types"
        const val PARAM_PAGETOKEN = "pagetoken"


        const val PARAM_RADIUS = "radius"
        const val PARAM_PHOTOREFERENCE = "photoreference"
        const val PARAM_MAXWIDTH = "maxwidth"
        const val PARAM_MAXHEIGHT = "maxheight"

        const val FIREBASE_AVATARS_PATH = "avatars/"


        const val UNUSED_PARAMETER = "UNUSED_PARAMETER"
        const val PWD = "password"


        val EMPTY_LOCATION_COORDINATES = LocationCoordinates.emptyCoordinates()
        val EMPTY_PLACE_DETAILS = PlaceDetails.emptyPlace()
        val DETAILS_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/details/json?language=pl")
        val TEXTSEARCH_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?")
        val PHOTO_GENERIC_URL = GenericUrl("https://maps.googleapis.com/maps/api/place/photo?")
    }
}
/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis.place.asynctasks

import android.content.Context
import android.util.ArrayMap
import android.widget.Toast
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.javanet.NetHttpTransport
import pl.nkozera.mastersthesis.R
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DETAILS_GENERIC_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_KEY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_LOCATION
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_MAXHEIGHT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_MAXWIDTH
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PAGETOKEN
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PHOTOREFERENCE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_TYPE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_QUERY
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RADIUS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_TYPES
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PHOTO_GENERIC_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.SPACE
import pl.nkozera.mastersthesis.base.BaseValues.Companion.TEXTSEARCH_GENERIC_URL
import pl.nkozera.mastersthesis.location.LocationCoordinates


class GenerateUrl(val context: Context) {


    private fun findInCity(city: String, nextPageToken: String): String {
        val params = ArrayMap<String, String>()
        params[PARAM_QUERY] = "$PARAM_PLACE_TYPE-${cityReplacement(city)}"
        params[PARAM_TYPES] = PARAM_PLACE_TYPE
        if (!nextPageToken.isEmpty()) {
            params[PARAM_PAGETOKEN] = nextPageToken
        }

        return requestUrl(request(TEXTSEARCH_GENERIC_URL, params))
    }

    private fun findNear(location: LocationCoordinates, distance: String, nextPageToken: String): String {
        val params = ArrayMap<String, String>()
        params[PARAM_LOCATION] = location.toString()
        params[PARAM_RADIUS] = distance
        params[PARAM_TYPES] = PARAM_PLACE_TYPE
        if (!nextPageToken.isEmpty()) {
            params[PARAM_PAGETOKEN] = nextPageToken
        }

        return requestUrl(request(TEXTSEARCH_GENERIC_URL, params))
    }


    fun placePhoto(width: Int, heigth: Int, photoReference: String): String {
        val params = ArrayMap<String, String>()
        params[PARAM_PHOTOREFERENCE] = photoReference
        params[PARAM_MAXWIDTH] = width.toString()
        params[PARAM_MAXHEIGHT] = heigth.toString()
        return requestUrl(request(PHOTO_GENERIC_URL, params))
    }

    private fun request(url: GenericUrl, parameters: Map<String, String>): HttpRequest {

        val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
        val request = httpRequestFactory.buildGetRequest(url)
        request.url[PARAM_KEY] = API_KEY
        for (param in parameters) {
            request.url[param.key] = param.value
        }
        return request
    }


    private fun requestUrl(request: HttpRequest?): String {
        return try {
            request!!.url.toString()
        } catch (e: Exception) {
            EMPTY_STRING
        }
    }

    private fun cityReplacement(city: String): String {
        return city.replace(SPACE, "-")
    }


    fun findDetailsUrl(placeId: String): String {

        val params = ArrayMap<String, String>()
        params[PARAM_PLACE_ID] = placeId
        return requestUrl(request(DETAILS_GENERIC_URL, params))


    }

    fun findPlacesUrl(location: LocationCoordinates, distance: String, city: String, nextPageToken: String): String {
        when {
            !distance.isEmpty() -> {
                if (location.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.location_not_allowed), Toast.LENGTH_LONG).show()
                } else {
                    return findNear(location, distance, nextPageToken)
                }
            }
            !city.isEmpty() -> {
                return findInCity(city, nextPageToken)
            }
            else -> {
                Toast.makeText(context, context.getString(R.string.error_no_search_data), Toast.LENGTH_SHORT).show()
            }
        }
        return ""
    }

    private val API_KEY = context.getString(R.string.google_places_api_key)
}

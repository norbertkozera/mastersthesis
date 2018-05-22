/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import android.content.Context
import android.os.StrictMode
import android.util.ArrayMap
import com.google.api.client.http.*
import com.google.api.client.http.javanet.NetHttpTransport
import pl.nkozera.mastersthesis.R
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DETAILS_GENERIC_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PHOTO_GENERIC_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.TEXTSEARCH_GENERIC_URL


class ApiRequest(context: Context) {

    fun findInCity(city: String, nextPageToken: String): String {
        val params = ArrayMap<String, String>()
        params["query"] = "restaurant-${cityReplacement(city)}"
        params["types"] = "restaurant"
        if (!nextPageToken.isEmpty()) {
            params["pagetoken"] = nextPageToken
        }

        return stringResponse(request(TEXTSEARCH_GENERIC_URL, params))
    }

     fun findNear(location: LocationCoordinates, distance: String, nextPageToken: String): String {
        val params = ArrayMap<String, String>()
        params["location"] = location.toString()
        params["radius"] = distance
        params["types"] = "restaurant"
        if (!nextPageToken.isEmpty()) {
            params["pagetoken"] = nextPageToken
        }

        return stringResponse(request(TEXTSEARCH_GENERIC_URL, params))
    }

    fun placeDetails(placeId: String): String {
        val params = ArrayMap<String, String>()
        params["placeid"] = placeId

        return stringResponse(request(DETAILS_GENERIC_URL, params))
    }

    fun placePhoto(width: Int, heigth: Int, photoReference: String): String {


        val params = ArrayMap<String, String>()
        params["photoreference"] = photoReference
        params["maxwidth"] = width.toString()
        params["maxheight"] = heigth.toString()

        print("")
        return requestUrl(request(PHOTO_GENERIC_URL, params))
    }

    private fun request(url: GenericUrl, parameters: Map<String, String>): HttpRequest? {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
            val request = httpRequestFactory.buildGetRequest(url)
            request.url["key"] = API_KEY
            for (param in parameters) {
                request.url[param.key] = param.value
            }
        print("")
            return request

    }

    private fun stringResponse(request: HttpRequest?): String {
        return try {
            request!!.execute().parseAsString()
        } catch(e:Exception){
            EMPTY_STRING
        }
    }

    private fun requestUrl(request: HttpRequest?): String {
        return try {
            request!!.url.toString()
        } catch(e:Exception){
            EMPTY_STRING
        }
    }

    private fun cityReplacement(city: String): String {
        return city.replace(" ", "-")
    }

    private val API_KEY = context.getString(R.string.google_places_api_key)
}

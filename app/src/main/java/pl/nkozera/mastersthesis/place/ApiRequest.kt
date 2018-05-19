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

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import android.content.Context
import android.os.StrictMode
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import pl.nkozera.mastersthesis.R


class ApiRequest(context: Context) {

    //https://github.com/vmlinz/stackoverflow-android-top100-faqs/wiki/001-how-to-fix-android-os-network-on-mainthread-exception

    private val API_KEY = context.getString(R.string.google_places_api_key)

    fun findInCity(city: String, nextPageToken: String): String {

        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
            val request = httpRequestFactory
                    .buildGetRequest(GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?"))
            request.url["key"] = API_KEY
            request.url["query"] = "restaurant-${cityReplacement(city)}"
            request.url["types"] = "restaurant"
            if (!nextPageToken.isEmpty()) {
                request.url["pagetoken"] = nextPageToken
            }

            val response = request.execute()
            return response.parseAsString()

        } catch (e: HttpResponseException) {
            return ""
        }

    }


    fun findNear(location: LocationCoordinates, distance: String, nextPageToken: String): String {
//https://maps.googleapis.com/maps/api/place/textsearch/json?
// query=123+main+street&
// location=42.3675294,-71.186966&
// radius=10000&key=YOUR_API_KEY


        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
            val request = httpRequestFactory
                    .buildGetRequest(GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?"))
            request.url["key"] = API_KEY
            request.url["location"] = location.toString()
            request.url["radius"] = distance
            request.url["types"] = "restaurant"
            if (!nextPageToken.isEmpty()) {
                request.url["pagetoken"] = nextPageToken
            }

            val response = request.execute()
            return response.parseAsString()

        } catch (e: HttpResponseException) {
            return ""
        }

    }


    fun placeDetails(placeId: String): String {
// https://maps.googleapis.com/maps/api/place/details/json?
// placeid=ChIJlfnS8hP5FkcRXLSPkgOhLX8
// key=AIzaSyAny_oMpGWlwG0Mfwcieb0aWZQovIya7_0


        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
            val request = httpRequestFactory
                    .buildGetRequest(GenericUrl("https://maps.googleapis.com/maps/api/place/details/json?"))
            request.url["key"] = API_KEY
            request.url["placeid"] = placeId


            val response = request.execute()
            return response.parseAsString()

        } catch (e: HttpResponseException) {
            return ""
        }

    }


    private fun cityReplacement(city: String): String {
        return city.replace(" ", "-")
    }
}

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


class ApiRequest(private val context: Context) {

    //https://github.com/vmlinz/stackoverflow-android-top100-faqs/wiki/001-how-to-fix-android-os-network-on-mainthread-exception

    val API_KEY = context.getString(R.string.google_places_api_key)

    fun search(city: String, nextPageToken: String): String {

        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
            val request = httpRequestFactory
                    .buildGetRequest(GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?"))
            request.url.put("key", API_KEY)
            request.url.put("query", "restaurant-${cityReplacement(city)}")
            request.url.put("types", "restaurant")
            if (!nextPageToken.isEmpty()) {
                request.url.put("pagetoken", nextPageToken)
            }

            val response = request.execute()
            return response.parseAsString()

        } catch (e: HttpResponseException) {
            return ""
        }

    }

    private fun cityReplacement(city: String): String {
        return city.replace(" ", "-")
    }

//    private fun fullCityRequestName(city: String): String {
//        if (city.contains(" ")) {
//            cityChange(city)
//        } else {
//            return city
//        }
//
//    }
//
//    private fun cityChange(city: String) {
//        fullCityRequestName(city.replace(" ", "-"))
//    }


//
//
//
//
//
//
//    fun request(context: Context, url: String) {
//        val queue = Volley.newRequestQueue(context)
//
//        val stringRequest = StringRequest(Request.Method.GET, url,
//                Response.Listener<String> { response ->
//                    //                    PlacesList().ceateNewLists()
//                    PlacesListsss().addToList(response, context, url)
//                },
//                Response.ErrorListener {
//                    //                    makeToast(getString(R.string.error_search))
////                    startActivity(Intent(this, FindCityActivity::class.java))
////                    finish()
//                    //TODO LOG IT
//                    PlacesListsss().addToList("")
//                }
//        )
//        queue.add(stringRequest)
//    }
//
//
//
//    fun search(coordinates: LocationCoordinates, city: String):  PlaceList{
//        //url.append("https://maps.googleapis.com/maps/api/place/textsearch/json?key=$API_KEY&query=restaurant")
////https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.20.0/com/google/api/client/http/HttpTransport
//        //https://www.programcreek.com/java-api-examples/?api=com.google.api.client.http.HttpRequestFactory
//        //https://stackoverflow.com/questions/26765423/not-regonize-the-createrequestfactory-method-for-the-type-httptransport/26786063#26786063
//        try {
////todo problem :(
////            val transport = ApacheHttpTransport()
////            val HTTP_TRANSPORT = UrlFetchTransport()
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
//            val httpRequestFactory: HttpRequestFactory = NetHttpTransport().createRequestFactory()
//            val request = httpRequestFactory
//                    .buildGetRequest(GenericUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?"))
//            request.getUrl().put("key", API_KEY)
//            //  request.getUrl().put("location", coordinates.toString())
//            request.getUrl().put("query", "restaurant-$city")
//            request.getUrl().put("types", "restaurant")
//
//
//            print("")
//
//            var list = PlaceList()
//            val response = request.execute()
//
//            print(".parseAs(PlacesList::class.java)")
//
////            if (list.next_page_token != null || list.next_page_token !== "") {
////                Thread.sleep(4000)
////                /*Since the token can be used after a short time it has been  generated*/
////                request.getUrl().put("pagetoken", list.next_page_token)
////                val temp = request.execute().parseAs(PlacesList::class.java)
////                list.results.addAll(temp.results)
////
////                if (temp.next_page_token != null || temp.next_page_token !== "") {
////                    Thread.sleep(4000)
////                    request.getUrl().put("pagetoken", temp.next_page_token)
////                    val tempList = request.execute().parseAs(PlacesList::class.java)
////                    list.results.addAll(tempList.results)
////                }
////
////            }
//            return list
//
//        } catch (e: HttpResponseException) {
//            return PlaceList()
//        }
//
//    }
}

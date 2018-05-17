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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class PlacesListsss {


    fun ceateNewLists(jsonString: String) {
        val gson = Gson()
        val json = gson.toJson(jsonString)
        val jsonTree = gson.toJsonTree(jsonString)

        val jobj = Gson().fromJson(jsonString, JsonObject::class.java)
        //val jsonObj = gson.toJson(jsonString)
//            val places = gson.fromJson<String>(jsonString, String.class)

        print("")
    }

    fun addToList(response: String, context: Context?, url: String?) {
        when {
            "".equals(response) -> {
            }
            else -> {
                val jobj = Gson().fromJson(response, JsonObject::class.java)
                val nextPageToken = jobj.get("next_page_token").asString
                val results = jobj.get("results").asJsonArray
                addResults(results)
                if (!nextPageToken.isNullOrEmpty()) {
                    // ApiRequest(context!!).request(context!!, getUrl(url, nextPageToken))
                }
            }
        }


    }

    private fun getUrl(url: String?, nextPageToken: String): String {
        return StringBuilder().append(url).append("&pagetoken=$nextPageToken").toString()

    }

    private fun addResults(results: JsonArray?) {
        print("")
    }

    fun addToList(response: String) {
        addToList(response, null, null)
    }
}
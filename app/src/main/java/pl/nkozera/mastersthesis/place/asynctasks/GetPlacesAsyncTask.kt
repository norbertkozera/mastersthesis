/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place.asynctasks

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonObject
import pl.nkozera.mastersthesis.base.BaseValues
import pl.nkozera.mastersthesis.place.PlacesList
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetPlacesAsyncTask(
        private val places: PlacesList,
        private val firstAsk: Boolean,
        private val listener: OnTaskCompleted
) : AsyncTask<String, String, String>() {


    override fun onPreExecute() {

    }

    override fun doInBackground(vararg urls: String?): String {
        var urlConnection: HttpURLConnection? = null

        if (!firstAsk) {
            Thread.sleep(2000)
        }

        try {
            url = URL(urls[0])

            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
            urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS

            val inString = streamToString(urlConnection.inputStream)

            publishProgress(inString)
        } catch (ex: Exception) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
        }

        return " "
    }

    override fun onProgressUpdate(vararg values: String?) {
        try {
            val jsonObject = Gson().fromJson(values[0], JsonObject::class.java)

            if (jsonObject.get(BaseValues.PARAM_NEXT_PAGE_TOKEN) != null) {
                places.setNextPaheToken(jsonObject.get(BaseValues.PARAM_NEXT_PAGE_TOKEN).asString)
            } else {
                places.setNextPaheToken("")
            }

            if (BaseValues.OK == jsonObject.get(BaseValues.PARAM_STATUS).asString) {
                places.addToPlaceList(jsonObject.get(BaseValues.PARAM_RESULTS).asJsonArray)
            }

        } catch (ex: Exception) {

        }
    }

    override fun onPostExecute(result: String?) {
        listener.onTaskCompleted()
    }


    private fun streamToString(inputStream: InputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }

        return result
    }


    private val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    private lateinit var url: URL


}
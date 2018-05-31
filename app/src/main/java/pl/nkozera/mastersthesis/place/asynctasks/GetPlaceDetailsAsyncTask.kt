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
import pl.nkozera.mastersthesis.place.ChoosenPlaceDetails
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetPlaceDetailsAsyncTask(
        private val container: ChoosenPlaceDetails,
        private val listener: OnTaskCompleted
) : AsyncTask<String, String, String>() {


    override fun onPreExecute() {
        // Before doInBackground
    }

    override fun doInBackground(vararg urls: String?): String {
        var urlConnection: HttpURLConnection? = null

        try {
            val url = URL(urls[0])

            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
            urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS

            val inString = streamToString(urlConnection.inputStream)

            publishProgress(inString)
        } catch (ex: Exception) {
            print("")
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

            if (BaseValues.OK == jsonObject.get(BaseValues.PARAM_STATUS).asString) {
                container.createPlaceDetails(jsonObject.get(BaseValues.PARAM_RESULT))
            }

        } catch (ex: Exception) {

        }
    }

    override fun onPostExecute(result: String?) {
        listener.onTaskCompleted()
    }


    private fun streamToString(inputStream: InputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
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




}
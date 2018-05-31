/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import pl.nkozera.mastersthesis.base.BaseValues
import pl.nkozera.mastersthesis.location.LocationCoordinates

open class Common {

    protected fun returnLocationCoordinates(obj: JsonElement, vararg elements: String): LocationCoordinates {
        val jObject = returnJsonObject(obj, elements)
        return try {
            LocationCoordinates(jObject.get(BaseValues.PARAM_LATITUDE_SHORT).asDouble, jObject.get(BaseValues.PARAM_LONGITUDE_SHORT).asDouble)
        } catch (e: Exception) {
            BaseValues.EMPTY_LOCATION_COORDINATES
        }
    }

    protected fun returnString(obj: JsonElement, vararg names: String): String {
        val jPrimitive = returnJsonPrimitive(obj, names)
        return try {
            jPrimitive.asString
        } catch (e: Exception) {
            BaseValues.EMPTY_STRING
        }
    }

    protected fun returnDouble(obj: JsonElement, vararg names: String): Double {
        val jPrimitive = returnJsonPrimitive(obj, names)
        return try {
            jPrimitive.asDouble
        } catch (e: Exception) {
            BaseValues.DEFAULT_DOUBLE
        }
    }

    protected fun returnJsonPrimitive(obj: JsonElement, names: Array<out String>): JsonPrimitive {
        var jObj = obj
        for (name in names) {
            jObj = try {
                jObj.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                if (jObj.isJsonObject && jObj.asJsonObject.get(name) == null) {
                    JsonPrimitive(BaseValues.EMPTY_STRING)
                } else {
                    JsonPrimitive(BaseValues.EMPTY_STRING)
                }

            }
        }
        return jObj.asJsonPrimitive
    }

    protected fun returnJsonObject(obj: JsonElement, names: Array<out String>): JsonObject {
        var jObj = obj
        for (name in names) {
            jObj = try {
                jObj.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                JsonObject()
            }
        }
        return jObj.asJsonObject
    }

    protected fun returnJsonArray(obj: JsonElement, names: Array<out String>): JsonArray {
        var jArr = obj
        for (name in names) {
            jArr = try {
                jArr.asJsonObject.get(name)
            } catch (e: IllegalStateException) {
                JsonArray()
            }
        }
        return jArr.asJsonArray
    }
}
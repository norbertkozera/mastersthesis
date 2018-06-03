/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import pl.nkozera.mastersthesis.base.BaseValues


class PlacesListTest {

    private lateinit var placesList: PlacesList

    @Before
    fun init() {
        placesList = PlacesList()
    }


    @Test
    fun isCorrectPlaceList() {
        placesList.addToPlaceList(prepareArray())
        assertTrue(placesList.getPlaces()[0].getPlaceId() == "ChIJuaKgPWpbFkcR1BEMavHF11Y")
        assertTrue(placesList.getPlaces()[0].getOpenedNow() == "false")
        assertTrue(placesList.getPlaces()[0].getAddress() == "Józefa 14, 31-056 Kraków, Polska")
    }

    @Test
    fun isIncorrectPlaceList() {
        placesList.addToPlaceList(prepareArray())
        assertFalse(placesList.getPlaces()[0].getPlaceId() == "ChdwdIJuaKgPWpbFkcR1BEMavHF11Y")
        assertFalse(placesList.getPlaces()[0].getOpenedNow() == "true")
        assertFalse(placesList.getPlaces()[0].getAddress() == "Jozefa 14, 31-056 Kraków, Polska")
    }

    fun prepareArray(): JsonArray {
        val parser = JsonParser()
        val o = parser.parse(jsonString).asJsonObject
        return o.get(BaseValues.PARAM_RESULTS).asJsonArray
    }

    @Test
    fun isCorrectPageToken() {
        placesList.setNextPaheToken("test")
        assertEquals(placesList.getNextPageToken(), "test")
    }

    @Test
    fun isIncorrectPageToken() {
        placesList.setNextPaheToken("test")
        assertNotEquals(placesList.getNextPageToken(), "nottest")
    }


    private val jsonString = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"next_page_token\" : \"CuQD0wEAAID36QJT1_KRXesE9E3W99V8PZlTKi43K1ZLbBUIJMg9Gyp2EHsX08kQSZD75VgMNJAQwId-rkdwfkpFF6xN26FxmA-WMOJAfXEGTKqjEPSslsPhxIqALnq0G9Nou429TozSe0-bajjeMBEsVAcommAXwBGuHeYGSDjiQXWLBsOLPgM4O4h60ynvKcH_Andoao3h-jDvVVaWkQSnV6YlhUO3YUQbv-URoWyUVC-0F_HpTNTL-4cF1QqfSsrDqXUIj6X55LrByhT417WHQb1qddToNfVkn3KgOzYyL18O1DJnPJI6Zub8eFxbOuaqBhQCKKLxs9FdVas0BDrJFv8x5WJb3Q8d0Rad_p2PZlJeN-6adVVi6EwTtvEfvSUBjt7m8jDO3tMndAuG2J-NExL-MUBoAo5QjDcJ2WqKUXiL9OIN-Av4dbV9Y5vgFNfULXmGxLD4FbE0_JIw7DXOqIZs0stHfEsKOWOfEcTH2uvywppNfWx2JrefNLNNDg2operaJZhsvc4Z7zc1rzBNBWp8epW6gtn7WATh29zzKGj7Nv18GSDtF8gV2LpVoKyD072FOKKGaaMH0Rxq6MRSBMhEREwivpSc57Xam_RxW0mCnh3O3ovG7jGNkNaWjmhdFhvB1BIQYNP3mnrXXUZy2iuYpc8ExRoUwYYe58wfrtApDmDdif4xh8zXXHI\",\n" +
            "   \"results\" : [\n" +
            "      {\n" +
            "         \"formatted_address\" : \"Józefa 14, 31-056 Kraków, Polska\",\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 50.05062299999999,\n" +
            "               \"lng\" : 19.9444188\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 50.05199087989272,\n" +
            "                  \"lng\" : 19.94575982989272\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 50.04929122010728,\n" +
            "                  \"lng\" : 19.94306017010728\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"24d6031ed5243f1c9627f9f3a37989ec3e584951\",\n" +
            "         \"name\" : \"Starka\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : false,\n" +
            "            \"weekday_text\" : []\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 3120,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117317344038593744956/photos\\\"\\u003eA Google User\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAnfVQLz_CC508YVMCzGg7aA6SqdisUFbsj39QggtKq1_DKPaXaZMxpgiKKKU7hzJ8kkbIebtdvZA_WTFv8vV4zGuq4uLkp0bvAJWZoTENIE8l_W5dhpRh8nGCaUnYQGynEhAfoDizpjNHxfGOkWvsPF1IGhTnLNIEqBX8ObAGoorj0Jl22ETwMA\",\n" +
            "               \"width\" : 4160\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJuaKgPWpbFkcR1BEMavHF11Y\",\n" +
            "         \"rating\" : 4.6,\n" +
            "         \"reference\" : \"CmRbAAAAxLQYJ-gBbSNYgSQ8YIYlxxeUxi8LRDy5fy2U0fNIFa__D4Un9N5Hs4xZOUlQ4Jgjnz_QzgK1avFBXsCVgDb6-ah4b1dc3z0OEJ1pFV4jr_GEcQOYZd4dy0O8Ieu6pZNLEhAmCr8-Say-hY25HdD5PqKJGhQ42qCuJO2KMIwy7nvekkSaYXe1zw\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ]\n" +
            "      } \n" +
            "\t],\n" +
            "   \"status\" : \"OK\"\n" +
            "}"

}
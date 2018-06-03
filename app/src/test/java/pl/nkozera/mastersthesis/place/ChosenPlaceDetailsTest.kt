/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import pl.nkozera.mastersthesis.base.BaseValues

class ChosenPlaceDetailsTest {


    private lateinit var placeDetails: ChosenPlaceDetails

    @Before
    fun init() {
        placeDetails = ChosenPlaceDetails()
    }


    @Test
    fun isCorrect() {
        placeDetails.createPlaceDetails(prepareObject().get(BaseValues.PARAM_RESULT))
        assertTrue(placeDetails.getCurrentPlaceDetails().getOpenedNow() == "false")
    }

    @Test
    fun isIncorrect() {
        placeDetails.createPlaceDetails(prepareObject().get(BaseValues.PARAM_RESULT))
        assertFalse(placeDetails.getCurrentPlaceDetails().getOpenedNow() == "true")
    }


    fun prepareObject(): JsonObject {
        val parser = JsonParser()
        return parser.parse(jsonString).asJsonObject
    }

    val jsonString = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"result\" : {\n" +
            "      \"address_components\" : [\n" +
            "         {\n" +
            "            \"long_name\" : \"14\",\n" +
            "            \"short_name\" : \"14\",\n" +
            "            \"types\" : [ \"street_number\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"Józefa\",\n" +
            "            \"short_name\" : \"Józefa\",\n" +
            "            \"types\" : [ \"route\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"Stare Miasto\",\n" +
            "            \"short_name\" : \"Stare Miasto\",\n" +
            "            \"types\" : [ \"sublocality_level_1\", \"sublocality\", \"political\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"Kraków\",\n" +
            "            \"short_name\" : \"Kraków\",\n" +
            "            \"types\" : [ \"locality\", \"political\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"Kraków\",\n" +
            "            \"short_name\" : \"Kraków\",\n" +
            "            \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"małopolskie\",\n" +
            "            \"short_name\" : \"małopolskie\",\n" +
            "            \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"Polska\",\n" +
            "            \"short_name\" : \"PL\",\n" +
            "            \"types\" : [ \"country\", \"political\" ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"long_name\" : \"31-056\",\n" +
            "            \"short_name\" : \"31-056\",\n" +
            "            \"types\" : [ \"postal_code\" ]\n" +
            "         }\n" +
            "      ],\n" +
            "      \"adr_address\" : \"\\u003cspan class=\\\"street-address\\\"\\u003eJózefa 14\\u003c/span\\u003e, \\u003cspan class=\\\"postal-code\\\"\\u003e31-056\\u003c/span\\u003e \\u003cspan class=\\\"locality\\\"\\u003eKraków\\u003c/span\\u003e, \\u003cspan class=\\\"country-name\\\"\\u003ePolska\\u003c/span\\u003e\",\n" +
            "      \"formatted_address\" : \"Józefa 14, 31-056 Kraków, Polska\",\n" +
            "      \"formatted_phone_number\" : \"12 430 65 38\",\n" +
            "      \"geometry\" : {\n" +
            "         \"location\" : {\n" +
            "            \"lat\" : 50.05062299999999,\n" +
            "            \"lng\" : 19.94441879999999\n" +
            "         },\n" +
            "         \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : 50.0519900302915,\n" +
            "               \"lng\" : 19.9457589802915\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : 50.0492920697085,\n" +
            "               \"lng\" : 19.94306101970849\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "      \"id\" : \"24d6031ed5243f1c9627f9f3a37989ec3e584951\",\n" +
            "      \"international_phone_number\" : \"+48 12 430 65 38\",\n" +
            "      \"name\" : \"Starka\",\n" +
            "      \"opening_hours\" : {\n" +
            "         \"open_now\" : false,\n" +
            "         \"periods\" : [\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 0,\n" +
            "                  \"time\" : \"2300\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 0,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 1,\n" +
            "                  \"time\" : \"2300\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 1,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 2,\n" +
            "                  \"time\" : \"2300\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 2,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 3,\n" +
            "                  \"time\" : \"2300\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 3,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 4,\n" +
            "                  \"time\" : \"2300\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 4,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"0000\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 5,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            },\n" +
            "            {\n" +
            "               \"close\" : {\n" +
            "                  \"day\" : 0,\n" +
            "                  \"time\" : \"0000\"\n" +
            "               },\n" +
            "               \"open\" : {\n" +
            "                  \"day\" : 6,\n" +
            "                  \"time\" : \"1200\"\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"weekday_text\" : [\n" +
            "            \"poniedziałek: 12:00–23:00\",\n" +
            "            \"wtorek: 12:00–23:00\",\n" +
            "            \"środa: 12:00–23:00\",\n" +
            "            \"czwartek: 12:00–23:00\",\n" +
            "            \"piątek: 12:00–00:00\",\n" +
            "            \"sobota: 12:00–00:00\",\n" +
            "            \"niedziela: 12:00–23:00\"\n" +
            "         ]\n" +
            "      },\n" +
            "      \"photos\" : [\n" +
            "         {\n" +
            "            \"height\" : 2848,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103837947623396549268/photos\\\"\\u003eStarka\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAzK3c18rBz1gS_Ch0be_1qsIjjyw2EGeG4HdBvjPxQjouFDyadC35ZZ4lGc6XEPovB-wrozrz5ZcmTZW4MaCzUfmz66rp7fYCxweroHhyIzMmsz1t7dHpPqswUnZd9AGjEhBkMC8aatNXuhi-4PbLmzkMGhS3PzoU-ChiYxB5EbsJ_Y5FjIhqEg\",\n" +
            "            \"width\" : 4288\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 898,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101410439124578665861/photos\\\"\\u003eMonika Gal\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAxSFTycnQm-AK0J2ebyuyvtZPSyAqDhethJ6DRM9i_Q-NJjR9tnPzHRiW4hnziEe-zn1q8VQQeBPKoPex-femlGK7CkwfbN-OJ2jKiyJT7lyHwEKVWs2RlXYp8PDhdkb2EhAku9_3rp__Kg0hUSX_Q8-yGhT7-EB6RB1_PedE4Wl9ed8IzZsdOQ\",\n" +
            "            \"width\" : 1347\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 1200,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101902480243390865453/photos\\\"\\u003eÖzgür Deli\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAAZnOm3uBvnTSBfaZZXScZTzFeUzSOsENyeggmty3Ct2P-R-k-etcIHhxdWO4S6zRC441bb26eoqBsDhkE14jWDi_y3rTzrnKwl29CbykENkf_v9MHzA-0d8OgMhitxzIEhDxlT2P_MnuzAI_sx-tWlZRGhRyljQR88kTr12zL3wl4GU89QUkDA\",\n" +
            "            \"width\" : 1600\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 1335,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101410439124578665861/photos\\\"\\u003eMonika Gal\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAO9ZgwXkwVbdhv2X4Vp7aSpv1wmV013BEubTJHoD-5XXB5_SV4sJweWyBs-CISNKBmcrd0avYdnQJzAAMxZ-DfJp3ck65c6-v2SJ_ztSKUrsVy0ErrzT8JQUTMggPfAvtEhD5XwRhI01D9jUoHVHjPq9zGhQwA8h5pkVFPVDLx_YxgdsBpVtKyw\",\n" +
            "            \"width\" : 2000\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 2867,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107497282562910947205/photos\\\"\\u003ePhill Gillespie\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAtPw_AviukjLIzqAc5zljJOGe8E8p3L9a5WEa4m2mgdiBkE_LxXcj3tHi_UwLGhPlk8rTU8XOws6RidN0xB0sj0_NlxxtVwkkX08w7-HRS55Cksb7MkYvjG1Uq7sMWM01EhC_N1Anz6kwltPE_1V0Bp9nGhS8B64F4JbmMdfNsb1HDsWkCktWgw\",\n" +
            "            \"width\" : 3823\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 3024,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109131070259960698072/photos\\\"\\u003eilia popoff\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAbeP_momRPGLKSfsYFx7IMg7GAZZIdnvzyzJpS4NP40iBnzQyp-4Wo_DrxgikwAe5Ya0hP8l5PniHrqgcsDym9kpN3GYK8Mm0yNQ4xIQIvnAbgC60LaKGQWL8D79g10-AEhBp1arz4zEeHdnVuljv5pcUGhQ4lSyF1aJfymTDmoAFCMBe_ttW3Q\",\n" +
            "            \"width\" : 4032\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 3024,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110854904272672323634/photos\\\"\\u003ewenyuan ou\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAA1DU2jJKOpZenNvSiaPmnjH32ropQK8lnt8baHL01yrBLlVXS70t9KXJSq7syVP1QFCg20_PhgBMBD0pYNWIQJwBDIhoSk3yEA38L6TOYLPH-HFEXQYLpXO2ZNepqTvfLEhAlxAyJXoyjdw2Aw9F6zg16GhT1t0Z3SQCUOEW7aF3FbNdMfJvxtg\",\n" +
            "            \"width\" : 4032\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 3120,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108664081411809116452/photos\\\"\\u003eshonach chang\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAA_EbY3Aq8qJkYVVdQ0TlIQdidL35fURAeeL9Jw7TR3SNw01M1iClt-leWlcl9Sa6YFGdLQuMxAx4Ch68y73iUXmC4TSFf1-dHku5Udrkz2vRN_8sCxnXKg0OmUSPkGFoFEhAK4kM_T7joKNQUunIH5S-5GhSZL3zTB_iRcqBrXIr1jRDSVxbYwg\",\n" +
            "            \"width\" : 4160\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 1586,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101410439124578665861/photos\\\"\\u003eMonika Gal\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAAhSCd7oknnujUHVk6DlHO-2yT7zN2HAsiOwO527bgFK63n6zgsJnC3MzyG3E6BvuNOZRDy_cfeSQMbDdyaEiQFy4DGztpCBeHaZSH7W66ZANMP1Hh9ZZ4Tau8Vb2qmKWfEhD9jrKekXstH2PoYeUvkO4xGhTXvdm1j0_ABW2l13DEOeMT9I8gTg\",\n" +
            "            \"width\" : 1058\n" +
            "         },\n" +
            "         {\n" +
            "            \"height\" : 3024,\n" +
            "            \"html_attributions\" : [\n" +
            "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101410439124578665861/photos\\\"\\u003eMonika Gal\\u003c/a\\u003e\"\n" +
            "            ],\n" +
            "            \"photo_reference\" : \"CmRaAAAA_1zOEqeZzEWHfLrysjAyVIMemNL3lclo3t7eJ-5Ipn5ealLhwotjmLzqiWl72YIpyaqRxFz2817sbvoiEbALvcxm7Ck8UxEEhTRxuSKubTHL1j0_ea6aDJ5joIpFK5TMEhBwMywbFjHbqQjlrbtRZoEYGhQnFCzNfTpkyft7El5LcBlWEcmiuQ\",\n" +
            "            \"width\" : 4032\n" +
            "         }\n" +
            "      ],\n" +
            "      \"place_id\" : \"ChIJuaKgPWpbFkcR1BEMavHF11Y\",\n" +
            "      \"rating\" : 4.6,\n" +
            "      \"reference\" : \"CmRRAAAAZOoFxBnv0Gw_KyMllJqSCOhvlD-gnr4Mw34XE6t10BosChO7r1jnxQ7v9dRxuN-UWW86YQIvrGdRi0eqqeGFF6v3QSQKaJXUojSXqLBELiR_NG02zS-RT3vGAOgdURs7EhB37TxjGmsn6cJtZG80BelWGhQyZ5qMN2ISpw_D0ztuQm-aaHN4sw\",\n" +
            "      \"reviews\" : [\n" +
            "         {\n" +
            "            \"author_name\" : \"Barbara M\",\n" +
            "            \"author_url\" : \"https://www.google.com/maps/contrib/106219644080436288389/reviews\",\n" +
            "            \"language\" : \"pl\",\n" +
            "            \"profile_photo_url\" : \"https://lh6.googleusercontent.com/-p2ENPgFgvnU/AAAAAAAAAAI/AAAAAAAAAK0/SApNUOCYOb0/s128-c0x00000000-cc-rp-mo-ba3/photo.jpg\",\n" +
            "            \"rating\" : 3,\n" +
            "            \"relative_time_description\" : \"w ostatnim tygodniu\",\n" +
            "            \"text\" : \"Jedzenie smaczne, duże porcje. Klimat specyficzny, pachnący artyzmem przez grafiki Heinricha Zille. Restauracja reklamuje się 'luźną i przyjazna atmosferą', niestety czasami zbyt luźną. W trakcie mojej wizyty kelnerki były mało zainteresowane obsługą polskiej klienteli, za to żwawo skakały w około zagranicznych klientów. Przy podawaniu posiłku można stwierdzić, że z pośpiechu go prawie rzuciły talerz na stół. Brak choćby cienia uśmiechu czy sympatii na twarzy i w zachowaniu. Niestety, za to minus, tym bardziej, że na posiłek w Starce trzeba się raczej mieć rezerwację z wyprzedzeniem.\",\n" +
            "            \"time\" : 1527401093\n" +
            "         },\n" +
            "         {\n" +
            "            \"author_name\" : \"Mateusz Jurek\",\n" +
            "            \"author_url\" : \"https://www.google.com/maps/contrib/106399350026862147211/reviews\",\n" +
            "            \"language\" : \"pl\",\n" +
            "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-TtYex9vAfqc/AAAAAAAAAAI/AAAAAAAAGhA/VZQ3dhI0PQI/s128-c0x00000000-cc-rp-mo-ba3/photo.jpg\",\n" +
            "            \"rating\" : 5,\n" +
            "            \"relative_time_description\" : \"2 tygodnie temu\",\n" +
            "            \"text\" : \"Pyszne jedzenia, podane w sposób cieszący oko. Obluga na poziomie. Wybór dań całkiem spory, będę musiał jeszce kilka razy sie wybrać żeby przetestować to, co mnie zainteresowalo. Bardzo dobre nalewki robione na miejscu. Ja zamówiłem sobie gruszkową i smak mnie uwiódł. Na koniec, na koszt firmy, znakomita nalewka grapefruitowa. Zachęcam do odwiedzenia.\",\n" +
            "            \"time\" : 1526555440\n" +
            "         },\n" +
            "         {\n" +
            "            \"author_name\" : \"Ola Tomaszewska\",\n" +
            "            \"author_url\" : \"https://www.google.com/maps/contrib/115536905015045875926/reviews\",\n" +
            "            \"language\" : \"pl\",\n" +
            "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-JO-l-f4hfl4/AAAAAAAAAAI/AAAAAAAAAAA/AB6qoq0sDGKTMX63zNdygnfy5PoEBEO_Fw/s128-c0x00000000-cc-rp-mo/photo.jpg\",\n" +
            "            \"rating\" : 5,\n" +
            "            \"relative_time_description\" : \"tydzień temu\",\n" +
            "            \"text\" : \"Smaczne jedzenie, które jest podawane w ciekawy estetyczny sposób. Porcje sa duże, można się porządnie najeść :) wystroj na plus, bardzo przyjemne wnętrze z domowym klimatem.\",\n" +
            "            \"time\" : 1526897728\n" +
            "         },\n" +
            "         {\n" +
            "            \"author_name\" : \"Paweł Młynarczyk\",\n" +
            "            \"author_url\" : \"https://www.google.com/maps/contrib/111626753113754367534/reviews\",\n" +
            "            \"language\" : \"pl\",\n" +
            "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-6ImFwQ0ibvA/AAAAAAAAAAI/AAAAAAAAAKM/r5jGe8_acJQ/s128-c0x00000000-cc-rp-mo-ba2/photo.jpg\",\n" +
            "            \"rating\" : 4,\n" +
            "            \"relative_time_description\" : \"2 tygodnie temu\",\n" +
            "            \"text\" : \"Bardzo miła obsługa. Fajnie, że raczą darmową przystawką i wódką na dobre trawienie. Obiad smaczny. Wystrój restauracji mocno przytłaczający, ciemny. Dobrze, ale nie zachwyciłem się tak, aby przychodzić tu regularnie.\",\n" +
            "            \"time\" : 1526361488\n" +
            "         },\n" +
            "         {\n" +
            "            \"author_name\" : \"Wojtek K\",\n" +
            "            \"author_url\" : \"https://www.google.com/maps/contrib/112466983621221814094/reviews\",\n" +
            "            \"language\" : \"pl\",\n" +
            "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-jBTdSshtH8o/AAAAAAAAAAI/AAAAAAAAAAA/AB6qoq3x20n3pLvR6pPsor2ECOLYIg1QXg/s128-c0x00000000-cc-rp-mo-ba2/photo.jpg\",\n" +
            "            \"rating\" : 5,\n" +
            "            \"relative_time_description\" : \"miesiąc temu\",\n" +
            "            \"text\" : \"Bardzo mila atmosfera... polecam w 100%\\nNa starter gratis serek z chlebkiem. Jedzenie piękne podane oraz smaczne polskie smaki. Urzekła mnie nalewka  na koniec do rachunku  \\\"total free\\\" zasłużone 5 gwiazdek :) polecam\",\n" +
            "            \"time\" : 1525090812\n" +
            "         }\n" +
            "      ],\n" +
            "      \"scope\" : \"GOOGLE\",\n" +
            "      \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "      \"url\" : \"https://maps.google.com/?cid=6257687847911559636\",\n" +
            "      \"utc_offset\" : 120,\n" +
            "      \"vicinity\" : \"Józefa 14, Kraków\",\n" +
            "      \"website\" : \"http://www.starka-restauracja.pl/\"\n" +
            "   },\n" +
            "   \"status\" : \"OK\"\n" +
            "}"

}
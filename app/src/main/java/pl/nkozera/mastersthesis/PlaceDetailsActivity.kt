/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.place.PlaceDetails
import pl.nkozera.mastersthesis.place.PlacesList

class PlaceDetailsActivity : BaseMenuActivity() {


   // private lateinit var place: PlaceDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_details)
        val placeId = intent.getStringExtra("placeId")

        PlacesList(this).findDetails(placeId)

    }
}
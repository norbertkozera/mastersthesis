/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_place_details.*
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.place.ApiRequest
import pl.nkozera.mastersthesis.place.PlaceDetails
import pl.nkozera.mastersthesis.place.PlacesList


class PlaceDetailsActivity : BaseMenuActivity() {


    private lateinit var placeDetails: PlaceDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_details)
        val placeId = intent.getStringExtra("placeId")

        val details = PlacesList(this)
        details.findDetails(placeId)
        placeDetails = details.getCurrentPlaceDetails()
        applyDetails()

    }

    private fun applyDetails() {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.activity_place_details, null)
        val linearLayout = v.findViewById(R.id.rating) as LinearLayout
        for (i in 1..5) {
            val iwStar = ImageView(this)
            iwStar.id = View.generateViewId()
            val iwIconParams = LinearLayout.LayoutParams(resources.getDimension(R.dimen.place_details_star).toInt(), resources.getDimension(R.dimen.place_details_star).toInt())
            iwStar.layoutParams = iwIconParams

            when {
                i <= placeDetails.getRating() -> iwStar.setImageDrawable(getDrawable(R.drawable.star128))
                i - 0.7 < placeDetails.getRating() && i - 0.3 > placeDetails.getRating() -> iwStar.setImageDrawable(getDrawable(R.drawable.star128_half))
                else -> iwStar.setImageDrawable(getDrawable(R.drawable.star128_emptyf))
            }

            linearLayout.addView(iwStar)

        }

        val twRating = TextView(this)
        twRating.text = placeDetails.getRating().toString()
        twRating.textSize = resources.getDimension(R.dimen.place_details_rating_text)
        twRating.id = View.generateViewId()
        val twRatingParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        twRatingParams.setMargins(resources.getDimension(R.dimen.place_details_rating_text_margin).toInt(), 0, 0, 0)
        twRating.layoutParams = twRatingParams

        linearLayout.addView(twRating)

        val ll = LinearLayout(this)
        ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.gravity = Gravity.CENTER

        val comments = placeDetails.getComments()

        val commentsView = v.findViewById(R.id.place_details_comments_layout) as LinearLayout


        for (i in 0 until comments.size) {
            val comment = comments[i]

            val rl = RelativeLayout(this)
            rl.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            rl.background = resources.getDrawable(R.drawable.custom_background_top)

            val ll = LinearLayout(this)
            val llparams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            ll.gravity = Gravity.CENTER

            ll.id = View.generateViewId()
            ll.layoutParams = llparams

            val tvCommentatorName = TextView(this)
            tvCommentatorName.text = comment.getCommentatorName()
            tvCommentatorName.textSize = resources.getDimension(R.dimen.place_details_rating_text)

            ll.addView(tvCommentatorName)

            val iwStar = ImageView(this)

            val iwIconParams = LinearLayout.LayoutParams(resources.getDimension(R.dimen.place_details_star).toInt(), resources.getDimension(R.dimen.place_details_star).toInt())
            iwIconParams.gravity = Gravity.END
            iwIconParams.setMargins(resources.getDimension(R.dimen.place_details_rating_comment_margin).toInt(), 0, 0, 0)
            iwStar.layoutParams = iwIconParams
            iwStar.setImageDrawable(getDrawable(R.drawable.star128))

            ll.addView(iwStar)

            val tvUserRating = TextView(this)
            tvUserRating.text = comment.getRating().toString()
            tvUserRating.textSize = resources.getDimension(R.dimen.place_details_rating_text)

            ll.addView(tvUserRating)
            rl.addView(ll)


            val tvCommentText = TextView(this)
            tvCommentText.text = comment.getComment()
            tvCommentText.textSize = resources.getDimension(R.dimen.restaurant_list_others)

            val tvDistanceParams = RelativeLayout.LayoutParams(R.dimen.restaurant_list_width_location, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvDistanceParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            tvDistanceParams.addRule(RelativeLayout.BELOW, ll.id)
            tvCommentText.layoutParams = tvDistanceParams

            rl.addView(tvCommentText)
            commentsView.addView(rl)

        }

        setContentView(v)

        place_name.text = placeDetails.getPlaceName()

        places_details_opened_now.text = if (EMPTY_STRING.equals(placeDetails.getOpenedNow())) {
            places_details_opened_now.visibility = View.INVISIBLE
            EMPTY_STRING
        } else {
            places_details_opened_now.visibility = View.VISIBLE
            when (placeDetails.getOpenedNow().toBoolean()) {
                true -> "OTWARTE!"
                false -> "ZAMKNIĘTE!"
            }
        }

        val photoUrl = ApiRequest(this).placePhoto(restaurant_photo.layoutParams.width, restaurant_photo.layoutParams.height, placeDetails.getPhotoRef())
        if (!EMPTY_STRING.equals(photoUrl)) {
            Glide.with(this).load(photoUrl).into(restaurant_photo)
        }



        places_details_address.text = if (EMPTY_STRING.equals(placeDetails.getAddress())) {
            places_details_address.visibility = View.VISIBLE
            "Wyznacz trasę"

        } else {
            places_details_address.visibility = View.VISIBLE
            placeDetails.getAddress()
        }


        places_details_address.setOnClickListener {
            val uri = "http://maps.google.com/maps?daddr=" + placeDetails.getLocation().toString()
            val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }



        places_details_phone.text = if (EMPTY_STRING.equals(placeDetails.getPhoneNumber())) {
            places_details_address.visibility = View.GONE
            EMPTY_STRING
        } else {
//            @IntDef when (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                PackageManager.PERMISSION_GRANTED -> places_details_phone.setOnClickListener {
//
//                    val callIntent = Intent(Intent.ACTION_CALL)
//                    callIntent.data = Uri.parse("tel:${placeDetails.getPhoneNumber()}")
//                    startActivity(callIntent)
//                }
//            }

            //WORKAROUND FOR NOT NEED OF CALL PERMISSION
            places_details_phone.setOnClickListener {
                val uri = "tel:${placeDetails.getPhoneNumber()}"
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(intent)
            }

            places_details_address.visibility = View.VISIBLE
            placeDetails.getPhoneNumber()
        }



        places_details_website.text = if (EMPTY_STRING.equals(placeDetails.getWebsite())) {
            places_details_website.visibility = View.GONE
            EMPTY_STRING
        } else {
            places_details_website.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placeDetails.getWebsite()))
                startActivity(intent)
            }
            places_details_website.visibility = View.VISIBLE
            placeDetails.getWebsite()
        }


    }
}
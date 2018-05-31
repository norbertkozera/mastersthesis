/*
 * Master Thesis project
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
import pl.nkozera.mastersthesis.place.asynctasks.GenerateUrl
import pl.nkozera.mastersthesis.place.asynctasks.GetPlaceDetailsAsyncTask
import pl.nkozera.mastersthesis.place.asynctasks.OnTaskCompleted
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_INT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.GOOGLE_MAPS_DIRECT_TO
import pl.nkozera.mastersthesis.base.BaseValues.Companion.GOOGLE_MAPS_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.HTML_TEL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID
import pl.nkozera.mastersthesis.base.BaseValues.Companion.SPACE
import pl.nkozera.mastersthesis.place.*
import pl.nkozera.mastersthesis.place.objects.PlaceDetails


class PlaceDetailsActivity : BaseMenuActivity(), OnTaskCompleted {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgressBar()
        val placeId = intent.getStringExtra(PARAM_PLACE_ID)
        val url = GenerateUrl(this).findDetailsUrl(placeId)
        GetPlaceDetailsAsyncTask(details, this).execute(url)
    }

    override fun onTaskCompleted() {
        hideProgressBar(R.layout.activity_place_details)
        placeDetails = details.getCurrentPlaceDetails()
        applyDetails()
    }

    private fun applyDetails() {
        //setup average place rating view
        val inflater = LayoutInflater.from(this)
        val activityPlaceDetails = inflater.inflate(R.layout.activity_place_details, null) //FIXME Do not use null as ViewGroup
        val ratingLinearLayout = activityPlaceDetails.findViewById(R.id.rating) as LinearLayout
        for (i in 1..5) {
            val starsImageView = ImageView(this)
            starsImageView.id = View.generateViewId()
            val iwIconParams = LinearLayout.LayoutParams(resources.getDimension(R.dimen.place_details_star).toInt(), resources.getDimension(R.dimen.place_details_star).toInt())
            starsImageView.layoutParams = iwIconParams

            when {
                i <= placeDetails.getRating() -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128))
                i - 0.7 < placeDetails.getRating() && i - 0.3 > placeDetails.getRating() -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128_half))
                else -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128_emptyf))
            }

            ratingLinearLayout.addView(starsImageView)
        }

        val ratingTextView = TextView(this)
        ratingTextView.text = placeDetails.getRating().toString()
        ratingTextView.textSize = resources.getDimension(R.dimen.place_details_rating_text)
        ratingTextView.id = View.generateViewId()
        val twRatingParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        twRatingParams.setMargins(resources.getDimension(R.dimen.place_details_rating_text_margin).toInt(), DEFAULT_INT, DEFAULT_INT, DEFAULT_INT)
        ratingTextView.layoutParams = twRatingParams

        ratingLinearLayout.addView(ratingTextView)

        //setup place comments view
        val comments = placeDetails.getComments()
        val commentsLinearLayout = activityPlaceDetails.findViewById(R.id.place_details_comments_layout) as LinearLayout
        for (i in 0 until comments.size) {
            val comment = comments[i]

            val commentatorNameAndRatingRelativeLayout = RelativeLayout(this)
            commentatorNameAndRatingRelativeLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            commentatorNameAndRatingRelativeLayout.background = resources.getDrawable(R.drawable.custom_background_top, theme)

            val commentatorAndRatingCurrentCommentLinearLayout = LinearLayout(this)
            val commentatorAndRatingCurrentCommentLinearLayoutParameters = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            commentatorAndRatingCurrentCommentLinearLayout.gravity = Gravity.CENTER
            commentatorAndRatingCurrentCommentLinearLayout.id = View.generateViewId()
            commentatorAndRatingCurrentCommentLinearLayout.layoutParams = commentatorAndRatingCurrentCommentLinearLayoutParameters

            val cmmentatorNameTextView = TextView(this)
            cmmentatorNameTextView.text = comment.getCommentatorName()
            cmmentatorNameTextView.textSize = resources.getDimension(R.dimen.place_details_rating_text)

            commentatorAndRatingCurrentCommentLinearLayout.addView(cmmentatorNameTextView)

            val commentatorRatingStarTextView = ImageView(this)
            val commentatorRatingStarTextViewParameters = LinearLayout.LayoutParams(resources.getDimension(R.dimen.place_details_star).toInt(), resources.getDimension(R.dimen.place_details_star).toInt())
            commentatorRatingStarTextViewParameters.gravity = Gravity.END
            commentatorRatingStarTextViewParameters.setMargins(resources.getDimension(R.dimen.place_details_rating_comment_margin).toInt(), DEFAULT_INT, DEFAULT_INT, DEFAULT_INT)
            commentatorRatingStarTextView.layoutParams = commentatorRatingStarTextViewParameters
            commentatorRatingStarTextView.setImageDrawable(getDrawable(R.drawable.star128))

            commentatorAndRatingCurrentCommentLinearLayout.addView(commentatorRatingStarTextView)

            val commentatorRatingTextView = TextView(this)
            commentatorRatingTextView.text = comment.getRating().toString()
            commentatorRatingTextView.textSize = resources.getDimension(R.dimen.place_details_rating_text)

            commentatorAndRatingCurrentCommentLinearLayout.addView(commentatorRatingTextView)
            commentatorNameAndRatingRelativeLayout.addView(commentatorAndRatingCurrentCommentLinearLayout)


            val tvCommentText = TextView(this)
            tvCommentText.text = comment.getComment()
            tvCommentText.textSize = resources.getDimension(R.dimen.restaurant_list_others)

            val tvDistanceParams = RelativeLayout.LayoutParams(R.dimen.restaurant_list_width_location, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvDistanceParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            tvDistanceParams.addRule(RelativeLayout.BELOW, commentatorAndRatingCurrentCommentLinearLayout.id)
            tvCommentText.layoutParams = tvDistanceParams

            commentatorNameAndRatingRelativeLayout.addView(tvCommentText)
            commentsLinearLayout.addView(commentatorNameAndRatingRelativeLayout)

        }

        setContentView(activityPlaceDetails)

        //setup place name
        place_name.text = placeDetails.getPlaceName()

        //setup info if place is now opened
        places_details_opened_now.text = if (EMPTY_STRING == placeDetails.getOpenedNow()) {
            places_details_opened_now.visibility = View.INVISIBLE
            EMPTY_STRING
        } else {
            places_details_opened_now.visibility = View.VISIBLE
            when (placeDetails.getOpenedNow().toBoolean()) {
                true -> getString(R.string.opened).toUpperCase()
                false -> getString(R.string.closed).toUpperCase()
            }
        }

        //setup photo presenting restaurant
        val photoUrl = GenerateUrl(this).placePhoto(restaurant_photo.layoutParams.width, restaurant_photo.layoutParams.height, placeDetails.getPhotoRef())
        if (EMPTY_STRING != photoUrl) {
            Glide.with(this).load(photoUrl).into(restaurant_photo)
        }

        //setup place address and click listener for opening google maps
        places_details_address.text = if (EMPTY_STRING == placeDetails.getAddress()) {
            places_details_address.visibility = View.VISIBLE
            getString(R.string.show_in_google_maps)

        } else {
            places_details_address.visibility = View.VISIBLE
            placeDetails.getAddress()
        }

        places_details_address.setOnClickListener {
            val uri = GOOGLE_MAPS_URL + GOOGLE_MAPS_DIRECT_TO + placeDetails.getLocation().toString()
            val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        //setup place phone number and click listener for opening calling interface
        places_details_phone.text = if (EMPTY_STRING == placeDetails.getPhoneNumber()) {
            places_details_address.visibility = View.GONE
            EMPTY_STRING
        } else {
//            @IntDef when (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                PackageManager.PERMISSION_GRANTED -> places_details_phone.setOnClickListener {
//
//                    val callIntent = Intent(Intent.ACTION_CALL)
//                    callIntent.data = Uri.parse("tel:${placeDetails.getPhoneNumber()}")
//                    startActivity(callIntent)
//                }
//            }

            //WORKAROUND FOR NO NEED OF CALL PERMISSION
            //and for not starting call automatically
            places_details_phone.setOnClickListener {
                val uri = HTML_TEL + placeDetails.getPhoneNumber().replace(SPACE, EMPTY_STRING)
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(intent)
            }

            places_details_address.visibility = View.VISIBLE
            placeDetails.getPhoneNumber()
        }

        //setup place webpage and click listener for opening in browser
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

    private lateinit var placeDetails: PlaceDetails
    private var details = ChoosenPlaceDetails()
}
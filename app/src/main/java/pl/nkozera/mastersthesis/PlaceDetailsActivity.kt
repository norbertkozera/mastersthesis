/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_place_details.*
import pl.nkozera.mastersthesis.base.BaseMenuActivity
import pl.nkozera.mastersthesis.base.BaseValues
import pl.nkozera.mastersthesis.base.BaseValues.Companion.DEFAULT_INT
import pl.nkozera.mastersthesis.base.BaseValues.Companion.EMPTY_STRING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.GOOGLE_MAPS_DIRECT_TO
import pl.nkozera.mastersthesis.base.BaseValues.Companion.GOOGLE_MAPS_URL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.HTML_TEL
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_AUTHOR_NAME
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_COMMENTS
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_PLACE_ID
import pl.nkozera.mastersthesis.base.BaseValues.Companion.PARAM_RATING
import pl.nkozera.mastersthesis.base.BaseValues.Companion.SPACE
import pl.nkozera.mastersthesis.place.ChosenPlaceDetails
import pl.nkozera.mastersthesis.place.asynctasks.GenerateUrl
import pl.nkozera.mastersthesis.place.asynctasks.GetPlaceDetailsAsyncTask
import pl.nkozera.mastersthesis.place.asynctasks.OnTaskCompleted
import pl.nkozera.mastersthesis.place.objects.PlaceComment
import pl.nkozera.mastersthesis.place.objects.PlaceDetails
import java.text.DecimalFormat


class PlaceDetailsActivity : BaseMenuActivity(), OnTaskCompleted {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showProgressBar()
        val placeId = intent.getStringExtra(PARAM_PLACE_ID)
        val url = GenerateUrl(this).findDetailsUrl(placeId)
        GetPlaceDetailsAsyncTask(details, this).execute(url)
        ratingTextView = TextView(this)
    }

    private fun updateInformation(values: Any) {
        when (values) {
            is HashMap<*, *> -> {

                for (value in values) {
                    val hashMap = value.value as java.util.HashMap<*, *>
                    try {
                        updateRating(hashMap[BaseValues.PARAM_RATING].toString().toDouble())
                        updateComments(hashMap)
                        //  run()
                    } catch (e: Exception) {

                    }
                }
            }
            else -> {

            }
        }


    }

    private fun updateComments(value: java.util.HashMap<*, *>) {
        placeDetails.updateComments(PlaceComment(
                value[PARAM_AUTHOR_NAME].toString(),
                value[PARAM_COMMENTS].toString(),
                "",
                "",
                value[PARAM_RATING].toString().toDouble()

        ))

        Toast.makeText(this, getString(R.string.added_comment), Toast.LENGTH_SHORT).show()
    }

    private fun updateRating(value: Double) {
        placeRating += value
        placeRating /= 2
        placeRating = DecimalFormat("#.##").format(placeRating).replace(",", ".").toDouble()
        ratingTextView.text = placeRating.toString()


    }

    var apply = true
    lateinit var ratingTextView: TextView

    fun addRating(@Suppress(BaseValues.UNUSED_PARAMETER) view: View) {
        place_details_layout.visibility = View.GONE
        add_rating.visibility = View.VISIBLE
    }

    fun addToFav(@Suppress(BaseValues.UNUSED_PARAMETER) view: View) {
        this.mDatabase.child("favourites").child(currentUser.uid).child(placeDetails.getPlaceId()).setValue(placeDetails.getPlaceName())
        Toast.makeText(this, getString(R.string.added), Toast.LENGTH_SHORT).show()
    }

    fun confirmRating(@Suppress(BaseValues.UNUSED_PARAMETER) view: View) {

        val rating = findViewById<View>(R.id.rating_radios) as RadioGroup
        val checked = rating.checkedRadioButtonId
        val selectedButton: RadioButton


        val comment = findViewById<View>(R.id.comment_text) as EditText
        val commentText = comment.text

        when {
            commentText.isEmpty() -> Toast.makeText(this, "napisz komentarz", Toast.LENGTH_LONG).show()
            checked == -1 -> Toast.makeText(this, "oceń restaurację", Toast.LENGTH_LONG).show()
            else -> {
                selectedButton = findViewById<View>(checked) as RadioButton
                mRestaurantRef.child(currentUser.uid).child(BaseValues.PARAM_AUTHOR_NAME).setValue(currentUser.displayName)
                mRestaurantRef.child(currentUser.uid).child(BaseValues.PARAM_RATING).setValue(selectedButton.text.toString().toDouble())
                mRestaurantRef.child(currentUser.uid).child(BaseValues.PARAM_COMMENTS).setValue(commentText.toString())
            }
        }
        place_details_layout.visibility = View.VISIBLE
        add_rating.visibility = View.GONE
    }

    fun cancelRating(@Suppress(BaseValues.UNUSED_PARAMETER) view: View) {
        place_details_layout.visibility = View.VISIBLE
        add_rating.visibility = View.GONE
    }

    override fun onTaskCompleted() {
        //details.createPlaceDetails(prepareObject().get(BaseValues.PARAM_RESULT))
        currentUser = mAuth.currentUser!!
        placeDetails = details.getCurrentPlaceDetails()
        placeRating = placeDetails.getRating()
        mRestaurantRef = mRestaurantsRef.child(placeDetails.getPlaceId())
        mRestaurantRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                placeDetails = details.getCurrentPlaceDetails()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val value = p0?.value
                placeDetails = details.getCurrentPlaceDetails()
                if (value != null) {
                    updateInformation(value)
                }
                applyDetails(apply)
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun applyDetails(apply: Boolean) {
        if (apply) {
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
                    i <= placeRating -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128))
                    i - 0.7 < placeRating && i - 0.3 > placeRating -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128_half))
                    else -> starsImageView.setImageDrawable(getDrawable(R.drawable.star128_emptyf))
                }

                ratingLinearLayout.addView(starsImageView)
            }

            ratingTextView = TextView(this)
            ratingTextView.text = placeRating.toString()
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

            print("")

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
            this.apply = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.search -> {
                startActivity(Intent(this, FindCityActivity::class.java))
                true
            }
            R.id.results -> {

                if(intent.getStringExtra(BaseValues.PARAM_CITY).isNullOrEmpty()){
                    Toast.makeText(this, getString(R.string.prompt_no_res), Toast.LENGTH_SHORT).show()
                    false
                } else {

                val findRestaurants = Intent(this, RestaurantListActivity::class.java)
                findRestaurants.putExtra(BaseValues.PARAM_CITY, intent.getStringExtra(BaseValues.PARAM_CITY))
                findRestaurants.putExtra(BaseValues.PARAM_DISTANCE, intent.getStringExtra(BaseValues.PARAM_DISTANCE))
                findRestaurants.putExtra(BaseValues.PARAM_LATITUDE, intent.getDoubleExtra(BaseValues.PARAM_LATITUDE, BaseValues.DEFAULT_DOUBLE))
                findRestaurants.putExtra(BaseValues.PARAM_LONGITUDE, intent.getDoubleExtra(BaseValues.PARAM_LONGITUDE, BaseValues.DEFAULT_DOUBLE))

                showProgressBar()
                startActivity(findRestaurants)
                finish()
                true
            }}
            R.id.menu_item_logout -> {
                firebaseLogOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private var placeDetails = PlaceDetails.emptyPlace()
    private var details = ChosenPlaceDetails()
    private lateinit var currentUser: FirebaseUser
    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mRestaurantsRef = this.mDatabase.child("restaurant")
    private lateinit var mRestaurantRef: DatabaseReference
    private lateinit var mFavRef: DatabaseReference
    private lateinit var mRestaurantCommentsRef: DatabaseReference
    private var placeRating: Double = BaseValues.DEFAULT_DOUBLE


///FIXME ONLY FOR TEST

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
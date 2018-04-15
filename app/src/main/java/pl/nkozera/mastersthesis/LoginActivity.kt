/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.widget.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.google.android.gms.common.util.ArrayUtils.contains
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.security.AccessController.getContext
import java.security.SecureRandom


class LoginActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInCLient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private val RC_SIGN_IN: Int = 1
    private val GET_FROM_GALLERY: Int = 2
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var avatarUrl: Uri = Uri.EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val googleSignIn = findViewById<View>(R.id.google_sign_in_button) as SignInButton
        googleSignIn.setOnClickListener { view: View? ->
            googleSignInOnClick()
        }
        FacebookSdk.sdkInitialize(applicationContext)
        mCallbackManager = CallbackManager.Factory.create()
        faceBookInitialize()


//        val loginButton = findViewById<View>(R.id.facebook_register_button) as LoginButton
//        loginButton.setReadPermissions("email", "public_profile")

//        val info: PackageInfo
//         try {
//             info = packageManager.getPackageInfo("pl.nkozera.mastersthesis", PackageManager.GET_SIGNATURES)
//             for (signature in info.signatures) {
//                 val md = MessageDigest.getInstance("SHA")
//                 md.update(signature.toByteArray())
//                 Log.d("KeyHash:", encodeToString(md.digest(), DEFAULT))
//             }
//         } catch (e: PackageManager.NameNotFoundException) {
//             e.printStackTrace()
//         } catch (e: Exception) {
//             e.printStackTrace()
//         }

        facebook_register_button.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        }
    }


    fun faceBookInitialize() {


        println("yess on fcbk")
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)

            }

            override fun onCancel() {
                //TODO Auto-generated method stub

            }

            override fun onError(error: FacebookException) {
                //TODO Auto-generated method stub

            }
        })
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    fun emailSignInOnClick(view: View) {
        val userEmail: String = email.text.toString()
        val passwd: String = password.text.toString()


        if (validateEmailAndPasswd(userEmail, passwd)) {
            firebaseAuthWithPassword(userEmail, passwd)
        }
    }

    fun confirmSignUp(view: View) {
        val userDisplayName: String = displayName.text.toString()
        val userEmail: String = email.text.toString()
        val passwd: String = password.text.toString()
        val retypePasswd: String = retype_new_password.text.toString()
        if (validateEmailAndPass(userDisplayName, userEmail, passwd, retypePasswd)) {
            firebaseRegisteWithPassword(userDisplayName, userEmail, passwd)
        }
    }

    private fun validateEmailAndPasswd(userEmail: String, passwd: String): Boolean {
        return if (userEmail.isEmpty()) {
            email.requestFocus()
            email.error = getString(R.string.error_field_is_required)
            false
        } else if (passwd.isEmpty()) {
            password.requestFocus()
            password.error = getString(R.string.error_field_is_required)
            false
        } else if (!userEmail.contains("@") ||
                !userEmail.substring(userEmail.indexOf("@")).contains(".")) {
            email.requestFocus()
            email.error = getString(R.string.error_invalid_email)
            false
        } else {
            true
        }
    }

    fun loadPhotoFromGallery(view: View) {
        email_register_account_button.isClickable = false
        startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY)
    }


    fun emailRegisterOnClick(view: View) {

        val emailSignUpButtonParams = email_register_button.layoutParams as RelativeLayout.LayoutParams
        val emailTextViewParams = email.layoutParams as RelativeLayout.LayoutParams

        emailSignUpButtonParams.addRule(RelativeLayout.BELOW, R.id.avatar_button)
        emailTextViewParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)

        view.layoutParams = emailSignUpButtonParams
        email_register_button.text = getString(R.string.cancel)
        email_register_button.setOnClickListener { cancelRegistration(view, emailSignUpButtonParams, emailTextViewParams) }

        retype_new_password.visibility = View.VISIBLE
        email_register_account_button.visibility = View.VISIBLE
        displayName.visibility = View.VISIBLE
        avatar_button.visibility = View.VISIBLE
        email_sign_in_button.visibility = View.GONE
        google_sign_in_button.visibility = View.GONE
        continue_withou_signing_in_button.visibility = View.GONE
        facebook_register_button.visibility = View.GONE
        displayName.requestFocus()


    }


    private fun cancelRegistration(view: View, emailSignUp: RelativeLayout.LayoutParams, emailTextView: RelativeLayout.LayoutParams) {

        retype_new_password.visibility = View.GONE
        email_register_account_button.visibility = View.GONE
        displayName.visibility = View.GONE
        avatar_button.visibility = View.GONE

        email_sign_in_button.visibility = View.VISIBLE
        google_sign_in_button.visibility = View.VISIBLE
        continue_withou_signing_in_button.visibility = View.VISIBLE
        facebook_register_button.visibility = View.VISIBLE

        emailSignUp.addRule(RelativeLayout.BELOW, R.id.password)
        emailTextView.addRule(RelativeLayout.ALIGN_PARENT_TOP)

        view.layoutParams = emailSignUp
        email_register_button.text = getString(R.string.action_register)
        email_register_button.setOnClickListener { emailRegisterOnClick(view) }

    }

    private fun validateEmailAndPass(userDisplayName: String, userEmail: String, passwd: String, retypePasswd: String): Boolean {
        return if (validateEmailAndPasswd(userEmail, passwd)) {
            when {
                userDisplayName.isEmpty() -> {
                    displayName.requestFocus()
                    displayName.error = getString(R.string.error_field_is_required)
                    false
                }
                passwd.length < 8 -> {
                    displayName.requestFocus()
                    password.error = getString(R.string.error_incorrect_password)
                    false
                }
                retypePasswd.length < 8 -> {
                    displayName.requestFocus()
                    retype_new_password.error = getString(R.string.error_incorrect_password)
                    false
                }
                retypePasswd != passwd -> {
                    retype_new_password.requestFocus()
                    retype_new_password.error = getString(R.string.error_different_passwords)
                    false
                }
                else -> true
            }
        } else {
            false
        }

    }


    fun googleSignInOnClick() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .build()
        mGoogleSignInCLient = GoogleSignIn.getClient(this, googleSignInOptions)
        mGoogleSignInCLient.signOut()
        val signInIntent: Intent = mGoogleSignInCLient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> handleResult(GoogleSignIn.getSignedInAccountFromIntent(data))
            GET_FROM_GALLERY -> when (resultCode) {
                Activity.RESULT_OK -> {
                    handleUploadAvatar(data)
                }
            }
        }
    }

    private fun handleUploadAvatar(data: Intent?) {
        avatar_progress.visibility = View.VISIBLE
        avatar_button.visibility = View.INVISIBLE
        loaded_avatar.visibility = View.INVISIBLE
        val selectedImageUri: Uri = data?.data as Uri
        val bitmap: Bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
            val imgName = SecureRandom().nextLong()
            val imageRef = storageRef.child("avatars/$imgName")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            imageRef.putBytes(bytes).addOnFailureListener {
                //TODO handler
            }.addOnSuccessListener {
                val url: Uri = it?.downloadUrl as Uri
                Glide.with(this).load(url).into(loaded_avatar)
                avatar_progress.visibility = View.GONE
                loaded_avatar.visibility = View.VISIBLE
                email_register_account_button.isClickable = true
                avatarUrl = url

            }
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> print("")
                is IOException -> print("")
            }
        }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, FindCityActivity::class.java))
            finish()
        } else {
            hideProgressBar()
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        showProgressBar()
//      TODO  => if user sign in as Facebook user, then sign in as Google user Firebase will overwrite user account, otherwise not
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> updateUI(it.exception)
                    }
                }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        showProgressBar()
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> {
                            LoginManager.getInstance().logOut()
                            updateUI(it.exception)
                        }
                    }
                }
    }

    private fun firebaseRegisteWithPassword(userName: String, email: String, password: String) {
        showProgressBar()

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> {
                            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(userName).setPhotoUri(avatarUrl).build()
                            mAuth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener(this) {
                                when {
                                    it.isSuccessful -> updateUI(mAuth.currentUser)
                                    else -> updateUI(it.exception)
                                }
                            }
                        }
                        else -> updateUI(it.exception)
                    }
                }
    }

    private fun updateUI(exception: Exception?) {
        hideProgressBar()
        handleAuthException(exception)

    }

    private fun hideProgressBar() {
        login_progress.visibility = View.GONE
        email_login_form.visibility = View.VISIBLE
    }


    private fun showProgressBar() {
        login_progress.visibility = View.VISIBLE
        email_login_form.visibility = View.GONE
    }


    private fun firebaseAuthWithPassword(email: String, password: String) {
        showProgressBar()
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> updateUI(it.exception)
                    }
                }
    }

    private fun handleAuthException(exception: Exception?) {
        val toastMsg: String = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.error_invalid_credentials)
            is FirebaseAuthUserCollisionException -> getString(R.string.error_accounts_duplicated)
            is FirebaseException -> getString(R.string.error_firebase_auth_other)
            else -> getString(R.string.error_firebase_other)
        }

        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
    }

    fun firebaseAuthAnonymusly(view: View) {
        showProgressBar()
        mAuth.signInAnonymously()
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> updateUI(it.exception)
                    }
                }
    }
}


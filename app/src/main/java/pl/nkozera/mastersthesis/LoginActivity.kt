/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import pl.nkozera.mastersthesis.auth.EmailValidator
import pl.nkozera.mastersthesis.auth.FieldValidator
import pl.nkozera.mastersthesis.auth.PasswordValidator
import pl.nkozera.mastersthesis.base.BaseActivity
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.security.SecureRandom


class LoginActivity : BaseActivity() {
    private lateinit var mGoogleSignInCLient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private val RC_SIGN_IN: Int = 1
    private val GET_FROM_GALLERY: Int = 2
    private val ASK_FOR_PERMISSTIONS: Int = 3
    private lateinit var mCallbackManager: CallbackManager
    private var avatarUrl: Uri = Uri.EMPTY
    val content = R.layout.activity_login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(content)

//        Ask for permission

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ASK_FOR_PERMISSTIONS)

//        Google login

        val googleSignIn = findViewById<View>(R.id.google_sign_in_button) as SignInButton
        googleSignIn.setOnClickListener {
            googleSignInOnClick()
        }

//        Facbook login
//      TODO  => https://stackoverflow.com/questions/49845972/android-facebook-login-with-firebase-forces-authentication-via-facebook-com-devi
        mCallbackManager = CallbackManager.Factory.create()
        val facebookButton = findViewById<View>(R.id.facebook_register_button) as LoginButton

        facebookButton.setReadPermissions("email", "public_profile")
        facebookButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                loginManager.logOut()
                updateUI(mAuth.currentUser)
            }

            override fun onError(error: FacebookException?) {
                loginManager.logOut()
                updateUI(error)
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> handleResult(GoogleSignIn.getSignedInAccountFromIntent(data))
            GET_FROM_GALLERY -> when (resultCode) {
                Activity.RESULT_OK -> {
                    handleUploadAvatar(data)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ASK_FOR_PERMISSTIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    finish()
//                    startActivity(intent)
//                    return
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
//                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, FindCityActivity::class.java))
            finish()
        }
    }

    private fun updateUI(exception: Exception?) {
        hideProgressBar(content)
        handleAuthException(exception)
    }


    fun emailRegisterOnClick(view: View) {
        val emailSignUpButtonParams = email_register.layoutParams as RelativeLayout.LayoutParams
        val emailTextViewParams = email.layoutParams as RelativeLayout.LayoutParams

        emailSignUpButtonParams.addRule(RelativeLayout.BELOW, R.id.avatar_button)
        emailTextViewParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)

        view.layoutParams = emailSignUpButtonParams
        email_register.text = "Anuluj"
        email_register.setOnClickListener { cancelRegistration(view, emailSignUpButtonParams, emailTextViewParams) }

        retype_password.visibility = View.VISIBLE
        email_register_account_button.visibility = View.VISIBLE
        displayName.visibility = View.VISIBLE
        avatar_button.visibility = View.VISIBLE
        email_sign_in_button.visibility = View.GONE
        google_sign_in_button.visibility = View.GONE
        continue_withou_signing_in_button.visibility = View.GONE
        facebook_register_button.visibility = View.GONE
        displayName.requestFocus()
    }

    private fun googleSignInOnClick() {
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

    fun firebaseAuthAnonymusly(@Suppress("UNUSED_PARAMETER") view: View) {
        showProgressBar()
        mAuth.signInAnonymously()
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> updateUI(it.exception)
                    }
                }
    }

    fun confirmSignUp(@Suppress("UNUSED_PARAMETER") view: View) {
        val userNameNotEmpty = FieldValidator().checkIfFieldsAreNotEmpty(this, displayName)
        val emailIsCorrect = EmailValidator(this, email).validate()
        val passwordIsCorrect = PasswordValidator(this, password, retype_password).validate()
        if (userNameNotEmpty && emailIsCorrect && passwordIsCorrect) {
            firebaseRegisteWithPassword(displayName.text.toString(), email.text.toString(), password.text.toString())
        }
    }

    fun emailSignInOnClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val emailIsCorrect = EmailValidator(this, email).validate()
        val passwordIsCorrect = PasswordValidator(this, password).validate()

        if (emailIsCorrect && passwordIsCorrect) {
            firebaseAuthWithPassword(email.text.toString(), password.text.toString())
        }
    }

    private fun cancelRegistration(view: View, emailSignUp: RelativeLayout.LayoutParams, emailTextView: RelativeLayout.LayoutParams) {
        retype_password.visibility = View.GONE
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
        email_register.text = getString(R.string.action_register)
        email_register.setOnClickListener { emailRegisterOnClick(view) }
    }

    @Suppress("UNUSED_PARAMETER") //param is needed
    fun loadPhotoFromGallery(view: View) {
        startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        showProgressBar()
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> updateUI(mAuth.currentUser)
                        else -> {
                            loginManager.logOut()
                            updateUI(it.exception)
                        }
                    }
                }
    }

    private fun handleUploadAvatar(data: Intent?) {
        showAvatarProgrssBar()
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
                hideAvatarProgressBar(false)
            }.addOnSuccessListener {
                val url: Uri = it?.downloadUrl as Uri
                Glide.with(this).load(url).into(loaded_avatar)
                avatarUrl = url
                hideAvatarProgressBar(true)
            }
        } catch (e: Exception) {
            when (e) {
                //TODO Log it
                is FileNotFoundException -> print("")
                is IOException -> print("")
            }
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            updateUI(e)
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

        makeToast(toastMsg)
    }


    fun showAvatarProgrssBar() {
        avatar_progress.visibility = View.VISIBLE
        avatar_button.visibility = View.INVISIBLE
        loaded_avatar.visibility = View.INVISIBLE
        email_register_account_button.isClickable = false
    }

    fun hideAvatarProgressBar(isSuccessful: Boolean) {
        when {
            isSuccessful -> {
                avatar_progress.visibility = View.GONE
                loaded_avatar.visibility = View.VISIBLE
                email_register_account_button.isClickable = true
            }
            else -> {
                avatar_progress.visibility = View.GONE
                loaded_avatar.visibility = View.INVISIBLE
                avatar_button.visibility = View.VISIBLE
                email_register_account_button.isClickable = true
                makeToast(getString(R.string.error_invalid_credentials))
            }
        }

    }

}


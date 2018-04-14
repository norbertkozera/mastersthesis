/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.facebook.appevents.AppEventsLogger;
import android.widget.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.*
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import android.support.annotation.NonNull
import android.support.v4.app.FragmentActivity
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.AuthCredential
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import java.security.MessageDigest


class LoginActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInCLient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private val RC_SIGN_IN: Int = 1
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

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


    fun emailRegisterOnClick(view: View) {

        val emailSignUpButtonParams = email_register_button.layoutParams as RelativeLayout.LayoutParams
        val emailTextViewParams = email.layoutParams as RelativeLayout.LayoutParams

        emailSignUpButtonParams.addRule(RelativeLayout.BELOW, R.id.checkboxRestaurantOwner)
        emailTextViewParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)

        view.layoutParams = emailSignUpButtonParams
        email_register_button.text = getString(R.string.cancel)
        email_register_button.setOnClickListener { cancelRegistration(view, emailSignUpButtonParams, emailTextViewParams) }

        retype_new_password.visibility = View.VISIBLE
        checkboxRestaurantOwner.visibility = View.VISIBLE
        email_register_account_button.visibility = View.VISIBLE
        displayName.visibility = View.VISIBLE
        email_sign_in_button.visibility = View.INVISIBLE
        google_sign_in_button.visibility = View.INVISIBLE
        continue_withou_signing_in_button.visibility = View.INVISIBLE
        displayName.requestFocus()


    }


    private fun cancelRegistration(view: View, emailSignUp: RelativeLayout.LayoutParams, emailTextView: RelativeLayout.LayoutParams) {

        retype_new_password.visibility = View.INVISIBLE
        checkboxRestaurantOwner.visibility = View.INVISIBLE
        email_register_account_button.visibility = View.INVISIBLE
        displayName.visibility = View.INVISIBLE

        email_sign_in_button.visibility = View.VISIBLE
        google_sign_in_button.visibility = View.VISIBLE
        continue_withou_signing_in_button.visibility = View.VISIBLE

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
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, FindCityActivity::class.java))
            login_progress.visibility = View.GONE
        } else {
            email_login_form.requestFocus()
            login_progress.visibility = View.GONE
            email_login_form.visibility = View.VISIBLE

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
        email_login_form.visibility = View.GONE
        login_progress.visibility = View.VISIBLE
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        login_progress.visibility = View.GONE
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "Problem while logging in 2", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        email_login_form.visibility = View.GONE
        login_progress.visibility = View.VISIBLE
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "FB Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                }

    }

    private fun firebaseRegisteWithPassword(userName: String, email: String, password: String) {
        email_login_form.visibility = View.GONE
        login_progress.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { createUser ->

                    if (createUser.isSuccessful) {
                        val user = mAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                        user!!.updateProfile(profileUpdates).addOnCompleteListener(this) { updateProf ->
                            if (updateProf.isSuccessful) {
                                
                                updateUI(user)
                            } else {
                                Toast.makeText(this, "Problem while registrating", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Problem while registrating", Toast.LENGTH_LONG).show()
                    }

                }
    }

    private fun firebaseAuthWithPassword(email: String, password: String) {
        email_login_form.visibility = View.GONE
        login_progress.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        login_progress.visibility = View.GONE
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while logging", Toast.LENGTH_LONG).show()
                    }

                }
    }

    fun firebaseAuthAnonymusly(view: View) {
        email_login_form.visibility = View.GONE
        login_progress.visibility = View.VISIBLE
        mAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        login_progress.visibility = View.GONE
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "Problem while logging", Toast.LENGTH_LONG).show()
                    }

                }
    }
}


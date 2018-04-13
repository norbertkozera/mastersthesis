/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
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
import android.widget.*
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private val LOG = LoggerFactory.getLogger("LoginActivity")

    lateinit var mGoogleSignInCLient: GoogleSignInClient
    lateinit var googleSignInOptions: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        println("test")
        mAuth = FirebaseAuth.getInstance()

        val googleSignIn = findViewById<View>(R.id.google_sign_in_button) as SignInButton
        googleSignIn.setOnClickListener { view: View? ->
            googleSignInOnClick()
        }


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
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

    private fun validateEmailAndPasswd(userEmail: String, passwd: String): Boolean {
        return if (userEmail.isEmpty()) {
            email.error = getString(R.string.error_field_is_required)
            false
        } else if (passwd.isEmpty()) {
            password.error = getString(R.string.error_field_is_required)
            false
        } else if (!userEmail.contains("@") &&
                !userEmail.substring(userEmail.indexOf("@")).contains(".")) {
            email.error = getString(R.string.error_invalid_email)
            false
        } else {
            true
        }
    }


    fun emailRegisterOnClick(view: View) {

        val emailSignInButtonParams = email_sign_in_button.layoutParams as RelativeLayout.LayoutParams
        val emailSignUpButtonParams = email_register_button.layoutParams as RelativeLayout.LayoutParams

        emailSignInButtonParams.addRule(RelativeLayout.BELOW, R.id.email_register_account_button)
        emailSignUpButtonParams.addRule(RelativeLayout.BELOW, R.id.email_register_account_button)

        view.layoutParams = emailSignInButtonParams
        view.layoutParams = emailSignUpButtonParams
        email_register_button.text = getString(R.string.cancel)
        email_register_button.setOnClickListener { cancelRegistration(view, emailSignInButtonParams, emailSignUpButtonParams) }

        retype_new_password.visibility = View.VISIBLE
        checkboxRestaurantOwner.visibility = View.VISIBLE
        email_register_account_button.visibility = View.VISIBLE


    }

    fun confirmSignUp(view: View) {
        val userEmail: String = email.text.toString()
        val passwd: String = password.text.toString()
        val retypePasswd: String = retype_new_password.text.toString()
        if (validateEmailAndPass(userEmail, passwd, retypePasswd)) {
            firebaseRegisteWithPassword(userEmail, passwd)
        }
    }

    private fun cancelRegistration(view: View, emailSignIn: RelativeLayout.LayoutParams, emailSignUp: RelativeLayout.LayoutParams) {

        retype_new_password.visibility = View.INVISIBLE
        checkboxRestaurantOwner.visibility = View.INVISIBLE
        email_register_account_button.visibility = View.INVISIBLE

        emailSignIn.addRule(RelativeLayout.BELOW, R.id.password)
        emailSignUp.addRule(RelativeLayout.BELOW, R.id.password)


        view.layoutParams = emailSignIn
        view.layoutParams = emailSignUp

        email_register_button.text = getString(R.string.action_register)
        email_register_button.setOnClickListener { emailRegisterOnClick(view) }

    }

    private fun validateEmailAndPass(userEmail: String, passwd: String, retypePasswd: String): Boolean {
        return if (validateEmailAndPasswd(userEmail,passwd)) {
            when {
                passwd.length < 8 -> {
                    password.error = getString(R.string.error_incorrect_password)
                    false
                }
                retypePasswd.length < 8 -> {
                    retype_new_password.error = getString(R.string.error_incorrect_password)
                    false
                }
                retypePasswd != passwd ->{
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
        LOG.info("Clicking google sign in button")
        println("test")

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
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, FindCityActivity::class.java))
        } else {
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            LOG.info("Logged in as: " + account.displayName)
            Toast.makeText(this, "Logged in as: " + account.displayName, Toast.LENGTH_LONG).show()

            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Log.i("dupa", "jaś")
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while logging in 2", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun firebaseRegisteWithPassword(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while registrating", Toast.LENGTH_LONG).show()
                    }

                }
    }

    private fun firebaseAuthWithPassword(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while logging", Toast.LENGTH_LONG).show()
                    }

                }
    }
    fun firebaseAuthAnonymusly(view: View) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {

                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while logging", Toast.LENGTH_LONG).show()
                    }

                }
    }


    fun firebaseLogOut(view: View) {
        mAuth.signOut()
        mGoogleSignInCLient.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}


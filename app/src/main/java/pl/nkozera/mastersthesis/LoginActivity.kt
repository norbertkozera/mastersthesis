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
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import com.google.firebase.auth.FirebaseUser
import android.support.design.widget.Snackbar
import com.google.firebase.auth.AuthResult
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


class LoginActivity : AppCompatActivity() {
    private val LOG = LoggerFactory.getLogger("LoginActivity")

    lateinit var mGoogleSignInCLient: GoogleSignInClient
    lateinit var googleSignInOptions: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1;
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
        LOG.info("Logger.info")
        LOG.debug("Logger.debug")
        LOG.warn("Logger.warn")
        LOG.error("Logger.error")
        LOG.trace("Logger.trace")
        println("test")

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
            Toast.makeText(this, "Problem while logging in", Toast.LENGTH_LONG).show()
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Problem while logging in", Toast.LENGTH_LONG).show()
                    }
                }
    }
}

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.content.Intent
import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import android.support.annotation.NonNull
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status


class FindCityActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_your_city)
        mAuth = FirebaseAuth.getInstance()
    }

    private lateinit var mAuth: FirebaseAuth


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        val textVIew: TextView = findViewById<View>(R.id.textView) as TextView
        var username = currentUser?.displayName
        if(username.isNullOrEmpty()) {
            textVIew.text = currentUser?.email
        } else {
            textVIew.text = currentUser?.displayName
        }


    }


    fun firebaseLogOut(view: View){
        mAuth.signOut()
        Toast.makeText(this, mAuth.currentUser?.toString(), Toast.LENGTH_LONG).show()

        startActivity(Intent(this, LoginActivity::class.java))
    }

}
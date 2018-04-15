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
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import kotlinx.android.synthetic.main.activity_find_your_city.*
import kotlinx.android.synthetic.main.activity_login.*


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
        var useremail = currentUser?.email
        var url = currentUser?.photoUrl

        if(url !=null)
            Glide.with(this).load(url).into(imageView2)

        if(!username.isNullOrEmpty()) {
            textVIew.text = currentUser?.displayName
        } else if(!useremail.isNullOrEmpty()){
            textVIew.text = currentUser?.email
        } else {
            textVIew.text = "Uzytkownik niezalogowany"

        }

        //LF4959GhtiYn4jWU3saqADhcOmn1
        //LF4959GhtiYn4jWU3saqADhcOmn1

    }


    fun firebaseLogOut(view: View){
        mAuth.signOut()
        LoginManager.getInstance().logOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
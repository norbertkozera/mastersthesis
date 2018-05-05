/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.base;

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pl.nkozera.mastersthesis.LoginActivity
import pl.nkozera.mastersthesis.R

open class BaseActivity : AppCompatActivity() {


    lateinit var mAuth: FirebaseAuth
    lateinit var firebase: FirebaseDatabase
    open lateinit var storage: FirebaseStorage
    lateinit var loginManager: LoginManager
    lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        firebase = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        loginManager = LoginManager.getInstance()
        storageRef = storage.reference

    }

    fun firebaseLogOut() {
        showProgressBar()
        mAuth.signOut()
        LoginManager.getInstance().logOut()

        when (mAuth.currentUser) {
            null -> startActivity(Intent(this, LoginActivity::class.java))
            else -> Toast.makeText(this, getText(R.string.could_not_logout), Toast.LENGTH_LONG).show()
        }


    }

    fun hideProgressBar(content: Int) {
        setContentView(content)
    }

    fun showProgressBar() {
        setContentView(R.layout.progress)
    }


    fun makeToast(toastMsg: String) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()
    }

    fun makeToast(toastLen: Int, toastMsg: String) {
        Toast.makeText(this, toastMsg, toastLen).show()
    }


}

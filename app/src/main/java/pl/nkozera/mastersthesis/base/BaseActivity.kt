/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */


package pl.nkozera.mastersthesis.base;

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.annotations.NotNull
import pl.nkozera.mastersthesis.LoginActivity
import pl.nkozera.mastersthesis.R

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        firebase = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        loginManager = LoginManager.getInstance()
        storageRef = storage.reference

    }

    fun askForPermission(@NotNull vararg permissions: String) {
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, ASK_FOR_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ASK_FOR_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.permission_allowed), Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    lateinit var mAuth: FirebaseAuth
    lateinit var firebase: FirebaseDatabase
    open lateinit var storage: FirebaseStorage
    lateinit var loginManager: LoginManager
    lateinit var storageRef: StorageReference
    private val ASK_FOR_PERMISSIONS: Int = 3
}

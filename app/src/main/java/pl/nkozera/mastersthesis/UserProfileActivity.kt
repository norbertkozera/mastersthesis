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

package pl.nkozera.mastersthesis

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user_profile.*
import pl.nkozera.mastersthesis.auth.PasswordValidator
import pl.nkozera.mastersthesis.base.BaseMenuActivity

class UserProfileActivity : BaseMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        loadUserAvatar()
        loadUserData()
    }

    fun logout(@Suppress("UNUSED_PARAMETER") view: View) {
        firebaseLogOut()
    }

    fun changePass(@Suppress("UNUSED_PARAMETER") view: View) {
        user_info.visibility = View.GONE
        change_pwd.visibility = View.VISIBLE

    }


    fun confirmChangePass(@Suppress("UNUSED_PARAMETER") view: View) {
        if (PasswordValidator(this, new_password, retype_new_password).validate()) {
            updateUserPassword(mAuth.currentUser!!, old_password.text.toString(), new_password.text.toString())
        }
    }

    private fun updateUserPassword(currentUser: FirebaseUser, oldPass: String, newPass: String) {
        currentUser.reauthenticate(EmailAuthProvider.getCredential(currentUser.email.toString(), oldPass))
                .addOnCompleteListener(this) {
                    when {
                        it.isSuccessful -> {
                            currentUser.updatePassword(newPass).addOnCompleteListener(this ){
                                when {
                                    it.isSuccessful -> {
                                        makeToast(getString(R.string.password_changed))
                                        change_pwd.visibility = View.GONE
                                        user_info.visibility = View.VISIBLE
                                    }
                                    else -> makeToast(getString(R.string.password_not_changed))
                                }
                            }
                        }
                        else -> makeToast(getString(R.string.old_pass_incorrect))
                    }
                }
    }


    private fun loadUserData() {
        val name = mAuth.currentUser?.displayName
        val email = mAuth.currentUser?.email

        when {
            mAuth.currentUser?.isAnonymous!! -> {
                userName.text = getString(R.string.anonymus_user)
                userEmail.visibility = View.GONE
                logout_button.text = getString(R.string.login_or_register)
            }
            mAuth.currentUser?.providers!!.contains("password") -> {
                userName.text = name.toString()
                userEmail.text = email.toString()
                change_pass_button.visibility = View.VISIBLE
            }
            else -> {
                userName.text = name.toString()
                userEmail.text = email.toString()
            }
        }
    }

    private fun loadUserAvatar() {
        if (!isPhotoLoaded) {
            userAvatar = mAuth.currentUser?.photoUrl

            if (userAvatar != null) {
                Glide.with(this).load(userAvatar).apply(RequestOptions().placeholder(R.drawable.profile_placeholder).centerCrop()).into(avatar_image_view)
            }
        }
    }

    private var userAvatar: Uri? = null
    private var isPhotoLoaded = false


}
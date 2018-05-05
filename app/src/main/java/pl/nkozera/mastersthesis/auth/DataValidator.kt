/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.auth

import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import pl.nkozera.mastersthesis.R

class DataValidator {
//
//use it:
//    http://www.baeldung.com/kotlin-sealed-classes


//    fun validateEmail(email: String) {
//        when {
//            email.isNullOrEmpty() -> FieldValidator.EMPTY_EMAIL
//        }
//    }
//
//    private fun validateEmailAndPasswd(userEmail: String, passwd: String): Boolean {
//        return if (userEmail.isEmpty()) {
//            email.requestFocus()
//            email.error = getString(R.string.error_field_is_required)
//            false
//        } else if (passwd.isEmpty()) {
//            password.requestFocus()
//            password.error = getString(R.string.error_field_is_required)
//            false
//        } else if (!userEmail.contains("@") ||
//                !userEmail.substring(userEmail.indexOf("@")).contains(".")) {
//            email.requestFocus()
//            email.error = getString(R.string.error_invalid_email)
//            false
//        } else {
//            true
//        }
//    }
//
//
//    private fun validateEmailAndPass(userDisplayName: String, userEmail: String, passwd: String, retypePasswd: String): Boolean {
//        return if (validateEmailAndPasswd(userEmail, passwd)) {
//            when {
//                userDisplayName.isEmpty() -> {
//                    displayName.requestFocus()
//                    displayName.error = getString(R.string.error_field_is_required)
//                    false
//                }
//                passwd.length < 8 -> {
//                    displayName.requestFocus()
//                    password.error = getString(R.string.error_incorrect_password)
//                    false
//                }
//                retypePasswd.length < 8 -> {
//                    displayName.requestFocus()
//                    retype_new_password.error = getString(R.string.error_incorrect_password)
//                    false
//                }
//                retypePasswd != passwd -> {
//                    retype_new_password.requestFocus()
//                    retype_new_password.error = getString(R.string.error_different_passwords)
//                    false
//                }
//                else -> true
//            }
//        } else {
//            false
//        }

 //  }

}
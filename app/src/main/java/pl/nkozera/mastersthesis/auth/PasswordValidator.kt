/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.auth

import android.content.Context
import android.widget.EditText
import pl.nkozera.mastersthesis.R


class PasswordValidator(private val context: Context, vararg val fields: EditText) {

    private var returnFlag: Boolean = true

    fun validate(): Boolean {
        checkIfFieldsAreNotEmpty()
        checkPasswordLength()
        checkIfPasswordsAreTheSame()
        return returnFlag
    }

    private fun checkPasswordLength() {
        for (field in fields) {
            if (returnFlag && field.text.toString().length < 8) {
                field.requestFocus()
                field.error = context.getString(R.string.error_incorrect_password)
                returnFlag = false
            }
        }
    }

    private fun checkIfFieldsAreNotEmpty() {
        for (field in fields) {
            if (returnFlag && field.text.toString().isEmpty()) {
                field.requestFocus()
                field.error = context.getString(R.string.error_field_is_required)
                returnFlag = false
                break
            }
        }

    }

    private fun checkIfPasswordsAreTheSame() {
        var temp: String
        for (i in 1..fields.size-1) {
            if (returnFlag && !fields.get(i).text.toString().equals(fields.get(i - 1).text.toString())) {
                fields.get(i).requestFocus()
                fields.get(i).error = context.getString(R.string.error_different_passwords)
                returnFlag = false
            }
        }
    }


}

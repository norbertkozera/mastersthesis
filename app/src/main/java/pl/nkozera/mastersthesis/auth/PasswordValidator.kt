/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.auth

import android.content.Context
import android.widget.EditText
import pl.nkozera.mastersthesis.R


class PasswordValidator(private val context: Context, vararg val fields: EditText) : FieldValidator() {

    private var returnFlag: Boolean = true

    fun validate(): Boolean {
        checkIfPasswordsAreTheSame()
        checkPasswordLength()
        checkIfFieldsAreNotEmpty(context, fields.toList())
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

    private fun checkIfPasswordsAreTheSame() {
        for (i in 1..fields.size - 1) {
            if (returnFlag && !fields.get(i).text.toString().equals(fields.get(i - 1).text.toString())) {
                fields.get(i).requestFocus()
                fields.get(i).error = context.getString(R.string.error_different_passwords)
                returnFlag = false
            }
        }
    }


}

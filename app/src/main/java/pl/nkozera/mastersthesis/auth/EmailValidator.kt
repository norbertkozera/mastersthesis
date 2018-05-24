/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.auth

import android.content.Context
import android.widget.EditText
import pl.nkozera.mastersthesis.R


class EmailValidator(private val context: Context, vararg val fields: EditText) : FieldValidator() {

    private var returnFlag: Boolean = true

    fun validate(): Boolean {
        checkIfFieldsAreNotEmpty(context, fields.toList())
        checkEmail()
        return returnFlag
    }

    private fun checkEmail() {
        for (field in fields) {
            if (returnFlag &&
                    (!field.text.toString().contains("@") ||
                            !field.text.toString().substring(field.text.toString().indexOf("@")).contains(".")
                            )
            ) {
                field.requestFocus()
                field.error = context.getString(R.string.error_invalid_email)
                returnFlag = false
            }
        }


    }


}

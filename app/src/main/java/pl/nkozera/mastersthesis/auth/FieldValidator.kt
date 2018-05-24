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


open class FieldValidator {


    fun checkIfFieldsAreNotEmpty(context: Context, fields: List<EditText>): Boolean {
        for (field in fields) {
            if (field.text.toString().isEmpty()) {
                field.requestFocus()
                field.error = context.getString(R.string.error_field_is_required)
                return false
            }
        }
        return true
    }

    fun checkIfFieldsAreNotEmpty(context: Context, vararg fields: EditText): Boolean {
        return checkIfFieldsAreNotEmpty(context, fields.toList())
    }


}

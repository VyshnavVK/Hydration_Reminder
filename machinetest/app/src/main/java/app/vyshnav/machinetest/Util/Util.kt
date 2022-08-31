package app.vyshnav.machinetest.Util

import android.util.Patterns
import android.widget.EditText

object Util {
    /**
     * Use this validator for email validation;
     * return true if the email is valid
     **/
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}

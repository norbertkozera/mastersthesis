/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.slf4j.LoggerFactory


class LoginActivity : AppCompatActivity() {
    private val LOG = LoggerFactory.getLogger("LoginActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun emailSignInOnClick(view: View) {
        LOG.info("Logger.info")
        LOG.debug("Logger.debug")
        LOG.warn("Logger.warn")
        LOG.error("Logger.error")
        LOG.trace("Logger.trace")

    }
}

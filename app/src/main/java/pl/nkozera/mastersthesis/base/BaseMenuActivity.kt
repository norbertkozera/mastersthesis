/*
 * Master Thesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.base

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import pl.nkozera.mastersthesis.FindCityActivity
import pl.nkozera.mastersthesis.LoginActivity
import pl.nkozera.mastersthesis.R
import pl.nkozera.mastersthesis.UserProfileActivity


open class BaseMenuActivity : BaseActivity() {


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.getItem(0).title = getUserName()
        return true
    }

//    override fun onBackPressed() {
//        flag++
//        if (flag < 2) {
//            Toast.makeText(this, getString(R.string.back_again_logout), Toast.LENGTH_SHORT).show()
//        } else {
//            firebaseLogOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.search -> {
                startActivity(Intent(this, FindCityActivity::class.java))
                true
            }
            R.id.results -> {
                Toast.makeText(this, getString(R.string.prompt_no_res), Toast.LENGTH_SHORT).show()
                false
            }
            R.id.menu_item_logout -> {
                firebaseLogOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getUserName(): String {
        return when {
            mAuth.currentUser?.isAnonymous!! -> getString(R.string.anonymus_user)
            else -> mAuth.currentUser?.displayName as String
        }
    }

    private var flag = 0
}

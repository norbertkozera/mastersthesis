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

/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.base

import android.view.Menu
import android.view.MenuItem
import pl.nkozera.mastersthesis.R


open class BaseMenuActivity : BaseActivity() {


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.getItem(0).setTitle(getUserName())
//        setUserzPhoto(menu.getItem(0))


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
        // R.id.avatar -> true
            R.id.menu_item_logout -> {
                firebaseLogOut()
                false
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

//    private fun setUserzPhoto(item: MenuItem) {
//        val url = mAuth.currentUser?.photoUrl
//        val imageUrl: URL = URL(url.toString())
//        val imageView = ImageView(this)
//        imageView.maxHeight = 10
//        imageView.maxWidth = 10
//        when (imageUrl) {
//            null -> imageView.setImageResource(R.drawable.profile_placeholder)
//            else -> {
//                showProgressBar()
//                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//                StrictMode.setThreadPolicy(policy)
//                var inputStram = imageUrl.openStream()
//                var drawable = Drawable.createFromStream(inputStram, "src")
//                imageView.setImageDrawable(drawable)
//
//            }
//        }
//
//        item.setActionView(imageView)
//
//    }

}

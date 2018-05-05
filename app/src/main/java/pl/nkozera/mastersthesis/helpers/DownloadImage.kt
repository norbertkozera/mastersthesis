/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.helpers

import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.ImageView

class DownloadImage : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg params: String?): Bitmap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var image: ImageView


}
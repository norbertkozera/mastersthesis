
/*
 * Master Thiesis project
 * All rights reserved
 * Created by Norbert Kozera <nkozera@gmail.com>
 */

package pl.nkozera.mastersthesis.place

class PlaceComment(
        private val commentatorName: String,
        private val comment: String,
        private val time: String,
        private val avatar: String,
        private val rating: Double) {


    fun getCommentatorName(): String {
        return commentatorName
    }

    fun getComment(): String {
        return comment
    }
    fun getTime(): String {
        return time
    }

    fun getAvatar(): String {
        return avatar
    }

    fun getRating(): Double {
        return rating
    }


}
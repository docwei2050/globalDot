package com.docwei.doblib

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.docwei.doblib.ZeTaFrontBackgroundContentProvider.Companion.APP_COUNT

/**
 * Created by liwk on 2021/6/13.
 */
class ZeTaFrontBackgroundHelper(context: Context) {
    var contentResolver: ContentResolver? = null
    var appCountUri: Uri? = null

    init {
        contentResolver = context.contentResolver
        appCountUri = Uri.parse("content://" + ZeTaFrontBgContentProvider + "/" + APP_COUNT)
    }

    fun commitAppCount(count: Int) {
        val values = ContentValues()
        values.put(APP_COUNT, count)
        contentResolver?.insert(appCountUri!!, values)
    }

    fun getAppCount():Int {
        var appCount = 0
        val cursor = contentResolver?.query(
            appCountUri!!,
            listOf<String>(APP_COUNT).toTypedArray(),
            null,
            null,
            null
        )
        cursor?.let {
            if (it.count > 0) {
                while (cursor.moveToNext()) {
                    appCount = cursor.getInt(0)
                }
            }
            it.close()
        }
        return  appCount
    }

    companion object {
        const val ZeTaFrontBgContentProvider = "com.docwei.globaldot.ZeTaFrontBackgroundContentProvider"
    }
}
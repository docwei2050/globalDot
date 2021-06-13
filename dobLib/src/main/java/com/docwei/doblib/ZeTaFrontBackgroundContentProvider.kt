package com.docwei.doblib

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log

/**
 * Created by liwk on 2021/6/13.
 */
class ZeTaFrontBackgroundContentProvider : ContentProvider() {
    var uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    var sp: SharedPreferences? = null
    var mEditor: SharedPreferences.Editor? = null
    var mContentResolver: ContentResolver? = null
    override fun onCreate(): Boolean {
        Log.e("ZeTadot","cp onCerat")
            if (context != null) {
            uriMatcher.addURI("com.docwei.globaldot.ZeTaFrontBackgroundContentProvider", APP_COUNT, 1)
            sp = context!!.getSharedPreferences("zetaDataApi", Context.MODE_PRIVATE)
            mEditor = sp?.edit()
            mEditor?.apply()
            mContentResolver = context!!.contentResolver
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code = uriMatcher.match(uri)
        var matrixCursor: MatrixCursor? = null
        when (code) {
            1 -> {
                val count = sp?.getInt(APP_COUNT, 0)
                matrixCursor = MatrixCursor(listOf<String>(APP_COUNT).toTypedArray())
                matrixCursor.addRow(listOf<Any?>(count).toTypedArray())
            }
        }
        return matrixCursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) {
            return uri
        }
        val code = uriMatcher.match(uri)
        when (code) {
            1 -> {
                val count = values.getAsInteger(APP_COUNT)
                mEditor?.putInt(APP_COUNT, count)
                mContentResolver?.notifyChange(uri, null)
            }
        }
        mEditor?.commit()
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    companion object {
        const val APP_COUNT = "app_count";
    }
}
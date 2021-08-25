package com.openclassrooms.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.data.repository.RealEstateRepository
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class RealEstateProvider: ContentProvider() {

    companion object {
        val AUTHORITY = "com.openclassrooms.data.provider"
        val TABLE_NAME = "table_estates/1"
        val URI_ITEM = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

//    val ESTATE = 1
/*
    val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "/$TABLE_NAME", ESTATE)
    }
*/
    @Inject lateinit var repositoryAccess: RealEstateRepositoryAccess

    override fun onCreate(): Boolean { return true }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val id = ContentUris.parseId(uri)
        return repositoryAccess.getEstateWithId(id)
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }
}
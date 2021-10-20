package com.openclassrooms.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent


class RealEstateProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.data.provider"
        const val TABLE_NAME = "table_estates/"
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RepositoryEntryPoint {
        fun getRepositoryAccess(): RealEstateRepositoryAccess
    }

    override fun onCreate(): Boolean { return true }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val id = ContentUris.parseId(uri)
        val context = context?.applicationContext ?: throw  IllegalStateException()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, RepositoryEntryPoint::class.java)
        val repositoryAccess = hiltEntryPoint.getRepositoryAccess()
        return repositoryAccess.getCursorEstateWithId(id)
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
package com.openclassrooms.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.data.dao.EstateDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.lang.IllegalStateException

class RealEstateProvider : ContentProvider() {

    companion object {
        val AUTHORITY = "com.openclassrooms.data.provider"
        val TABLE_NAME = "table_estates/" // /1
        val URI_ITEM = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

     @InstallIn(SingletonComponent::class)
     @EntryPoint
     interface EstateDaoEntryPoint {
         fun estateDao() : EstateDao
     }

     private fun getEstateDao(context: Context): EstateDao {
         val hiltEntryPoint = EntryPointAccessors.fromApplication(
             context,
             EstateDaoEntryPoint::class.java
         )
         return hiltEntryPoint.estateDao()
     }
    override fun onCreate(): Boolean { return true }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
         val id = ContentUris.parseId(uri)
         val context = context?.applicationContext ?: throw  IllegalStateException()
          val estateDao: EstateDao = getEstateDao(context)
         return estateDao.getCursorEstateWithId(id)
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
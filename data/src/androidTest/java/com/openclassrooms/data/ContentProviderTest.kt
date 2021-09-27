package com.openclassrooms.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.provider.RealEstateProvider
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertThat


@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    lateinit var contentResolver: ContentResolver
    private val USER_ID: Long = 1

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
        RealEstateManagerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun getItemFromContentProvider() {
        if (contentResolver == null)  Log.i("CHECK_RESOLVER", "NULL")

        val cursor: Cursor? = contentResolver.query(
            ContentUris.withAppendedId(RealEstateProvider.URI_ITEM, USER_ID),
            null,
            null,
            null,
            null)


        if (cursor == null)   Log.i("CHECK_CURSOR", "NULL")
        cursor?.let {
            Log.i("CHECK_CURSOR", "Column count : ${it.columnCount}")
            Log.i("CHECK_CURSOR", "Count : ${it.count}")
            it.columnNames.forEach {
                Log.i("CHECK_CURSOR", "Column name : ${it}")
            }
            assertThat(cursor, notNullValue())

        }
        cursor?.close()
    }
}
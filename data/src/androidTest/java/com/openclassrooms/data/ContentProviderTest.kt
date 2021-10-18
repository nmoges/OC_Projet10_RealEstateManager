package com.openclassrooms.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.provider.RealEstateProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


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
        val cursor: Cursor? = contentResolver.query(
            ContentUris.withAppendedId(RealEstateProvider.URI_ITEM, USER_ID),
            null,
            null,
            null,
            null)
        assertNotNull(cursor)
        cursor?.let {
            assertEquals(7, it.columnCount)
            val columnsName = it.columnNames
            assertEquals("id_estate", columnsName[0])
            assertEquals("type", columnsName[1])
            assertEquals("price", columnsName[2])
            assertEquals("description", columnsName[3])
            assertEquals("id_agent", columnsName[4])
            assertEquals("status", columnsName[5])
            assertEquals("id_firebase", columnsName[6])
        }
        cursor?.close()
    }
}
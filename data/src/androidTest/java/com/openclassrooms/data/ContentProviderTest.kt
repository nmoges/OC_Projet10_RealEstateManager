package com.openclassrooms.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.data.provider.RealEstateProvider
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertThat
/*
@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    lateinit var contentResolver: ContentResolver
    val USER_ID: Long = 1

    @Before
    private fun setUp() {
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    private fun getItemFromContentProvider() {
        val cursor: Cursor? = contentResolver.query(
            ContentUris.withAppendedId(RealEstateProvider.URI_ITEM, USER_ID),
            null,
            null,
            null,
            null)

        cursor?.let {
            assertThat(cursor, notNullValue())
        }
    }
}*/
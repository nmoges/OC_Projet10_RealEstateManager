package com.openclassrooms.data.entities

import androidx.room.ColumnInfo

class EntryDateData(
    @ColumnInfo(name = "entry_date_day")
    var entryDateDay: Int,

    @ColumnInfo(name = "entry_date_month")
    var entryDateMonth: Int,

    @ColumnInfo(name = "entry_date_year")
    var entryDateYear: Int,
)
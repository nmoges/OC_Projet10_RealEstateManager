package com.openclassrooms.data.entities.date

import androidx.room.ColumnInfo

class EntryDateData(
    @ColumnInfo(name = "entry_date_day")
    var day: Int,

    @ColumnInfo(name = "entry_date_month")
    var month: Int,

    @ColumnInfo(name = "entry_date_year")
    var year: Int,
)
package com.openclassrooms.data.entities.date

import androidx.room.ColumnInfo

data class SaleDateData(
    @ColumnInfo(name = "sale_date_day")
    var day: Int,

    @ColumnInfo(name = "sale_date_month")
    var month: Int,

    @ColumnInfo(name = "sale_date_year")
    var year: Int
)
package com.openclassrooms.data.entities

import androidx.room.ColumnInfo

data class SaleDateData(
    @ColumnInfo(name = "sale_date_day")
    var saleDateDay: Int,

    @ColumnInfo(name = "sale_date_month")
    var saleDateMonth: Int,

    @ColumnInfo(name = "sale_date_year")
    var saleDateYear: Int
)
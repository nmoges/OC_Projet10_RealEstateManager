package com.openclassrooms.data.entities.date

import androidx.room.ColumnInfo

/**
 * Defines columns for entity [DatesData].
 * @param day : day
 * @param month : month
 * @param year : year
 */
data class SaleDateData(
    @ColumnInfo(name = "sale_date_day")
    var day: Int,

    @ColumnInfo(name = "sale_date_month")
    var month: Int,

    @ColumnInfo(name = "sale_date_year")
    var year: Int
)
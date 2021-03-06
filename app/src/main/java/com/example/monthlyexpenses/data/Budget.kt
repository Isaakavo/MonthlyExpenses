package com.example.monthlyexpenses.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_table")
data class Budget(
    @ColumnInfo(name = "budgetForFirstFortnight")
    var budgetForFirstFortnight: Float = 0F,
    @ColumnInfo(name = "budgetForSecondFortnight")
    var budgetForSecondFortnight: Float = 0F,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "month")
    var month: Long = 0L
) {
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id")
//    var id: Long = 0
}
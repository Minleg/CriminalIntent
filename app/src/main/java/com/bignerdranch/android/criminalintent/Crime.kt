package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity // indicates that the class defines the structure of a table or a set of tables, in the database
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(), // UUID - utility class - provides an easy way to generate universally unique ID values
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = "",
)

package com.bignerdranch.android.criminalintent

import java.util.*

data class Crime(
    val id: UUID = UUID.randomUUID(), // UUID - utility class - provides an easy way to generate universally unique ID values
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
)

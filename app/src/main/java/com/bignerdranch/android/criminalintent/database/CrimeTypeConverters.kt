package com.bignerdranch.android.criminalintent.database

import androidx.room.TypeConverter
import java.util.*

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? { // informs Room how to convert the date datatype in Entity class to a valid datatype in Room db
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? { // informs Room how to convert the data back to its original form in Entity class
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}

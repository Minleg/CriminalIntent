package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent.Crime

@Database(
    entities = [Crime::class],
    version = 1,
) // list of entity class - first parameter, version of db as second parameter, increment the version as you make changes
@TypeConverters(CrimeTypeConverters::class) // required to explicitly add the converters to the database
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao // hooks up your DAO to your database. The abstract fun returns CrimeDao object
}

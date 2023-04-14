package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent.Crime

@Database(
    entities = [Crime::class],
    version = 1,
) // list of entity class - first parameter, version of db as second parameter, increment the version as you make changes
@TypeConverters(CrimeTypeConverters::class) // required to explicitly add the converters to the database
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao // hooks up your DAO to your database. The abstract fun returns CrimeDao object
}

// to tell Room how to migrate your database between the one version to another, provide a Migration
val migration_1_2 = object : Migration(1, 2) { // initial database version was set to 1, bump it up to 2.
    override fun migrate(database: SupportSQLiteDatabase) { // Room uses SQLite under the hood
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''",
        )
    }
}

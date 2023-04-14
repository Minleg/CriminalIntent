package com.bignerdranch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintent.database.CrimeDatabase
import com.bignerdranch.android.criminalintent.database.migration_1_2
import java.util.UUID
import java.util.concurrent.Executors

/* Repository class encapsulates the logic for accessing data from a single source or a set of sources.
* It determines how to fetch and store a particular set of data, whether locally in a database or from a remote server.*/

// CrimeRepository is a singleton which means there will only ever be one instance of it in your app.
// A singleton exists as long as the application stays in memory, so storing any properties on the singleton will keep
// them available throughout any lifecycle changes in your activities and fragments. They are destroyed  when Android removes
// your application from memory. singleton class - not a solution for long-term storage.

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {
// mark constructor as private to ensure no components can go rogue and create their own instance

    // creates a concrete implementation of your abstract CrimeDatabase using three parameters
    private val database: CrimeDatabase = Room.databaseBuilder(
        // references to your database object
        context.applicationContext, // Context object, since the db is accessing the filesystem
        CrimeDatabase::class.java, // Database class that you want Room to create
        DATABASE_NAME, // name of the database file you want Room to create for you
    ).addMigrations(migration_1_2) // migration needs to be provided to your database when creating the DB instance
        .build() // variable number of migration objects can be provided

    private val crimeDao = database.crimeDao() // references for your DAO object
    private val executor =
        Executors.newSingleThreadExecutor() // returns an executor instance that points to a new thread

    // add function to your repository for each function in your DAO
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute { // pushes these operations off the main thread so you do not block your UI
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    companion object {
        // To make CrimeRepository a singleton, add two functions to its companion object.
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) { // initializes a new instance of the repository
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository { // accesses the repository
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
            // exception is to ensure that you initialize your repository when your application is starting
        }
    }
}

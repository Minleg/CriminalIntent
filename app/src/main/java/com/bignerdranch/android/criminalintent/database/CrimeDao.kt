package com.bignerdranch.android.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalintent.Crime
import java.util.UUID

/* Data Access Object or DAO is an interface that contains functions for each database operation
you want to perform.
@Query indicates the function pulls information out of the database rather than inserting, updating or deleting items from the database
The return type of the query function in DAO interface indicates the type of result the query will return
You need to register your DAO class with your database class*/
@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime") // requires String containing SQL command as input
    fun getCrimes(): LiveData<List<Crime>> // by returning LiveData, you signal Room to run your query on a background thread
    // when the query completes, the LiveData object will handle sending the crime data over to the main thread and notify any observers

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>
}

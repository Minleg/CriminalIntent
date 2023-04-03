package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = // stores the ID of the crime currently display or about to be displayed by CrimeFragment
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            /* A live data transformation is a way to set up a trigger-response relationship between two
            * LiveData objects. A transformation function takes two inputs:
            * a LiveData object used as a trigger and
            * a mapping function that must return a LiveData object.*/
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }
}
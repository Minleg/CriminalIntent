package com.bignerdranch.android.criminalintent

import android.app.Application

class CriminalIntentApplication : Application() {

    override fun onCreate() { // called when your application is first loaded into memory
        // good place to do any kind of one-time initialization operations.
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}
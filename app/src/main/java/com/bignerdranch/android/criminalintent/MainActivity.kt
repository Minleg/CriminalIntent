package com.bignerdranch.android.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Activity's FragmentManager is accessed using the supportFragmentManager property
        // you can retrieve the CrimeFragment from the FragmentManager by using containerView ID
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null ) {
            val fragment = CrimeFragment()
            // creates and commits a fragment transaction (used to add, remove, attach, detach or replace fragment in fragment list)
            // can add multiple fragments to different containers at the same time.
            supportFragmentManager
                .beginTransaction() // creates and returns a new instance of FragmentTransaction
                .add(R.id.fragment_container, fragment) // containerView id and newly created CrimeFragment
                .commit()
            /*FragmentManager maintains a back stack of fragment transactions that you navigate.
            * If your fragment transaction includes multiple operations, they are reversed when the transaction is removed from the back stack */
        }
    }
}

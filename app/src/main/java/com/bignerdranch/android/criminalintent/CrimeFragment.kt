package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment // use this Jetpack(AndroidX) version in all new android apps (Don't use the Android OS version android.app.fragment)

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) { // Fragment lifecycle functions must be public as they will be accessed by the activity hosting the fragment
        super.onCreate(savedInstanceState) // Activity's onCreate() function is protected
        // You don't inflate the fragment's view in onCreate() - unlike the case with Activity
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        /* This function is where we inflate the fragment's view and return the inflated View to the hosting activity
        * LayoutInflater and ViewGroup are necessary to inflate the layout.
        * The Bundle will contain data that this function can use to re-create the view from a saved state*/
        // return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        // second parameter is the view's parent. Third parameter tells the layout inflater whether to immediately add the inflated view
        // to the view's parent. Pass false as fragment's view will be hosted in the activity's container view and the fragment's
        // view does not need to be added to the parent view immediately - the activity will handle adding the view later.
        titleField = view.findViewById(R.id.crime_title) as EditText // View.findViewById(Int) - even Activity does the same behind the scenes
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false // disables the button
        }
        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher { // anonymous class that implements the verbose TextWatcher interface
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(sequece: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequece.toString() // charsequence is the user input and that is set to the crime title
            }

            override fun afterTextChanged(sequence: Editable?) {
                //
            }
        }
        titleField.addTextChangedListener(titleWatcher) // this listener is triggered when data is set on them when the view state is restored, such as on rotation
        // listeners that only react to user interaction - eg onClickListener
        /* View state is restored after onCreateView() and before onStart(). When the state is restored, the contents of the
        * Edit Text will get set to whatever value is currently in crime.title. At this point, if you have already set a
        * listener on the Edit Text,TestWatcher's functions will execute.
        * Setting the listener in onStart() avoids this behavior since the listener is hooked up after the view state is restored*/

        solvedCheckBox.apply {
            setOnCheckedChangeListener {  _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}

package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment // use this Jetpack(AndroidX) version in all new android apps (Don't use the Android OS version android.app.fragment)
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Fragment lifecycle functions must be public as they will be accessed by the activity hosting the fragment
        super.onCreate(savedInstanceState) // Activity's onCreate() function is protected
        // You don't inflate the fragment's view in onCreate() - unlike the case with Activity
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
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
        titleField =
            view.findViewById(R.id.crime_title) as EditText // View.findViewById(Int) - even Activity does the same behind the scenes
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object :
            TextWatcher { // anonymous class that implements the verbose TextWatcher interface
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                //
            }

            override fun onTextChanged(
                sequece: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                crime.title =
                    sequece.toString() // charsequence is the user input and that is set to the crime title
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
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply { // Intent.ACTION_SEND is a string that is a constant defining the action
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject),
                )
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI,
                ) // Intent for requesting a contact with ACTION_PICK

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            // pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            val packageManager: PackageManager = requireActivity().packageManager
            // PackageManager checks to see if there is an app that can handle this request - if there is a contacts app to look up to
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(
                    pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY, // only activities with the CATEGORY_DEFAULT flag
                )
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
    }

    override fun onStop() { // this function is called any time your fragment moves entirely out of the view - i.e. on stopped state
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = requireActivity().contentResolver
                    .query(contactUri!!, queryFields, null, null, null)
                cursor?.use {
                    // Verify cursor contains at least one result
                    if (it.count == 0) {
                        return
                    }

                    // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            /* This function creates Fragment instance and bundles up and set its arguments */
            val args =
                Bundle().apply {
                    // fragment arguments allow you to stash pieces of data someplace that belongs to the fragment
                    putSerializable(ARG_CRIME_ID, crimeId)
                }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}

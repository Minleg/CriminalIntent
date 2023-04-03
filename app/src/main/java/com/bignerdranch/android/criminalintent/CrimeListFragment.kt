package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    /**
     * Required interface for hosting activities **/
    interface Callbacks {
        // defines work that the fragment needs done by its boss, the hosting activity
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) { // context refers to the activity instance hosting the fragment
        /* Fragment.onAttach(Context) lifecycle function is called when a fragment is attached to an activity */
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            // this function registers an observer on the LiveData instance
            // and tie the life of the observation to the life of another component, such as an activity or fragment
            viewLifecycleOwner, // you scope the observer to the life of the fragment's view. viewLifecycleOwner here
            Observer { crimes -> // observer implementation is responsible for reacting to new data from the LiveData.
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            },
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        /* sets  up CrimeListFragments UI. connect Adapter to your RecyclerView*/
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    /* The RecyclerView expects an item view to be wrapped in an instance of ViewHolder. A ViewHolder
    * stores a reference to an item's view ( and sometimes to specific widgets within that view)
    * A RecyclerView never creates Views by themselves. It always creates ViewHolders, which bring their itemViews along for the ride*/
    private inner class CrimeHolder(view: View) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime // cache the Crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.getDateInstance().format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            // Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    /* RecyclerView does not create ViewHolders itself. It asks an adapter - a controller object to create the necessary
    * ViewHolders when asked and bind ViewHolders to data from the model layer when asked */
    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            // creates a view to display, wraps the view in a ViewHolder and returns the result - at the call of the RecyclerView
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size

        /*RecyclerView doesn't know anything about the model/data - Crime object in this case. Instead the Adapter
        * CrimeAdapter here which sits between the RecyclerView and the model knows all about the model, i.ed  Crime's
        * intimate and personal details*/
        // RecyclerView calls this method, passing a ViewHolder along with a position. Adapter looks up the model data for that position
        // and bind it to the ViewHolder's View.
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            // responsible for populating a given holder with the crime from a given position
            val crime = crimes[position]
            holder.bind(crime)
        }
    }

    companion object {
        fun newInstance(): CrimeListFragment { // activities can call this fun to get an instance of this fragment
            return CrimeListFragment()
        }
    }
}

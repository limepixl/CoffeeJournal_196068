package mk.ukim.finki.coffeejournal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.adapters.JournalEntriesAdapter
import mk.ukim.finki.coffeejournal.adapters.JournalEntriesHeader
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModel
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModelFactory

class ViewJournalEntriesFragment : Fragment() {
    private lateinit var rvJournalCards: RecyclerView
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var journalEntries: LiveData<List<JournalEntry>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalViewModel =
            JournalViewModelFactory(requireContext()).create(JournalViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_journal_entries, container, false)

        var headerAdapter = JournalEntriesHeader()
        var recyclerViewAdapter = JournalEntriesAdapter()
        val concatAdapter = ConcatAdapter(headerAdapter, recyclerViewAdapter)

        rvJournalCards = view.findViewById(R.id.rvJournalCards)
        rvJournalCards.adapter = concatAdapter

        journalEntries = journalViewModel.getJournalEntriesLiveData()
        journalEntries.observe(viewLifecycleOwner) { newValues ->
            headerAdapter = JournalEntriesHeader(newValues)
            recyclerViewAdapter = JournalEntriesAdapter(newValues)
            rvJournalCards.adapter = ConcatAdapter(headerAdapter, recyclerViewAdapter)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        journalEntries.removeObservers(viewLifecycleOwner)
    }
}
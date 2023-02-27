package mk.ukim.finki.coffeejournal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.ukim.finki.coffeejournal.enums.BrewMethod
import mk.ukim.finki.coffeejournal.repository.JournalEntryRepository
import mk.ukim.finki.coffeejournal.room.model.JournalEntry
import java.util.*

@Suppress("unused")
class JournalViewModel(val journalEntryRepository: JournalEntryRepository) : ViewModel() {
    private val journalEntriesLiveData = MutableLiveData<List<JournalEntry>>()
    private val journalEntry: JournalEntry = JournalEntry()

    fun getJournalEntriesLiveData(): LiveData<List<JournalEntry>> = journalEntriesLiveData

    fun listAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val journalEntries = journalEntryRepository.getAll()
            journalEntriesLiveData.postValue(journalEntries)
        }
    }

    fun setDate(date: Date) {
        journalEntry.date = date.time
    }

    fun getDate() : Date {
        return Date(journalEntry.date)
    }

    fun setBrewMethod(brewMethod: BrewMethod) {
        journalEntry.brewMethod = brewMethod
    }

    fun getBrewMethod() : BrewMethod {
        return journalEntry.brewMethod
    }

    fun setRating(rating: Int) {
        journalEntry.rating = rating.coerceIn(0, 5)
    }

    fun getRating() : Int {
        return journalEntry.rating
    }

    fun setNotes(notes: String) {
        journalEntry.notes = notes
    }

    fun getNotes() : String? {
        return journalEntry.notes
    }
}
package mk.ukim.finki.coffeejournal.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mk.ukim.finki.coffeejournal.repository.JournalEntryRepository
import mk.ukim.finki.coffeejournal.room.RoomDataSource
import mk.ukim.finki.coffeejournal.room.database.AppDatabase

class JournalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            return JournalViewModel(
                JournalEntryRepository(
                    RoomDataSource(AppDatabase.getDatabase(context).journalEntryDao())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
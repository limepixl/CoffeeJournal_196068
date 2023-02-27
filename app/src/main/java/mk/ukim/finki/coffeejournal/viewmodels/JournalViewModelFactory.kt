package mk.ukim.finki.coffeejournal.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mk.ukim.finki.coffeejournal.repository.JournalEntryRepository
import mk.ukim.finki.coffeejournal.room.RoomDataSource
import mk.ukim.finki.coffeejournal.room.database.AppDatabase

class JournalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            JournalEntryRepository::class.java
        ).newInstance(
            JournalEntryRepository(
                RoomDataSource(AppDatabase.getDatabase(context).journalEntryDao())
            )
        )
    }
}
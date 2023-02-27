package mk.ukim.finki.coffeejournal.repository

import mk.ukim.finki.coffeejournal.LocalDataSource
import mk.ukim.finki.coffeejournal.room.model.JournalEntry

class JournalEntryRepository(
    private val localDataSource: LocalDataSource
) {
    suspend fun getAll(): List<JournalEntry> {
        return localDataSource.getAll()
    }

    suspend fun insert(journalEntry: JournalEntry) {
        localDataSource.insert(journalEntry)
    }

    suspend fun insertAll(journalEntries: List<JournalEntry>) {
        for(entry in journalEntries) {
            localDataSource.insert(entry)
        }
    }
}
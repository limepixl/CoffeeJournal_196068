package mk.ukim.finki.coffeejournal.repository

import mk.ukim.finki.coffeejournal.LocalDataSource
import mk.ukim.finki.coffeejournal.room.model.JournalEntry

class JournalEntryRepository(
    private val localDataSource: LocalDataSource
) {
    suspend fun getAll(): List<JournalEntry> {
        return localDataSource.getAll()
    }
}
package mk.ukim.finki.coffeejournal.viewmodels

import androidx.lifecycle.ViewModel
import java.util.Date

enum class BrewMethod {
    ESPRESSO,
    MOKA_POT,
    INSTANT_COFFEE
}

@Suppress("unused")
class JournalViewModel : ViewModel() {
    private var _date: Date = Date()
    private var _brewMethod: BrewMethod = BrewMethod.ESPRESSO
    private var _rating: Int = 0
    private var _notes: String = ""

    fun setDate(date: Date) {
        _date = date
    }

    fun getDate() : Date {
        return _date
    }

    fun setBrewMethod(brewMethod: BrewMethod) {
        _brewMethod = brewMethod
    }

    fun getBrewMethod() : BrewMethod {
        return _brewMethod
    }

    fun setRating(rating: Int) {
        _rating = rating.coerceIn(0, 5)
    }

    fun getRating() : Int {
        return _rating
    }

    fun setNotes(notes: String) {
        _notes = notes
    }

    fun getNotes() : String {
        return _notes
    }
}
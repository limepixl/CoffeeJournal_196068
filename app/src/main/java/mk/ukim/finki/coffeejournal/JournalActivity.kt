package mk.ukim.finki.coffeejournal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import mk.ukim.finki.coffeejournal.fragments.AddJournalEntryFragment

class JournalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                replace<AddJournalEntryFragment>(R.id.fv_journal)
                setReorderingAllowed(true)
            }
        }
    }
}
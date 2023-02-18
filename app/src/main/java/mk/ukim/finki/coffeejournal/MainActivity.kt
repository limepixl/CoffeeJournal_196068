package mk.ukim.finki.coffeejournal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnJournal: Button
    private lateinit var btnHelp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnJournal = findViewById(R.id.btn_journal)
        btnHelp = findViewById(R.id.btn_help)

        btnJournal.setOnClickListener {
            val intent = Intent(this, JournalActivity::class.java)
            startActivity(intent)
        }
    }
}
package mk.ukim.finki.coffeejournal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.viewmodels.BrewMethod
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModel
import java.util.*
import kotlin.math.floor

class AddJournalEntryFragment : Fragment() {
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var radioGroup: RadioGroup
    private lateinit var datePicker: DatePicker
    private lateinit var btnPickDate: Button
    private lateinit var ratingBar: RatingBar
    private lateinit var etNotes: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalViewModel = ViewModelProvider(this)[JournalViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_journal_entry, container, false)

        datePicker = view.findViewById(R.id.date_picker)
        datePicker.setOnDateChangedListener { dpView, year, monthOfYear, dayOfMonth ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            journalViewModel.setDate(calendar.time)
            dpView.visibility = GONE

            btnPickDate.visibility = VISIBLE
            btnPickDate.text = journalViewModel.getDate().toString()
        }

        btnPickDate = view.findViewById(R.id.btn_pick_date)
        btnPickDate.setOnClickListener {
            datePicker.visibility = VISIBLE
            btnPickDate.visibility = GONE
        }

        ratingBar = view.findViewById(R.id.rating_bar)
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                journalViewModel.setRating(floor(rating).toInt())
            }
        }

        radioGroup = view.findViewById(R.id.rg_methods)
        for (value in BrewMethod.values()) {
            val rb = RadioButton(context)
            rb.text = value.toString()
                .replace('_', ' ')
                .lowercase()
                .replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.ROOT)
                    else
                        it.toString()
                }
            rb.id = value.ordinal
            radioGroup.addView(rb)
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId > 0) {
                val index = radioGroup.children.first().id
                journalViewModel.setBrewMethod(BrewMethod.values()[index])
            }
        }
        radioGroup.check(journalViewModel.getBrewMethod().ordinal + 1)

        etNotes = view.findViewById(R.id.et_notes)
        etNotes.doAfterTextChanged {
            journalViewModel.setNotes(it.toString())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radioGroup.removeAllViews()
    }
}
package mk.ukim.finki.coffeejournal.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.enums.BrewMethod
import mk.ukim.finki.coffeejournal.repository.JournalEntryRepository
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModel
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.floor

class AddJournalEntryFragment : Fragment() {
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var radioGroup: RadioGroup
    private lateinit var datePicker: DatePicker
    private lateinit var btnPickDate: Button
    private lateinit var ratingBar: RatingBar
    private lateinit var etNotes: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnPhoto: Button
    private lateinit var ivPhoto: ImageView

    private lateinit var journalEntryRepository: JournalEntryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalViewModel =
            JournalViewModelFactory(requireContext()).create(JournalViewModel::class.java)

        journalEntryRepository = journalViewModel.journalEntryRepository
    }

    @OptIn(DelicateCoroutinesApi::class)
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
            rb.text = value.toString().replace('_', ' ').lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                else it.toString()
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

        ivPhoto = view.findViewById(R.id.iv_journal_photo)

        btnPhoto = view.findViewById(R.id.btn_camera)
        btnPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        btnSubmit = view.findViewById(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            GlobalScope.launch {
                journalEntryRepository.insert(journalViewModel.getJournalEntry())
            }
            this.activity?.finish()
        }

        return view
    }

    private val cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras: Bundle? = data?.extras
            if(extras != null) {
                val stream = ByteArrayOutputStream()
                val bitmap: Bitmap? = extras.getParcelable("data")
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                val photoBytes = stream.toByteArray()
                journalViewModel.setPhoto(photoBytes)

                ivPhoto.setImageBitmap(bitmap)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraActivityLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // ERROR
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radioGroup.removeAllViews()
    }
}
package mk.ukim.finki.coffeejournal.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
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

class AddJournalEntryFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var spinner: Spinner
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

        ratingBar = view.findViewById(R.id.rating_bar)
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                journalViewModel.setRating(floor(rating).toInt())
            }
        }

        spinner = view.findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this

        val brewMethods: List<String> = BrewMethod.values().map { brewMethod ->
            brewMethod.toString().replace('_', ' ').lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                else it.toString()
            }
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, brewMethods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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
            journalViewModel.setDate(Date())
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
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras: Bundle? = data?.extras
            if (extras != null) {
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var selectedItemString = parent?.getItemAtPosition(position) as String
        selectedItemString = selectedItemString.replace(' ', '_').uppercase()

        val selectedItem = BrewMethod.valueOf(selectedItemString)
        journalViewModel.setBrewMethod(selectedItem)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
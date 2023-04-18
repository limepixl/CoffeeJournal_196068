package mk.ukim.finki.coffeejournal.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mk.ukim.finki.coffeejournal.R
import mk.ukim.finki.coffeejournal.enums.BrewMethod
import mk.ukim.finki.coffeejournal.repository.JournalEntryRepository
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModel
import mk.ukim.finki.coffeejournal.viewmodels.JournalViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
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

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val journalViewModelFactory = JournalViewModelFactory(requireContext())
        journalViewModel =
            ViewModelProvider(this, journalViewModelFactory)[JournalViewModel::class.java]

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
            if (!journalViewModel.getNotes().isNullOrEmpty() && journalViewModel.getRating() > 0) {
                journalViewModel.setDate(Date())
                GlobalScope.launch {
                    journalEntryRepository.insert(journalViewModel.getJournalEntry())
                }

                findNavController().navigate(R.id.action_addJournalEntryFragment_to_viewJournalEntriesFragment)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Some required fields are empty!")
                    .setMessage("Make sure that you give each coffee at least a rating and write some notes...")
                    .setPositiveButton("Will do!", null)
                    .show()
            }
        }

        return view
    }

    private val cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            journalViewModel.setPhotoPath(currentPhotoPath)
            Glide.with(this.requireView()).load(currentPhotoPath).into(ivPhoto)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile : File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.also {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "mk.ukim.finki.coffeejournal.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }

        try {
            cameraActivityLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // ERROR
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile() : File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
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
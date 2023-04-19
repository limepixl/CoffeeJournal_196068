package mk.ukim.finki.coffeejournal.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.ukim.finki.coffeejournal.R
import java.util.*


class BackupFragment : Fragment() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var ivProfilePicture: ImageView
    private lateinit var btnAuth: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data

                    val mAccount = GoogleSignIn.getLastSignedInAccount(requireActivity())

                    val credential = GoogleAccountCredential.usingOAuth2(
                        requireContext(), Collections.singleton(Scopes.DRIVE_FILE)
                    )
                    if (mAccount != null) {
                        credential.selectedAccount = mAccount.account
                        ImageViewCompat.setImageTintList(ivProfilePicture, null);
                        Glide.with(requireView()).load(mAccount.photoUrl).into(ivProfilePicture)
                        btnAuth.text = mAccount.displayName
                    }

                    val googleDriveService = Drive.Builder(
                        NetHttpTransport(), GsonFactory(), credential
                    ).setApplicationName("CoffeeJournal").build()

                    val dbPath = "/data/data/mk.ukim.finki.coffeejournal/databases/journal_database"
                    val dbPathShm =
                        "/data/data/mk.ukim.finki.coffeejournal/databases/journal_database-shm"
                    val dbPathWal =
                        "/data/data/mk.ukim.finki.coffeejournal/databases/journal_database-wal"

                    val storageFile = File()
                    storageFile.name = "journal_database"

                    val storageFileShm = File()
                    storageFileShm.name = "journal_database-shm"

                    val storageFileWal = File()
                    storageFileWal.name = "journal_database-wal"

                    val filePath = java.io.File(dbPath)
                    val filePathShm = java.io.File(dbPathShm)
                    val filePathWal = java.io.File(dbPathWal)
                    val mediaContent = FileContent("", filePath)
                    val mediaContentShm = FileContent("", filePathShm)
                    val mediaContentWal = FileContent("", filePathWal)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val file =
                            googleDriveService.files().create(storageFile, mediaContent).execute()
                        System.out.printf(
                            "Filename: %s File ID: %s \n", file.name, file.id
                        )

                        val fileShm = googleDriveService.files().create(
                            storageFileShm, mediaContentShm
                        ).execute()
                        System.out.printf(
                            "Filename: %s File ID: %s \n", fileShm.name, fileShm.id
                        )

                        val fileWal = googleDriveService.files().create(
                            storageFileWal, mediaContentWal
                        ).execute()
                        System.out.printf(
                            "Filename: %s File ID: %s \n", fileWal.name, fileWal.id
                        )
                    }
                }
            }

//                lifecycleScope.launch(Dispatchers.IO) {
//                    request.execute()
//                }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_backup, container, false)

        btnAuth = view.findViewById(R.id.btn_auth)
        btnAuth.setOnClickListener {
            oauth2()
        }

        ivProfilePicture = view.findViewById(R.id.iv_profile_pic)

        // Inflate the layout for this fragment
        return view
    }

    private fun oauth2() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(
            Scope(Scopes.DRIVE_FILE), Scope(Scopes.DRIVE_APPFOLDER)
        ).requestEmail().build()
        val googleSingInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSingInClient.signInIntent

        resultLauncher.launch(signInIntent)
    }
}
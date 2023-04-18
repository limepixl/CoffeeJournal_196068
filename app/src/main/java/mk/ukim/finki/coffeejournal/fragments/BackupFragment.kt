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
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import mk.ukim.finki.coffeejournal.R
import java.util.*

class BackupFragment : Fragment() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var ivProfilePicture: ImageView
    private lateinit var btnAuth: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
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
            }
        }
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
package `is`.hbv601g.movieapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import `is`.hbv601g.movieapp.database.AppDatabaseHelper
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigationButtons()
        setupThemeToggleButton()

        lifecycleScope.launch {
            setupAverageRating()
        }

        // Setup profile picture
        profileImageView = findViewById(R.id.profileImageView)
        loadProfilePicture()

        // Launcher for gallery selection using OpenDocument.
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                // Use a coroutine to copy the file (this is IO-bound work).
                lifecycleScope.launch {
                    val localUri = copyUriToFile(it)
                    if (localUri != null) {
                        updateProfilePicture(localUri)
                    } else {
                        Toast.makeText(this@HomeActivity, "Failed to copy image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Launcher for taking a new photo.
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && photoUri != null) {
                // photoUri is already a local file URI.
                updateProfilePicture(photoUri!!)
            } else {
                Toast.makeText(this, "Failed to take photo", Toast.LENGTH_SHORT).show()
            }
        }

        // When profile image is clicked, show dialog to choose image source.
        profileImageView.setOnClickListener {
            showImagePickerDialog()
        }
    }

    /**
     * Shows a dialog with options to pick an image from the gallery or take a new photo.
     */
    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Take Photo")
        AlertDialog.Builder(this)
            .setTitle("Select Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch(arrayOf("image/*"))
                    1 -> {
                        photoUri = createImageUri()
                        takePhotoLauncher.launch(photoUri)
                    }
                }
            }
            .show()
    }

    /**
     * Creates a temporary URI for saving a new photo.
     */
    private fun createImageUri(): Uri {
        val imageFile = File.createTempFile("profile_", ".jpg", getExternalFilesDir(null))
        return FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", imageFile)
    }

    /**
     * Copies the file from the given [uri] to the app's storage and returns a local URI.
     */
    private suspend fun copyUriToFile(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            // Create a directory for profile images.
            val profileDir = File(getExternalFilesDir(null), "profile_images")
            if (!profileDir.exists()) {
                profileDir.mkdirs()
            }
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(profileDir, fileName)

            // Open an input stream from the source URI and copy its contents.
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Return a content URI for the newly copied file.
            FileProvider.getUriForFile(this@HomeActivity, "${applicationContext.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Updates the profile picture ImageView and stores the local URI in the database.
     */
    private fun updateProfilePicture(uri: Uri) {
        profileImageView.setImageURI(uri)
        val dbHelper = AppDatabaseHelper(this)
        dbHelper.insertProfilePicture(uri.toString())
        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads the profile picture from the database, if available.
     */
    private fun loadProfilePicture() {
        val dbHelper = AppDatabaseHelper(this)
        val uriString = dbHelper.getProfilePicture()
        uriString?.let {
            try {
                profileImageView.setImageURI(Uri.parse(it))
            } catch (e: Exception) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigationButtons() {
        val searchButton = findViewById<Button>(R.id.btnSearch)
        val moviesButton = findViewById<Button>(R.id.btnMovies)
        val tvShowsButton = findViewById<Button>(R.id.btnTvShows)
        val reviewButton = findViewById<Button>(R.id.btnReview)
        val favoritesButton = findViewById<Button>(R.id.btnFavorites)

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        moviesButton.setOnClickListener {
            startActivity(Intent(this, MovieActivity::class.java))
        }
        tvShowsButton.setOnClickListener {
            startActivity(Intent(this, TvShowActivity::class.java))
        }
        reviewButton.setOnClickListener {
            startActivity(Intent(this, MyRatingsActivity::class.java))
        }
        favoritesButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    private fun setupThemeToggleButton() {
        val toggleThemeButton = findViewById<Button>(R.id.btnToggleTheme)
        toggleThemeButton.setOnClickListener {
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    private suspend fun setupAverageRating() {
        val averageRatingTextView = findViewById<TextView>(R.id.textViewAverageRating)
        val rating = getAverageRating(applicationContext)
        val averageRatingText = "My Average Rating Across All Movies: $rating"
        averageRatingTextView.text = averageRatingText
    }

    private suspend fun getAverageRating(context: Context): Double {
        val token = AppDatabaseHelper(context).getLatestToken()
        var rating = 0.0
        token?.let {
            val fetchedRating = RetrofitInstance.userApiService.getAverageRatingOfMe(token).body()
            fetchedRating?.let { rating = fetchedRating }
        }
        return rating
    }
}

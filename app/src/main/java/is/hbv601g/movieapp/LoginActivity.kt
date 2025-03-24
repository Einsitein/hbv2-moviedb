package `is`.hbv601g.movieapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601g.movieapp.database.AppDatabaseHelper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * LoginActivity handles user login and saves the JWT token using the AppDatabaseHelper.
 */
class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the XML layout for login.
        setContentView(R.layout.activity_login)

        // Get references to views.
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonSend)
        val switchButton = findViewById<Button>(R.id.buttonSwitch)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            sendLoginRequest(email, password, applicationContext)
        }

        switchButton.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Sends a login request and saves the JWT token using AppDatabaseHelper.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param context The context.
     */
    private fun sendLoginRequest(email: String, password: String, context: Context) {

        val jsonBody = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://hugbunadarverkefni1-moviedb.onrender.com/user/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()

                if (response.isSuccessful && responseString != null) {
                    Log.d("Login", "Login successful: $responseString")
                    if (responseString.startsWith("Invalid")) {
                        // Invalid credentials – notify the user.
                        runOnUiThread {
                            Toast.makeText(context, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                        }
                        return
                    } else {
                        // Save token using AppDatabaseHelper.
                        val dbHelper = AppDatabaseHelper(context)
                        dbHelper.insertToken(responseString)
                        val tokenFromDb = dbHelper.getLatestToken()
                        runOnUiThread {
                            Toast.makeText(context, "Token: $tokenFromDb", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e("Login", "Login failed with code: ${response.code}")
                }
                runOnUiThread {
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}

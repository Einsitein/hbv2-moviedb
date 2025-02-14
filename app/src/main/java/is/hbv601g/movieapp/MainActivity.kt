package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the XML layout
        setContentView(R.layout.activity_main)

        // Get references to views
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.button)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            sendLoginRequest(email, password)
        }
    }

    private fun sendLoginRequest(email: String, password: String) {
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("https://example.com/login") // do not want to commit with my ipv4
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("Login", "Login successful: ${response.body?.string()}")
                } else {
                    Log.e("Login", "Login failed with code: ${response.code}")
                }
                runOnUiThread {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}

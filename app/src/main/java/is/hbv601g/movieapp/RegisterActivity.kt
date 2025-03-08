package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the XML layout
        setContentView(R.layout.activity_register)

        // Get references to views
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val registerButton = findViewById<Button>(R.id.buttonSend)
        val switchButton = findViewById<Button>(R.id.buttonSwitch)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            sendRegisterRequest(email, password)
        }

        switchButton.setOnClickListener {
            runOnUiThread {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun sendRegisterRequest(email: String, password: String) {

        val jsonBody = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://hugbunadarverkefni1-moviedb.onrender.com/user/register")
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Register", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("Register", "Register successful: ${response.body?.string()}")
                } else {
                    Log.e("Register", "Register failed with code: ${response.code}")
                }
                runOnUiThread {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}

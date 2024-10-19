package com.example.appsample

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appsample.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val urlLogin = "http://192.168.1.5/databasesample/login.php" // Ensure this is the correct URL
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString().trim()
            val loginPassword = binding.loginPassword.text.toString().trim()

            // Check if fields are not empty
            if (loginUsername.isEmpty() || loginPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginDatabase(loginUsername, loginPassword)
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun handleResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val success = jsonObject.getString("success")

            if (success == "1") {
                // Check if "login" is an object and not an array
                if (jsonObject.has("login") && jsonObject.getJSONArray("login").length() > 0) {
                    val user = jsonObject.getJSONArray("login").getJSONObject(0) // Get first user object
                    val userId = user.getString("userid")
                    val userName = user.getString("username").trim()

                    Toast.makeText(this, "User Id: $userId, Name: $userName", Toast.LENGTH_LONG).show()

                    // Navigate to the main activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login data not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Login Failed: ${jsonObject.getString("message")}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginDatabase(username: String, password: String) {
        val progressDialog = Dialog(this).apply {
            setContentView(R.layout.dialog_progress) // Reference to your dialog layout
            setCancelable(false)
        }
        progressDialog.show()

        val stringRequest = object : StringRequest(Request.Method.POST, urlLogin,
            Response.Listener { response ->
                progressDialog.dismiss() // Dismiss the dialog
                Log.d("LoginActivity", "Response: $response") // Log the response for debugging
                handleResponse(response)
            },
            Response.ErrorListener { error: VolleyError ->
                progressDialog.dismiss() // Dismiss the dialog
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Network Error: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username, "password" to password)
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed() // Call the super method
    }
}

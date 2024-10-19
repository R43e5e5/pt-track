package com.example.appsample

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appsample.databinding.ActivitySignupBinding
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    // Update the URL to point to your PHP script on the XAMPP server
    private val urlSignup = "http://192.168.1.5/databasesample/register.php" // Use 10.0.2.2 for emulator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString().trim()
            val signupPassword = binding.signupPassword.text.toString().trim()

            // Validate input
            if (signupUsername.isEmpty() || signupPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signupDatabase(signupUsername, signupPassword)
        }

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupDatabase(username: String, password: String) {
        val progressDialog = Dialog(this).apply {
            setContentView(R.layout.dialog_progress) // Create a simple loading layout
            setCancelable(false)
        }
        progressDialog.show()

        val stringRequest = object : StringRequest(Request.Method.POST, urlSignup,
            Response.Listener { response ->
                progressDialog.dismiss()
                val jsonObject = JSONObject(response)
                val success = jsonObject.getString("success")

                if (success == "1") {
                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Signup Failed: ${jsonObject.getString("message")}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username, "password" to password)
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
}

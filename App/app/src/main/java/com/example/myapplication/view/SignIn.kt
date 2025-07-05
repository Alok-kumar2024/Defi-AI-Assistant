package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.os.PatternMatcher
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignInBinding
import java.util.regex.Pattern

class SignIn : AppCompatActivity() {

    private lateinit var binding : ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.TvSignUpSignIn.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
            finish()
        }

        binding.BtnSignIn.setOnClickListener {
            val email = binding.TETemailSignIn.text.toString()
            val password = binding.TETpasswordSignIn.text.toString()

            if (!emailChecker(email))
            {
                Toast.makeText(this,"Not a Valid Email type",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty() || email.isBlank())
            {
                Toast.makeText(this,"Email Field is Empty.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.isBlank())
            {
                Toast.makeText(this,"Password Field is Empty.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



        }

    }

    fun emailChecker(email : String) : Boolean
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}
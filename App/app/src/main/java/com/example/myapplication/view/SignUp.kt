package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieDrawable
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.model.signUpDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val SignOutTime = 15000L
    private val timeOutHandler = Handler(Looper.getMainLooper())
    private var timeOutRunnable: Runnable? = null
    private var signUpProgress = false

    private var user : Map<*,*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharetheme = getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.TvSignInSignUp.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }


        binding.BtnSignIn.setOnClickListener {

            if (signUpProgress) {
                Toast.makeText(
                    this,
                    "Already in Progress , Please Wait..",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            signUpProgress = true

            val fullName = binding.TETNameSignUp.text
            val email = binding.TETemailSignUp.text
            val password = binding.TETpasswordSignUp.text
            val passwordConfirm = binding.TETConfirmpasswordSignUp.text

            binding.LottieAnimationSignUp.visibility = View.VISIBLE
            binding.LottieAnimationSignUp.repeatCount = LottieDrawable.INFINITE
            binding.LottieAnimationSignUp.playAnimation()



            timeOutRunnable = Runnable {
                if (signUpProgress) {
                    signUpResetState()
                    Toast.makeText(
                        this,
                        "Sign-Up timed out. Please try again Later.. ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            timeOutHandler.postDelayed(timeOutRunnable!!, SignOutTime)


            if (fullName.isNullOrEmpty()) {
                Toast.makeText(this, "Name Field is Empty", Toast.LENGTH_SHORT).show()
                signUpResetState("No FullName")
                return@setOnClickListener
            }
            if (email.isNullOrEmpty()) {
                Toast.makeText(this, "Email Field is Empty", Toast.LENGTH_SHORT).show()
                signUpResetState("No Email")
                return@setOnClickListener
            }

            if (password.isNullOrEmpty()) {
                Toast.makeText(this, "Password Field is Empty", Toast.LENGTH_SHORT).show()
                signUpResetState("No Password")
                return@setOnClickListener
            }

            if (passwordConfirm.isNullOrEmpty()) {
                Toast.makeText(this, "Confirm Password Field is Empty", Toast.LENGTH_SHORT).show()
                signUpResetState("No Confirm Password")
                return@setOnClickListener
            }

            if (emailChecker(email.toString())) {
                if (password.length < 6) {
                    Toast.makeText(
                        this,
                        "Password Must be Longer than 6 Letters.",
                        Toast.LENGTH_SHORT
                    ).show()
                    signUpResetState("Short Password")
                    return@setOnClickListener
                }

                if (password.toString() == passwordConfirm.toString()) {
                    auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                val userData = signUpDataClass(
                                    name = fullName.toString(),
                                    email = email.toString(),
                                    signUpMethod = "CUSTOM"
                                )
                                val uniqueKey = email.toString().substringBefore("@").lowercase()

                                database.child("USERS").child(uniqueKey).setValue(userData)
                                    .addOnCompleteListener { task->

                                        if (task.isSuccessful)
                                        {
                                            val share = getSharedPreferences("SIGNUP", MODE_PRIVATE)
                                            val editor = share.edit()

                                            editor.putString("EMAIL",email.toString())
                                            editor.putString("UNIQUEKEY",uniqueKey)
                                            editor.putBoolean("SignIn",true)
                                            editor.apply()

                                            FirebaseDatabase.getInstance().getReference("USERS").child(uniqueKey)
                                                .child("wallets").orderByChild("choosen").equalTo(true)
                                                .addListenerForSingleValueEvent( object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if (snapshot.exists())
                                                        {
                                                            Log.d("signin","Inside snapshot exists")
                                                            for(userSnapshot in snapshot.children)
                                                            {
                                                                user = userSnapshot.value as? Map<*, *>
                                                                Log.d("signin","Inside snapshot exists")
                                                                Log.d("signIn","The User Value in For Loop is $user")
                                                            }

                                                            if (user != null)
                                                            {
                                                                Log.d("signin","Inside user != nul if")
                                                                val address = user?.get("address") ?: "Not Got"
                                                                val choosen = user?.get("choosen") ?: "Not Got"
                                                                editor.apply()
                                                                Log.d("SignIn","The Address of the choosen Wallet : $address\n The Choosen Value : $choosen")

                                                                editor.putString("address", address.toString()).apply()
                                                                editor.putBoolean("choosen", choosen as Boolean).apply()

                                                                val intent = Intent(this@SignUp,MainActivity::class.java)
                                                                startActivity(intent)
                                                                finish()

                                                            }else{
                                                                Toast.makeText(this@SignUp,"Wallet Found , but data loading here is Null",Toast.LENGTH_SHORT).show()
                                                            }

                                                        }else{
                                                            Log.d("signin","Inside snapshot do not exists")
                                                            val intent = Intent(this@SignUp,MainActivity::class.java)
                                                            startActivity(intent)
                                                            finish()

                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        Log.e("FirebaseError", "Error: ${error.message}")
                                                    }

                                                })

//                                            val intent = Intent(this,MainActivity::class.java)
//                                            startActivity(intent)
//                                            finish()
                                            signUpResetState("Completed SignUp")
                                        }else{
                                            Toast.makeText(this,"Error : Couldn't Sync To the Database.",Toast.LENGTH_SHORT).show()
                                            signUpResetState("Sync Error Database")
                                        }

                                    }

                            }else
                            {
                                val exception = task.exception

                                when(exception)
                                {
                                    is FirebaseAuthUserCollisionException ->{
                                        Toast.makeText(this,"Email Already Exists",Toast.LENGTH_SHORT).show()
                                    }
                                    is FirebaseAuthWeakPasswordException -> {
                                        Toast.makeText(this,"Weak Password",Toast.LENGTH_SHORT).show()
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Toast.makeText(this, "Signup failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }

                                }
                                signUpResetState("Due To firebase Exceptions")
                            }
                        }


                } else {
                    Toast.makeText(this, "Password Not Matched.", Toast.LENGTH_SHORT).show()
                    signUpResetState("Password Not Matched")
                    return@setOnClickListener
                }


            } else {
                Toast.makeText(this, "Email Format is Incorrect", Toast.LENGTH_SHORT).show()
                signUpResetState("Email Format Incorrect")
                return@setOnClickListener
            }

        }
    }

    private fun signUpResetState(reset: String = "Unknown") {
        signUpProgress = false

        timeOutRunnable?.let { timeOutHandler.removeCallbacks(it) }

        binding.LottieAnimationSignUp.cancelAnimation()
        binding.LottieAnimationSignUp.visibility = View.GONE

        Log.d("SignUpDebug", "Resettings due to : $reset")

    }

    fun emailChecker(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
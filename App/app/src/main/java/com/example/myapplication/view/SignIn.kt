package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PatternMatcher
import android.provider.ContactsContract.CommonDataKinds.Email
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
import com.example.myapplication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import java.util.regex.Pattern

class SignIn : AppCompatActivity() {

    private lateinit var binding : ActivitySignInBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private var user : Map<*,*>? = null

    private val signInTime = 15000L
    private val timeOutHandler = Handler(Looper.getMainLooper())
    private var timeOutRunnable : Runnable? = null
    private var signInProgress = false

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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("USERS")

        binding.TvSignUpSignIn.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
            finish()
        }

        binding.BtnSignIn.setOnClickListener {

            if (signInProgress) {
                Toast.makeText(
                    this,
                    "Already in Progress , Please Wait..",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            signInProgress = true

            binding.LottieAnimationSignIn.visibility = View.VISIBLE
            binding.LottieAnimationSignIn.repeatCount = LottieDrawable.INFINITE
            binding.LottieAnimationSignIn.playAnimation()


            val email = binding.TETemailSignIn.text.toString()
            val password = binding.TETpasswordSignIn.text.toString()

            timeOutRunnable = Runnable {
                if (signInProgress)
                {
                    signInResetState()
                    Toast.makeText(this,"Sign In Time-Out, Please Try Again Later",Toast.LENGTH_SHORT).show()
                }
            }
            timeOutHandler.postDelayed(timeOutRunnable!!,signInTime)


            if (email.isEmpty() || email.isBlank())
            {
                Toast.makeText(this,"Email Field is Empty.",Toast.LENGTH_SHORT).show()
                signInResetState("Empty Email")
                return@setOnClickListener
            }
            if (password.isEmpty() || password.isBlank())
            {
                Toast.makeText(this,"Password Field is Empty.",Toast.LENGTH_SHORT).show()
                signInResetState("Empty Password")
                return@setOnClickListener
            }

            if (emailChecker(email.toString()))
            {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->

                    if (task.isSuccessful)
                    {
                        val uniqueKey = email.toString().substringBefore("@").lowercase()

                        val share = getSharedPreferences("SIGNUP", MODE_PRIVATE)
                        val editor = share.edit()

                        editor.putString("EMAIL",email.toString()).apply()
                        editor.putString("UNIQUEKEY",uniqueKey).apply()

                        FirebaseDatabase.getInstance().getReference("USERS").child(uniqueKey)
                            .child("wallets").orderByChild("choosen").equalTo(true)
                            .addListenerForSingleValueEvent( object : ValueEventListener{
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
                                            Log.d("SignIn","The Address of the choosen Wallet : $address\n The Choosen Value : $choosen")

                                            editor.putString("address", address.toString()).apply()
                                            editor.putBoolean("choosen", choosen as Boolean).apply()

                                            val intent = Intent(this@SignIn,MainActivity::class.java)
                                            startActivity(intent)
                                            finish()

                                        }else{
                                            Toast.makeText(this@SignIn,"Wallet Found , but data loading here is Null",Toast.LENGTH_SHORT).show()
                                        }

                                    }else{
                                        Log.d("signin","Inside snapshot do not exists")
                                        val intent = Intent(this@SignIn,MainActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("FirebaseError", "Error: ${error.message}")
                                }

                            })

//                        val intent = Intent(this,MainActivity::class.java)
//                        startActivity(intent)
//                        finish()

                        signInResetState("Completed SignIn")


                    }else
                    {
                        val exception = task.exception


                        when(exception)
                        {
                            is FirebaseAuthInvalidUserException ->{
                                Toast.makeText(this,"Email not Registered",Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException ->{
                                Toast.makeText(this,"Invalid Password or Email Not Registered",Toast.LENGTH_SHORT).show()
                            }
                        }

                        signInResetState("due To FirebaseAuth Exceptions")
                    }

                }
            }else
            {
                signInResetState("Invalid Email")
                Toast.makeText(this,"Incorrect Email Format",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }

    }

    private fun signInResetState(reset : String = "Unknown")
    {
        signInProgress = false

        timeOutRunnable?.let { timeOutHandler.removeCallbacks(it) }

        binding.LottieAnimationSignIn.cancelAnimation()
        binding.LottieAnimationSignIn.visibility = View.GONE

        Log.d("SignIn Debug","The Reset is Due to : $reset")

    }

    fun emailChecker(email : String) : Boolean
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}
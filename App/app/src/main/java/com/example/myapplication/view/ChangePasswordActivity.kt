package com.example.myapplication.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieDrawable
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangePasswordBinding

    private lateinit var firebaseAuth : FirebaseAuth

    private var uniqueID = ""

    private var isChangePasswordClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val share = getSharedPreferences("SIGNUP", MODE_PRIVATE)
        uniqueID = share.getString("UNIQUEKEY",null) ?: "Not Got"
        Log.d("Settings","The unique key is $uniqueID")

        firebaseAuth = FirebaseAuth.getInstance()

        binding.IbBackButtonOfChangePassword.setOnClickListener {
            finish()
        }

        binding.BtnChangePasswordPassword.setOnClickListener {
            binding.LoadingLottiAnimationChangePassword.repeatCount = LottieDrawable.INFINITE
            binding.LoadingLottiAnimationChangePassword.visibility = View.VISIBLE
            binding.LoadingLottiAnimationChangePassword.playAnimation()

            if (isChangePasswordClicked){
                Toast.makeText(this,"Currently In Progress, Please Wait Before Clicking Again",
                    Toast.LENGTH_SHORT).show()
                binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                return@setOnClickListener
            }

            val currentPassword = binding.EtCurrentPasswordPassword.text.trim()
            val newPassword = binding.EtNewPasswordPassword.text.trim()

            val user = firebaseAuth.currentUser

            if (user == null) {
                Toast.makeText(this, "User Not Found.", Toast.LENGTH_SHORT).show()
                isChangePasswordClicked = false
                binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                return@setOnClickListener
            }

            if (currentPassword.isNullOrEmpty()) {
//                binding.TIEcurrentPasswordChangePassword.error = "This is an Required Field."
                Toast.makeText(this, "Current Password Cannot be Empty.", Toast.LENGTH_SHORT).show()
                isChangePasswordClicked = false
                binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                return@setOnClickListener
            }
            if (newPassword.isNullOrEmpty()) {
                Toast.makeText(this, "New Password Cannot be Empty.", Toast.LENGTH_SHORT).show()
                isChangePasswordClicked = false
                binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(user.email!!,currentPassword.toString())

            user.reauthenticate(credential).addOnCompleteListener { auth->

                if(auth.isSuccessful){
                    user.updatePassword(newPassword.toString()).addOnCompleteListener {task->
                        if (task.isSuccessful){
                            Toast.makeText(
                                this,
                                "SuccessFully Changed Password.",
                                Toast.LENGTH_SHORT
                            ).show()
                            isChangePasswordClicked = false
                            binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                            binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                            finish()
                        }else{
                            Toast.makeText(
                                this,
                                "Try a bit longer Password.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                            binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                            isChangePasswordClicked = false
                        }
                    }
                }else{
                    Toast.makeText(this, "Incorrect Current Password.", Toast.LENGTH_SHORT)
                        .show()
                    isChangePasswordClicked = false
                    binding.LoadingLottiAnimationChangePassword.cancelAnimation()
                    binding.LoadingLottiAnimationChangePassword.visibility = View.GONE
                }

            }

            isChangePasswordClicked = true

        }

    }
}
package com.example.myapplication.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.model.ThemeHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isDarkThemeDialogShowing = false
    private var isLogOutDialogShowing = false

    private lateinit var database : DatabaseReference
    private lateinit var firebase : FirebaseAuth

    private var uniqueID = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)

        val share = requireContext().getSharedPreferences("SIGNUP", MODE_PRIVATE)
        uniqueID = share.getString("UNIQUEKEY",null) ?: "Not Got"
        Log.d("Settings","The unique key is $uniqueID")

        database = FirebaseDatabase.getInstance().getReference("USERS")
        firebase = FirebaseAuth.getInstance()

        binding.LLThemeSettings.setOnClickListener {
            showAlertDialogTheme()
        }
        binding.IVThemeSettings.setOnClickListener{
            showAlertDialogTheme()
        }

        binding.LLLogOutSettings.setOnClickListener {
            logOutAlertBox()
        }
        binding.IVLogOutSettings.setOnClickListener {
            logOutAlertBox()
        }

        binding.LLPasswordSettings.setOnClickListener {
            val intent = Intent(requireContext(),ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.IVChangePasswordSettings.setOnClickListener {
            val intent = Intent(requireContext(),ChangePasswordActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun showAlertDialogTheme() {

        if (isDarkThemeDialogShowing) return

        val view = layoutInflater.inflate(R.layout.theme_alert_dialog_box, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.RGOptionsTheme)
        val btnCancel = view.findViewById<Button>(R.id.BtnCancelTheme)
        val btnOk = view.findViewById<Button>(R.id.BtnOkTheme)

        val sharedPref = requireContext().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme = sharedPref.getString("themeOption", ThemeHelper.SYSTEM)

        when (savedTheme) {
            ThemeHelper.LIGHT -> radioGroup.check(R.id.light)
            ThemeHelper.DARK -> radioGroup.check(R.id.dark)
            ThemeHelper.SYSTEM -> radioGroup.check(R.id.system)
        }

        val builder = AlertDialog.Builder(requireContext()).setView(view).create()


        btnCancel.setOnClickListener {
            builder.dismiss()
        }

        btnOk.setOnClickListener {
            val radioID = radioGroup.checkedRadioButtonId
            if (radioID == -1) {
                Toast.makeText(requireContext(), "Select An Option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedBtn = view.findViewById<RadioButton>(radioID)
            val theme = selectedBtn.text.toString().lowercase().trim()
            actionApplyTheme(theme)
            builder.dismiss()
        }

        builder.setOnDismissListener {
            isDarkThemeDialogShowing = false
        }

        isDarkThemeDialogShowing = true

        builder.window?.setBackgroundDrawable(
            ContextCompat.getColor(
            requireContext(),
            R.color.transparent
        ).toDrawable())

        builder.show()

    }

    private fun actionApplyTheme(option: String) {
        val share = requireContext().getSharedPreferences("theme", MODE_PRIVATE)

        Log.d("Theme", "Option Choosen $option")

        when (option) {
            "light" -> {
                share.edit().putString("themeOption", ThemeHelper.LIGHT).apply()
                ThemeHelper.applyTheme(ThemeHelper.LIGHT)
                requireActivity().recreate()
            }

            "dark" -> {
                share.edit().putString("themeOption", ThemeHelper.DARK).apply()
                ThemeHelper.applyTheme(ThemeHelper.DARK)
                requireActivity().recreate()
            }

            "system" -> {
                share.edit().putString("themeOption", ThemeHelper.SYSTEM).apply()
                ThemeHelper.applyTheme(ThemeHelper.SYSTEM)
                requireActivity().recreate()
            }

            else -> {
                share.edit().putString("themeOption", ThemeHelper.SYSTEM).apply()
                ThemeHelper.applyTheme(ThemeHelper.SYSTEM)
                requireActivity().recreate()
            }
        }
    }

    private fun logOutAlertBox() {

        if (isLogOutDialogShowing) return

        val dialogBox = layoutInflater.inflate(R.layout.alertbox_logout, null)

        val name = dialogBox.findViewById<TextView>(R.id.TvUserNameLogout)
        val logoutBtn = dialogBox.findViewById<Button>(R.id.BtnLogOutAlterBox)
        val cancelBtn = dialogBox.findViewById<Button>(R.id.BtnCancelLogOutAlertBox)

        val builder = AlertDialog.Builder(requireContext()).setView(dialogBox).setCancelable(false).create()

        database.child(uniqueID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = (snapshot.child("name").value ?: "Not Found").toString()


                    Log.d(
                        "LogoutInfo", "The name is $username")

                    name.text = username
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LogOut", "Cant Fetch from Database : error -> ${error.message}")
            }

        })

        logoutBtn.setOnClickListener {

            val credentialManager = CredentialManager.create(requireContext())

            lifecycleScope.launch {
                try {
                    val clearStateRequest = ClearCredentialStateRequest()
                    credentialManager.clearCredentialState(clearStateRequest)
                    Log.d("Logout", "CredentialManager cleared")
                } catch (e: Exception) {
                    Log.e("Logout", "Error clearing CredentialManager", e)
                }
            }

            firebase.signOut()
            Toast.makeText(requireContext(), "SuccessFully Logged Out.", Toast.LENGTH_SHORT).show()

            val share = requireContext().getSharedPreferences(
                "SIGNUP", Context.MODE_PRIVATE
            )
//            FirebaseFirestore.getInstance().collection("USERS").document(currentID)
//                .update("fcmToken", FieldValue.delete()).addOnSuccessListener {
//                    Log.d("Logout", "Token deleted from FireStore")
//                }

            val editor = share.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), SignIn::class.java)
            startActivity(intent)
            builder.dismiss()

            requireActivity().finishAffinity()

        }

        cancelBtn.setOnClickListener {
            builder.dismiss()
        }

        builder.setOnDismissListener {
            isLogOutDialogShowing = false
        }

        isLogOutDialogShowing = true

        builder.window?.setBackgroundDrawable(ContextCompat.getColor(requireContext(),R.color.transparent).toDrawable())

        builder.show()

    }

}
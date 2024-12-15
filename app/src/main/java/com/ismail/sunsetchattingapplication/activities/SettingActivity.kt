package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)


        binding.apply {


            goProfile.setOnClickListener(){
                var intent = Intent(this@SettingActivity, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        
            arrowB.setOnClickListener() {
                var intent = Intent(this@SettingActivity, UsersActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

            lagout.setOnClickListener() {
                showLogoutConfirmationDialog()
            }

            readCurrentUserInfo()
        }
    }


    private fun readCurrentUserInfo() {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("userName").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val imageUrl = dataSnapshot.child("ProfileUrl").getValue(String::class.java)

                    binding.cUserName.text = username
                    binding.cUserEmail.text = email

                    // Attempt to load the image using Glide or Picasso
                    try {
                        Glide.with(applicationContext)
                            .load(imageUrl)
                            .placeholder(R.drawable.select_profile_image) // Ensure you have a 'user_profile' drawable resource as a placeholder
                             // Optional: A fallback image in case of error
                            .into(binding.cUserProfile)
                    } catch (e: IllegalArgumentException) {
                        Log.e("SettingActivity", "Error loading image with Glide", e)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(
//                        this@SettingActivity,
//                        "laguma guulaysan soo aqrint xogta isticamalah: ${databaseError.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            })
        } else {
            Log.e("SettingActivity", "User ID is null")
        }
    }



    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Kabax")
            .setMessage("Ma hubta inaad kabaxidid ciwankan?")
            .setCancelable(false)
            .setPositiveButton("Haa") { dialog, which ->
                logoutUser()
            }
            .setNegativeButton("Maya") { dialog, which ->
                // User clicked "No", dismiss the dialog and do nothing
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutUser() {

        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this@SettingActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@SettingActivity, UsersActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    private fun applyTheme(darkMode: Boolean) {
        if (darkMode) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }

        // Recreate the activity to apply the theme change
        recreate()
    }
}
package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    val IMAGE_PICK_CODE = 0
    private var imageUri: Uri? = null
    private var isImageSelected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        binding.apply {
            userProfile1.setOnClickListener() {
                openGalleryForImage()
            }

            update.setOnClickListener() {
                val username = edUserName.text.toString()
                val bio = edBio.text.toString()
                if (imageUri == null) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "fadlan soo dooro sawir",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (username.isEmpty()) {
                    edUserName.setError("fadlan geli magac isticmalkga")
                    edUserName.requestFocus()
                } else if (bio.isEmpty()) {
                    edBio.setError("fadlan geli qoraalka ka heshid")
                    edBio.requestFocus()
                } else {
                    progressBar.visibility = View.VISIBLE
                    update.isClickable = false
                    edUserName.isClickable = false
                    edBio.isClickable = false
                    userProfile1.isClickable = false
                    updateProfile(imageUri!!)
                }
            }

            back.setOnClickListener() {
                var intent = Intent(this@ProfileActivity, SettingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }

        readCurrentUserInfo()

    }

    fun takeBio(bio: String) {
        val userBio = bio
    }

    private fun readCurrentUserInfo() {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!isFinishing && !isDestroyed) {
                        val username = dataSnapshot.child("userName").getValue(String::class.java)
                        val email = dataSnapshot.child("userBio").getValue(String::class.java)
                        val imageUrl = dataSnapshot.child("ProfileUrl").getValue(String::class.java)


                        val email1 = "badal qoraalka aad keheshid (${email})"
                        val userName = "badal magac isticmalakaga (${username})"


                        // Load the image using Glide or Picasso
                        Glide.with(this@ProfileActivity).load(imageUrl)
                            .placeholder(R.drawable.user_profiel)
                            .into(binding.userProfile1)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }

    fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // Handle the image URI
            imageUri = data?.data
            if (imageUri != null) {
                // User has selected an image, set it to the CircleImageView
                binding.userProfile1.setImageURI(imageUri)
                isImageSelected = true


            }
        }
    }


    fun updateProfile(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val username = binding.edUserName.text.toString().trim()
        val bio = binding.edBio.text.toString().trim()


        // Correctly initialize the reference to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/$userId.jpg")

        // Initialize the reference to Firebase Realtime Database correctly
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val userProfileUpdates = mapOf(
                    "userName" to username,
                    "userBio" to bio,
                    "ProfileUrl" to downloadUri.toString()
                )

                // Update Firebase Realtime Database
                databaseRef.updateChildren(userProfileUpdates).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this, "Si guula yaa loo badlay profile", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        binding.update.isClickable = true
                        var intent = Intent(this@ProfileActivity, SettingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.update.isClickable = true
                        Toast.makeText(
                            this,
                            "waa lagu guul daraystay badalka profileka.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.update.isClickable = true
                        binding.edUserName.isClickable = true
                        binding.edBio.isClickable = true
                        binding.userProfile1.isClickable = true
                    }
                }
            } else {
                binding.progressBar.visibility = View.GONE
                binding.update.isClickable = true
                // Handle failures
//                Toast.makeText(this, "Walagu guul darystay dir", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
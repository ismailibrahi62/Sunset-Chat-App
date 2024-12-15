package com.ismail.sunsetchattingapplication.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityAddProfileBinding
import java.util.UUID

class AddProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddProfileBinding
    val IMAGE_PICK_CODE = 0
    private var imageUri: Uri? = null
    private var isImageSelected: Boolean = false
    lateinit var auth: FirebaseAuth
    lateinit var databaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_profile)
        auth = Firebase.auth

        binding.apply {

            imgSelectedProfile.setOnClickListener() {
                openGalleryForImage()
            }

            btnSumbit.setOnClickListener() {
                val userName = edittextUserName.text.toString()

                if (!isImageSelected) {
                    Toast.makeText(
                        this@AddProfileActivity,
                        "fadlan soo dooro sawir",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (userName.isEmpty()) {
                    edittextUserName.setError("fadlan geli qoraal aad keheshid")
                    edittextUserName.requestFocus()
                } else {
                    progressBar.visibility = View.VISIBLE
                    btnSumbit.isClickable = false
                    imgSelectedProfile.isClickable = false
                    val userShortName = edittextUserName.text.toString()
                    var userEmail = intent.getStringExtra("email")
                    var userFullname = intent.getStringExtra("fullname")
                    val userId = auth.currentUser?.uid

                    uploadImageToFirebaseStorageAndSaveUserInfo(
                        imageUri!!, userId!!, userShortName,
                        userFullname!!, userEmail!!
                    )

                }
            }
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
                binding.imgSelectedProfile.setImageURI(imageUri)
                isImageSelected = true


            }
        }
    }

    fun uploadImageToFirebaseStorageAndSaveUserInfo(
        imageUri: Uri,
        userId: String,
        userName: String,
        userFullname: String,
        email: String
    ) {
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/profileImages/")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    saveUserInfoWithHashMap(userId, userName, email, userFullname, profileImageUrl)
                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads

            }
    }
    private fun clearProfileAddingFlag() {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("IsAddingProfile", false)
            apply()
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("IsAddingProfile", true)
            apply()
        }
    }

    fun saveUserInfoWithHashMap(
        userId: String,
        userBio: String,
        userName: String,
        email: String,
        profileImageUrl: String
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        val userHashMap: HashMap<String, Any> = HashMap()
        userHashMap["userId"] = userId
        userHashMap["userBio"] = userBio
        userHashMap["userName"] = email
        userHashMap["email"] = userName // Be cautious with storing passwords plainly
        userHashMap["ProfileUrl"] = profileImageUrl


        databaseRef.setValue(userHashMap)
            .addOnSuccessListener {



                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Kushodhow Somali Chat App", Toast.LENGTH_SHORT)
                        .show()
                    var intent = Intent(this@AddProfileActivity, UsersActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()



            }
            .addOnFailureListener {
                // Handle unsuccessful database operations
                Toast.makeText(this, "laguma guulaysan diwaan gelinta isticmalaha", Toast.LENGTH_SHORT).show()

                binding.progressBar.visibility = View.GONE
            }
    }






}
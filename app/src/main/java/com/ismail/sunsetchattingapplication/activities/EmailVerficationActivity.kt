package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityEmailVerficationBinding

class EmailVerficationActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerficationBinding
    var  auth = FirebaseAuth.getInstance()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil. setContentView(this, R.layout.activity_email_verfication)


        binding.apply {
            verify.setOnClickListener(){

                checkIfEmailVerified()
            }


            resend.setOnClickListener(){
                resend.isClickable = false
                verify.isClickable = false
                binding.progressBar.visibility = View.VISIBLE
                resendVerificationEmail()
            }


            var userEmail = intent.getStringExtra("email")

                email.setText(userEmail)


        }
    }




    private fun checkIfEmailVerified() {
        var userEmail = intent.getStringExtra("email")
        var userFullname = intent.getStringExtra("fullname")
        auth.currentUser?.reload()?.addOnSuccessListener {
            if (auth.currentUser?.isEmailVerified == true ) {
                var intent = Intent(this@EmailVerficationActivity, AddProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("email",userEmail)
                intent.putExtra("fullname",userFullname)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

            } else {
                Toast.makeText(this, "weli ma adan xaqiijin gmailkaga", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                binding.resend.isClickable = true
                binding.verify.isClickable = true
                Toast.makeText(this, "markale ayaan kusoodirnay ee booqo", Toast.LENGTH_LONG).show()
            }
            else{
                binding.resend.isClickable = true
                binding.verify.isClickable = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "${task.exception}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteUserFromFirebaseAuth() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DeleteUser", "User account deleted.")

            } else {
                Log.d("DeleteUser", "Failed to delete user account.")
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        deleteUserFromFirebaseAuth()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        deleteUserFromFirebaseAuth()
//    }

    override fun onDestroy() {
        super.onDestroy()
        deleteUserFromFirebaseAuth()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        deleteUserFromFirebaseAuth()
        var intent = Intent(this@EmailVerficationActivity, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }


}

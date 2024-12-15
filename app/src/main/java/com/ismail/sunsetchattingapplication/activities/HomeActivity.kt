package com.ismail.sunsetchattingapplication.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityHomeBinding
import java.util.Calendar

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

 
        binding.apply {

            val textGreeting = textviewGreeting.text.toString()
            updateGreetingMessage(textviewGreeting)

            letsGet.setOnClickListener() {
                var intent = Intent(this@HomeActivity, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (FirebaseAuth.getInstance().currentUser != null) {
            // User is logged in, redirect to the home screen
            val homeIntent = Intent(this, UsersActivity::class.java)
            startActivity(homeIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
//        else {
//            // User not logged in, redirect to the login/sign-up screen
//            val loginIntent = Intent(this, HomeActivity::class.java)
//            startActivity(loginIntent)
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            finish()
//        }
    }

    private fun updateGreetingMessage(greetingTextView: TextView) {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greetingText = when (timeOfDay) {
            in 0..11 -> "Sodhowow, Subax Wanagsan" // Morning (from 12am to 11:59am)
            in 12..16 -> "Sodhowow, Galab Wanagsan" // Afternoon (from 12pm to 4:59pm)
            else -> "Sodhowow, Habeen Wanagsan" // Evening (from 5pm to 11:59pm)
        }

        greetingTextView.text = greetingText
    }




}
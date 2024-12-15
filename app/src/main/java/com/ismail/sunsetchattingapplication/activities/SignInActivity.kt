package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivitySingInBinding

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySingInBinding
    private lateinit var auth: FirebaseAuth
    var passwordVisible = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sing_in)


        binding.apply {

            forgetPassword.setOnClickListener(){
                var intent = Intent(this@SignInActivity,ForgetPasswordActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
            edittextPassword.setOnTouchListener { v, event ->
                val DRAWABLE_RIGHT = 2

                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edittextPassword.right - edittextPassword.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        // Toggle between showing and hiding the password
                        passwordVisible = !passwordVisible
                        updatePasswordVisibility()
                        true
                    }
                }
                false
            }

          arrowBack.setOnClickListener(){
              var intent = Intent(this@SignInActivity, HomeActivity::class.java)
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              startActivity(intent)
              overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
              finish()
          }

            singUp.setOnClickListener() {
                var intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }


            buttonSignIn.setOnClickListener() {
                val userEmail = edittextEmail.text.toString()
                val userPassword = edittextPassword.text.toString()


                if (userEmail.isEmpty()) {
                    edittextEmail.setError("fadlan geli emailkaga")
                    edittextEmail.requestFocus()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    edittextEmail.setError("fadlan geli email sax ah")
                    edittextEmail.requestFocus()
                } else if (userPassword.isEmpty()) {
                    edittextPassword.setError("fadlan geli number sireedkaga")
                    edittextPassword.requestFocus()
                } else if (userPassword.length < 6) {
                    edittextPassword.setError("number sireedku kama yaraan karo 6 character")
                    edittextPassword.requestFocus()
                } else if (userPassword.length > 10) {
                    edittextPassword.setError("number sireedku kama waynaan karo 10 character")
                    edittextPassword.requestFocus()
                } else {
                    progressBar.visibility = View.VISIBLE
                    signInWithEmail(userEmail, userPassword)

                }
            }
        }
    }




    private fun signInWithEmail(userEmail:String,userPassword:String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    // Sign-in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Sodhoowow", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this@SignInActivity, UsersActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                } else {
                    binding.progressBar.visibility = View.GONE
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "Fadlan sax emailka ama number sireedkag", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.progressBar.visibility = View.VISIBLE
                       signInWithEmail(userEmail,userPassword)
                    }
                }
            }
    }




    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@SignInActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    private fun updatePasswordVisibility() {
        if (passwordVisible) {
            binding.edittextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.edittextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.showpwd, 0)
        } else {
            binding.edittextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.edittextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.hidepwd, 0)
        }
        binding.edittextPassword.setSelection(binding.edittextPassword.text.length)
    }

}
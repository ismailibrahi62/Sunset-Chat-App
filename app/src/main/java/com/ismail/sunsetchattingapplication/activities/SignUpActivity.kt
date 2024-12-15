package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    var passwordVisible = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)


        binding.apply {
            registerBtn.setOnClickListener() {
                var intent = Intent(this@SignUpActivity, EmailVerficationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            password.setOnTouchListener { v, event ->
                val DRAWABLE_RIGHT = 2

                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (password.right - password.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        // Toggle between showing and hiding the password
                        passwordVisible = !passwordVisible
                        updatePasswordVisibility()
                        true
                    }
                }
                false
            }
            confirmPassword.setOnTouchListener { v, event ->
                val DRAWABLE_RIGHT = 2

                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (password.right - confirmPassword.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        // Toggle between showing and hiding the password
                        passwordVisible = !passwordVisible
                        updateForConfirm()
                        true
                    }
                }
                false
            }


            arrowBack.setOnClickListener() {
                var intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            signIn.setOnClickListener() {

                var intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }


            registerBtn.setOnClickListener() {

                val userFullname = edittextFullname.text.toString()
                val userEmail = email.text.toString()
                val userPassword = password.text.toString()
                val userConfirmPassword = confirmPassword.text.toString()




                if (userFullname.isEmpty()) {
                    edittextFullname.setError("fadlan geli magac isticmalkaga")
                    edittextFullname.requestFocus()
                } else if (userEmail.isEmpty()) {
                    email.setError("fadlan geli emailkaga")
                    email.requestFocus()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    email.setError("fadlan geli email sax ah")
                    email.requestFocus()
                } else if (userPassword.isEmpty()) {
                    password.setError("fadlan geli number sireedkaga")
                    password.requestFocus()
                } else if (userPassword.length < 6) {
                    password.setError("number sireedku kama yaran karo lix xaraf ")
                    password.requestFocus()
                } else if (userPassword.length > 10) {
                    password.setError("number sireedku kama waynan karo 10 xaraf")
                    password.requestFocus()
                } else if (userConfirmPassword.isEmpty()) {
                    confirmPassword.setError("fadlan markale geli number sireedkaga")
                    confirmPassword.requestFocus()
                } else if (userConfirmPassword.length < 6) {
                    confirmPassword.setError("number sireedku kama yaran karo lix xaraf")
                    confirmPassword.requestFocus()
                } else if (userConfirmPassword.length > 10) {
                    confirmPassword.setError("number sireedku kama waynan karo 10 xaraf")
                    confirmPassword.requestFocus()
                } else if (userConfirmPassword != userPassword) {
                   confirmPassword.setText("")
                    password.setText("")
                    password.setHint("markale geli number sireedka")
                    confirmPassword.setHint("markale geli number sireedka")
                    password.requestFocus()
                    confirmPassword.requestFocus()
                    Snackbar.make(binding.root, "number sireedadu mahan kuwa isku mida ee markale geli", Snackbar.LENGTH_LONG)
                        .setAction("hagag"){}.show()
                }
                else {
                        progressBar.visibility = View.VISIBLE
                       binding.registerBtn.isClickable = false
                        createAccount(userEmail,userPassword,userFullname)
                }

            }
        }
    }

    fun createAccount(email: String, password: String,fullname:String) {

        var auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information


                    val user = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.progressBar.visibility = View.GONE
                                var intent = Intent(this@SignUpActivity, EmailVerficationActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("email",email)
                                intent.putExtra("fullname",fullname)
                                startActivity(intent)
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                                finish()
                            }

                        }

                    // Update UI or inform the user about the verification email
                } else {

                    binding.progressBar.visibility = View.GONE
                    binding.registerBtn.isClickable = true
                    // Update UI
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        binding.progressBar.visibility = View.GONE
                        binding.registerBtn.isClickable = true
                        // This error indicates the email is already in use for another account
                        Toast.makeText(this, "Emailkan waxa isku diwan galiyey qofkale", Toast.LENGTH_SHORT).show()
                    } else {
                        createAccount(email,password,fullname)
                    } 
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@SignUpActivity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }
    private fun updatePasswordVisibility() {
        if (passwordVisible) {
            binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.showpwd, 0)
        } else {
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.hidepwd, 0)
        }
        binding.password.setSelection(binding.password.text.length)
    }

    private fun updateForConfirm() {
        if (passwordVisible) {
            binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.confirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.showpwd, 0)
        } else {
            binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.confirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock2, 0, R.drawable.hidepwd, 0)
        }
        binding.confirmPassword.setSelection(binding.confirmPassword.text.length)
    }

}
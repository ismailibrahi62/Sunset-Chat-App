package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.ismail.sunsetchattingapplication.Adapters.UserAdapter
import com.ismail.sunsetchattingapplication.ModelClasses.User
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    lateinit var binding: ActivityUsersBinding
    val userlist = ArrayList<User>()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_users)
        binding.apply {

            supportActionBar?.setTitle("WadaSheekaysi")
            recyelerView.layoutManager = LinearLayoutManager(
                this@UsersActivity,
                LinearLayoutManager.VERTICAL, false
            )


            getUserList()


        }


    }

    fun getUserList() {
        var firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseRefrence: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        databaseRefrence.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()

                for (datasnapshot: DataSnapshot in snapshot.children) {
                    val user = datasnapshot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {
                        userlist.add(user)
                    }

                }
                val userAdapter = UserAdapter(userlist)

                binding.recyelerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@UsersActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.user_setting -> {
                var intent = Intent(this@UsersActivity, SettingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
//    fun updateUserLastSeen(userId: String) {
//        val statusRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("status")
//        val updates = hashMapOf<String, Any>(
//            "lastSeen" to ServerValue.TIMESTAMP
//        )
//        statusRef.updateChildren(updates)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        val  userId = FirebaseAuth.getInstance().currentUser?.uid
//        updateUserLastSeen(userId!!)
//    }


}
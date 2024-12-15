package com.ismail.sunsetchattingapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismail.sunsetchattingapplication.Adapters.ChatMessageAdapter
import com.ismail.sunsetchattingapplication.ModelClasses.ChatMessage
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.databinding.ActivityChatBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatMessageAdapter
    private var messages: ArrayList<ChatMessage> = ArrayList()

    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)


        binding.apply {
            val usersId = FirebaseAuth.getInstance().currentUser?.uid
//            readOnlineOrOffline(usersId!!)
            // Retrieve the username and profile URL from intent extras
            val userName = intent.getStringExtra("userName")
            val profileUrl = intent.getStringExtra("profileUrl")
            val userId = intent.getStringExtra("userId")



            // Set the username text
            username.text = userName

            // Use Glide to load the profile image
            Glide.with(this@ChatActivity).load(profileUrl).into(binding.profilePic)

            arrowBack.setOnClickListener(){
                var intent = Intent(this@ChatActivity, UsersActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUserId = firebaseAuth.currentUser?.uid
            val chatPartnerId = intent.getStringExtra("userId")
            reyclerMs.layoutManager = LinearLayoutManager(this@ChatActivity,
                LinearLayoutManager.VERTICAL,false)



            adapter = ChatMessageAdapter(currentUserId!!, messages)
            reyclerMs.adapter = adapter

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat")
            readMessages(currentUserId, chatPartnerId!!)



            val MAX_LINES = 5 // Set a maximum number of lines to expand to

            edittextSendMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Dynamically adjust the maxLines based on the content
                    val lineCount = edittextSendMessage.lineCount
                    if (lineCount <= MAX_LINES) {
                        edittextSendMessage.maxLines = lineCount // Allow expansion
                    } else {
                        edittextSendMessage.maxLines = MAX_LINES // Set to max lines and enable scrolling
                    }
                }
            })


            edittextSendMessage.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edittextSendMessage.right - edittextSendMessage.compoundDrawables[2].bounds.width())) {
                        // User tapped on the emoji drawable
                        // Open emoji picker or switch keyboard here
                        showEmojiPicker()
                        return@setOnTouchListener true
                    }
                }
                false
            }


            buttonSendMessage.setOnClickListener(){
                val message = edittextSendMessage.text.toString()

                val firebaseAuth = FirebaseAuth.getInstance()
                val currentUser = firebaseAuth.currentUser?.uid

                if (message.isEmpty()){
                    Toast.makeText(this@ChatActivity, "please write message", Toast.LENGTH_SHORT).show()
                    edittextSendMessage.setText("")
                }else{
                    sendMessage(currentUser!!,userId!!,message)
                    edittextSendMessage.setText("")
                }


            }







        }


    }
    private fun scrollToBottom() {
        if (adapter.itemCount > 0) {
            binding.reyclerMs.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun readMessages(myId: String, userId: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (dataSnapshot in snapshot.children) {
                    val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
                    if (chatMessage != null && chatMessage.receiverId == myId && chatMessage.senderId == userId ||
                        chatMessage?.senderId == myId && chatMessage.receiverId == userId) {
                        messages.add(chatMessage!!)
                    }
                }
                adapter.notifyDataSetChanged()
                scrollToBottom()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showEmojiPicker() {
        Toast.makeText(this, "you clicked emoji", Toast.LENGTH_SHORT).show()
    }


//    private fun sendMessage(senderId: String, receiverdId:String,message:String){
//        val refrence: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
//
//
//        var hashMap: HashMap<String,String> = HashMap()
//        hashMap.put("senderId",senderId)
//        hashMap.put("receiverId",receiverdId)
//        hashMap.put("message",message)
//        refrence?.child("Chat")?.push()?.setValue(hashMap)
//    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        val messageKey = reference.push().key  // Generate a unique key for the message

        val messageMap = hashMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message
        )

        messageKey?.let {
            reference.child(it).setValue(messageMap)
        }
    }

//    fun readOnlineOrOffline(userId: String) { // Pass userId as a parameter
//        val statusRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("status")
//
//        statusRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val isOnline = snapshot.child("isOnline").getValue(Boolean::class.java) ?: false
//                    val lastSeen = snapshot.child("lastSeen").getValue(Long::class.java) ?: -1L
//                    updateStatusTextView(isOnline, lastSeen)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
//    fun updateStatusTextView(isOnline: Boolean, lastSeen: Long) {
//
//        val lastSeenDate = Calendar.getInstance().apply {
//            timeInMillis = lastSeen
//        }
//        val currentDate = Calendar.getInstance()
//
//        if (isOnline) {
//            binding.status.text = "Online"
//            binding.status.setTextColor(Color.GREEN) // Optional: Customize as needed
//        } else {
//            // Determine if the lastSeen timestamp is from today
//            val isToday = currentDate.get(Calendar.YEAR) == lastSeenDate.get(Calendar.YEAR) &&
//                    currentDate.get(Calendar.DAY_OF_YEAR) == lastSeenDate.get(Calendar.DAY_OF_YEAR)
//
//            // Based on the comparison, choose the appropriate message
//            val lastSeenText = if (isToday) {
//                // If the lastSeen is today, format as "today at HH:mm"
//                SimpleDateFormat("HH:mm", Locale.getDefault()).format(lastSeenDate.time)
//            } else {
//                // If the lastSeen is not today, format as "dd/MM/yyyy HH:mm"
//                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(lastSeenDate.time)
//            }
//
//            // Set the text on the TextView
//            binding.status.text = if (isToday) "Last seen today at $lastSeenText" else "Last seen: $lastSeenText"
//            binding.status.setTextColor(Color.RED) // Optional: Customize as needed
//        }
//    }








    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@ChatActivity, UsersActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }

}
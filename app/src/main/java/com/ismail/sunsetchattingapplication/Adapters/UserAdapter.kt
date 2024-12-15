package com.ismail.sunsetchattingapplication.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.core.Context
import com.ismail.sunsetchattingapplication.ModelClasses.User
import com.ismail.sunsetchattingapplication.R
import com.ismail.sunsetchattingapplication.activities.ChatActivity

class UserAdapter( private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userNameTextView.text = user.userName
        holder.userBioTextView.text = user.userBio
        // Load image with Glide or similar library
        Glide.with(holder.userImageView.context).load(user.ProfileUrl)
            .placeholder(R.drawable.user_profiel).into(holder.userImageView)

        holder.layout.setOnClickListener(){
            val intent = Intent(holder.layout.context, ChatActivity::class.java).apply {
                // Put the user name and profile URL as extras
                putExtra("userName", user.userName)
                putExtra("profileUrl", user.ProfileUrl)
                putExtra("userId", user.userId)
            }
            // Start ChatActivity
            holder.layout.context.startActivity(intent)
        }
    }

    override fun getItemCount() = users.size


    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.userName)
        val userImageView: ImageView = view.findViewById(R.id.userProfile)
        val userBioTextView :  TextView = view.findViewById(R.id.userBio)
        val layout : LinearLayout = view.findViewById(R.id.layout)
    }
}

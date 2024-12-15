package com.ismail.sunsetchattingapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ismail.sunsetchattingapplication.ModelClasses.ChatMessage
import com.ismail.sunsetchattingapplication.R

class ChatMessageAdapter(private val userId: String, private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return if (message.senderId == userId) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            LayoutInflater.from(parent.context).inflate(R.layout.message_sent, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.recieve_message, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as MessageViewHolder).bind(message.message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as MessageViewHolder).bind(message.message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: String) {
            if (itemViewType == VIEW_TYPE_MESSAGE_SENT) {
                itemView.findViewById<TextView>(R.id.text_message_body_sent).text = message
            } else {
                itemView.findViewById<TextView>(R.id.text_message_body_received).text = message
            }
        }
    }
}

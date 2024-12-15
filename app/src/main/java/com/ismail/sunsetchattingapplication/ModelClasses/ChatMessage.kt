package com.ismail.sunsetchattingapplication.ModelClasses

data class ChatMessage(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
)

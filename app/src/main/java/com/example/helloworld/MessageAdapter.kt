package com.example.helloworld

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private var messages: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun submitList(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        // A more efficient way would be to use DiffUtil, but for this simple case, this is fine.
        notifyDataSetChanged()
    }

    private const val VIEW_TYPE_USER = 1
    private const val VIEW_TYPE_ASSISTANT = 2

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].sender) {
            Message.SENDER_USER -> VIEW_TYPE_USER
            else -> VIEW_TYPE_ASSISTANT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_assistant, parent, false)
            AssistantViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_USER) {
            (holder as UserViewHolder).bind(message)
        } else {
            (holder as AssistantViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        fun bind(message: Message) {
            messageText.text = message.content
        }
    }

    inner class AssistantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        fun bind(message: Message) {
            messageText.text = message.content
        }
    }
}

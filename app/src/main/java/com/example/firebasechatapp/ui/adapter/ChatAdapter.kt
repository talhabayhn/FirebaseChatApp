package com.example.firebasechatapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.model.Message
import com.example.firebasechatapp.util.TimeUtil
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(var context: Context,var messageList:ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        const val VIEW_TYPE_USER= 1
        const val VIEW_TYPE_FRIEND= 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        var viewHolder : RecyclerView.ViewHolder?= null
        when (viewType){
            VIEW_TYPE_USER->{
                val view= inflater.inflate(R.layout.item_message_me,parent,false)
                viewHolder = CurrentUserViewHolder(view)
            }
            VIEW_TYPE_FRIEND->{
                val view= inflater.inflate(R.layout.item_message_friend,parent,false)
                viewHolder = FriendViewHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(messageList[position].from== FirebaseAuth.getInstance().currentUser?.uid){
            prepareUserViewHolder(holder as CurrentUserViewHolder,position)
        }
        else { prepareFriendViewHolder(holder as FriendViewHolder,position) }
    }

    override fun getItemViewType(position: Int): Int{
        return if (messageList[position].from==FirebaseAuth.getInstance().currentUser?.uid){
            VIEW_TYPE_USER
        }
        else{ VIEW_TYPE_FRIEND }
    }

    fun add(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    override fun getItemCount(): Int = messageList.size






    private fun prepareUserViewHolder(holder: CurrentUserViewHolder,position: Int){
        holder.userMessage.text= messageList[position].message
        holder.userTimeStamp.text= TimeUtil.timeStampToDate(messageList[position].time)
    }
    private fun prepareFriendViewHolder(holder: FriendViewHolder,position: Int){
        holder.friendMessage.text= messageList[position].message
        holder.friendTimeStamp.text= TimeUtil.timeStampToDate(messageList[position].time)
    }

    class CurrentUserViewHolder(view: View): RecyclerView.ViewHolder(view){
        val userMessage= view.findViewById<TextView>(R.id.item_message_me_messageText)
        val userTimeStamp= view.findViewById<TextView>(R.id.item_message_me_timestamp)
    }
    class FriendViewHolder(view: View): RecyclerView.ViewHolder(view){
        val friendMessage= view.findViewById<TextView>(R.id.item_message_friend_messageText)
        val friendTimeStamp= view.findViewById<TextView>(R.id.item_message_friend_timestamp)
    }
}
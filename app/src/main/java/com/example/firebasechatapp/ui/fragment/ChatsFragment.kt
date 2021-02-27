package com.example.firebasechatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.model.User
import com.example.firebasechatapp.ui.activity.ChatActivity
import com.example.firebasechatapp.ui.adapter.FriendsAdapter
import com.example.firebasechatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.chats_fragment.*
import java.lang.Exception

class ChatsFragment : Fragment(), FriendsAdapter.OnFriendClickListener{
    private val mMessageDatabase: DatabaseReference by lazy { FirebaseDatabase.getInstance()
        .reference.child(Constants.MESSAGES) }

    private  val mUserDatabase: DatabaseReference by lazy { FirebaseDatabase.getInstance()
        .reference.child(Constants.CHILD_USERS) }
    private lateinit var  adapter: FriendsAdapter
    private var userList: ArrayList<User> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         return  inflater.inflate(R.layout.chats_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        adapter= FriendsAdapter(activity!!,userList)
        chats_recyclerView.layoutManager= LinearLayoutManager(activity)
        chats_recyclerView.adapter= adapter

        mMessageDatabase.child(currentUserId!!).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(datasnapshot: DataSnapshot, previousChildName: String?) {
                if(datasnapshot.value!= null){
                    try {
                        val chatFriendKey = datasnapshot.ref.key
                        getUsers(chatFriendKey)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        adapter.setOnFriendClickListener(this)
    }

    override fun onFriendClick(user: User) {
        mUserDatabase.orderByChild(Constants.CHILD_NAME).equalTo(user.name)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val clickedUserKey= snapshot.children.iterator().next().ref.key

                    val intent = Intent(activity,ChatActivity::class.java)
                    intent.putExtra(Constants.EXTRA_NAME,user.name)
                    intent.putExtra(Constants.EXTRA_ID,clickedUserKey)
                    startActivity(intent)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    private  fun getUsers(chatFriendKey: String?){
        mUserDatabase.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(datasnapshot: DataSnapshot, previousChildName: String?) {
                if(datasnapshot.value != null){
                    val users = datasnapshot.getValue(User::class.java)
                    val friendKey= datasnapshot.ref.key

                    if(chatFriendKey== friendKey){
                        userList.add(users!!)
                        adapter.notifyItemInserted(userList.size-1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}
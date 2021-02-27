package com.example.firebasechatapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.model.User
import com.example.firebasechatapp.ui.activity.ChatActivity
import com.example.firebasechatapp.ui.adapter.FriendsAdapter
import com.example.firebasechatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_friends.*
import java.lang.Exception

class FriendsFragment : Fragment(), FriendsAdapter.OnFriendClickListener{
    private val mUserDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child(Constants.CHILD_USERS)
    }
    private lateinit var adapter: FriendsAdapter
    private var userList : ArrayList<User> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friends,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        adapter= FriendsAdapter(activity!!,userList)
        friends_recyclerView.layoutManager= LinearLayoutManager(activity)
        friends_recyclerView.adapter= adapter

        mUserDatabase.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value!= null){
                    try{
                        val model = snapshot.getValue(User::class.java)
                        val friendKey = snapshot.ref.key
                        if(!currentUserId.equals(friendKey)){
                            userList.add(model!!)
                            adapter.notifyItemInserted(userList.size-1)
                        }

                    }catch (e:Exception){
                        e.message?.let { Log.e("onChildAdded", it) } ////
                    }
                }
                else{

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
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val clickedUserKey= snapshot.children.iterator().next().ref.key

                    val intent = Intent(activity, ChatActivity::class.java)
                    intent.putExtra(Constants.EXTRA_NAME,user.name)
                    intent.putExtra(Constants.EXTRA_ID,clickedUserKey)
                    startActivity(intent)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
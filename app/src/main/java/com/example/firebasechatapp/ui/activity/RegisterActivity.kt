package com.example.firebasechatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebasechatapp.R
import com.example.firebasechatapp.util.Constants
import com.example.firebasechatapp.util.gone
import com.example.firebasechatapp.util.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private lateinit var  mReference : DatabaseReference
    private lateinit var mUserReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener { 
            val userName= register_username.text.toString()
            val email= register_email.text.toString()
            val password= register_password.text.toString()
            
            if(email.isNotEmpty()&& userName.isNotEmpty()&&password.isNotEmpty()){
                if(password.length>=6){
                    register_progressbar.visible()
                    registerUser(userName,email,password)
                }
                else{
                    register_password.error="Password has at least 6 characters"
                }
            }
            else{
                if(email.isEmpty()) register_email.error="Enter your email"
                if(password.isEmpty()) register_email.error="Enter your password"
                if(userName.isEmpty()) register_email.error="Enter your username"
            }
        }
        register_login_button.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Unsucessfull",Toast.LENGTH_LONG).show()
                register_progressbar.gone()
            }else{
                val currentUser = mAuth.currentUser
                val userId = currentUser?.uid

                mReference= mDatabase.reference
                mUserReference= mReference.child(Constants.CHILD_USERS).child(userId!!)

                val userMap = HashMap<String,String>()
                userMap["name"]= userName
                userMap["profile_image"]= "no_image"
                userMap["status"]= "I am using ChatApp"

                mUserReference.setValue(userMap).addOnCompleteListener {
                    Toast.makeText(this,"Succesfull",Toast.LENGTH_LONG).show()
                    register_progressbar.gone()

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    finish()
                }
            }
        }
    }
}
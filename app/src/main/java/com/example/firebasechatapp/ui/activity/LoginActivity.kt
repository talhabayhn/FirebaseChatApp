package com.example.firebasechatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebasechatapp.R
import com.example.firebasechatapp.util.gone
import com.example.firebasechatapp.util.visible
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            val email : String = login_email.text.toString()
            val password : String = login_password.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                login_progressbar.visible()
                login(email,password)
            }
            else{
                if(email.isEmpty()) login_email.error="Enter your email."
                if(password.isEmpty()) login_password.error="Enter your password"
            }
        }

        login_register_button.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }
    private fun login (email : String , password:String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Unsuccesfull",Toast.LENGTH_LONG).show()
                login_progressbar.gone()
            }else{
                login_progressbar.gone()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
            }
        }
    }
}
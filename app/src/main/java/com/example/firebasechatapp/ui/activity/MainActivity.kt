package com.example.firebasechatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.firebasechatapp.R
import com.example.firebasechatapp.ui.adapter.ViewPagerAdapter
import com.example.firebasechatapp.ui.fragment.ChatsFragment
import com.example.firebasechatapp.ui.fragment.FriendsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private  val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_logout->{
                mAuth.signOut()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            R.id.action_profile->{
                startActivity(Intent(this,ProfileActivity::class.java))
            }
        }
        return true
    }

    private  fun setupUI(){
        setSupportActionBar(main_toolbar)
        setupViewPager()
        main_tabs.setupWithViewPager(main_viewPager)
    }
    private fun setupViewPager(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.apply {
            addFragment(ChatsFragment(),"Messages")
            addFragment(FriendsFragment(),"Friends")
        }
        main_viewPager.adapter= adapter
    }
}
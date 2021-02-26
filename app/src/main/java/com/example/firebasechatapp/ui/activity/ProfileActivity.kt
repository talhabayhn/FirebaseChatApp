package com.example.firebasechatapp.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firebasechatapp.R
import com.example.firebasechatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.profile_page_content.*
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth         by lazy { FirebaseAuth.getInstance() }
    private val mDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val mStorageRef: StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private val mCurrentUser: FirebaseUser  by lazy { mAuth.currentUser!! }

    private val GALLERY_REQUEST_CODE = 2525
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mUserReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(profile_page_toolbar)
        setUserDatas()

        change_img_fab.setOnClickListener {changeImage()}

        allStatus.setOnClickListener {showStatusChangeDialog()}

    }


    private fun setUserDatas() {
        val userId = mCurrentUser.uid
        mUserReference = mDatabase.reference.child(Constants.CHILD_USERS).child(userId)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child(Constants.CHILD_NAME).value.toString()
                val profileImage = snapshot.child(Constants.CHILD_PPIMAGE).value.toString()
                val status = snapshot.child(Constants.CHILD_STATUS).value.toString()

                profile_page_collapsing.title = name
                profile_page_toolbar.title = name
                status_text.text = status

                if (profileImage == "no_image") {
                    Glide.with(applicationContext).load(R.drawable.ic_baseline_user)
                        .into(profile_page_profileimg)
                } else {
                    Glide.with(applicationContext).load(profileImage).into(profile_page_profileimg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog = ProgressDialog(this).apply {
                title = "Please wait"
                setMessage("Photo is loading..")
                setCancelable(false)
                show()
            }
            try {
                val imageUri = data?.data
                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                profile_page_profileimg.setImageBitmap(selectedImage)

                val filePath = mStorageRef.child(Constants.PPFOLDER)
                    .child("${System.currentTimeMillis()}-${mCurrentUser.uid}")

                filePath.putFile(imageUri).continueWithTask { task ->
                    if (!task.isSuccessful) task.exception?.let { throw  it }
                    filePath.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val downloadUri = it.result
                        saveImageUrltoStorage(downloadUri.toString())
                    }
                    else{
                        Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
            }
        }

        else{
            Toast.makeText(this,"Nothing choosen",Toast.LENGTH_LONG).show()
        }


    }

    private fun saveImageUrltoStorage(url: String) {
        mDatabase.reference.child(Constants.CHILD_USERS).child(mCurrentUser.uid).child(Constants.CHILD_PPIMAGE)
            .setValue(url).addOnCompleteListener {
                if(it.isComplete){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Photo loaded",Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun changeImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose a profile photo"),
            GALLERY_REQUEST_CODE
        )
    }

    private fun showStatusChangeDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog2, null)
        dialogBuilder.setView(dialogView)

        val edt: EditText= dialogView.findViewById(R.id.dialog_status_text)
        dialogBuilder.setTitle("Change the status")
        dialogBuilder.setPositiveButton("Apply") { dialog, which->
            val dialogStatusText = edt.text.toString()
            setStatusText(dialogStatusText)
        }
        dialogBuilder.setNegativeButton("Cancel"){ dialog, which->
            dialog.dismiss()
        }
        val dialog= dialogBuilder.create()
        dialog.show()
    }
    private fun setStatusText(status : String){
        mDatabase.reference.child(Constants.CHILD_USERS).child(mCurrentUser.uid).child(Constants.CHILD_STATUS)
            .setValue(status).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Status changed",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this,"Not changed",Toast.LENGTH_LONG).show()
                }
            }
    }
}
package com.example.greenurth

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*



class ProfileActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private var uploadFileName: String? = null

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    //access database table
    private var userDatabase: DatabaseReference? = null
    //to get the current database pointer
    private var userReference: DatabaseReference? = null
    private var userListener: ChildEventListener? = null

    //get current user
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //val currentUser =intent.getStringExtra("currentUser")

        //INITIALISE FIREBASEDATABASE
        userDatabase = FirebaseDatabase.getInstance().reference

        userReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://greenurth-1dee5.firebaseio.com/user")

       // Display  current user data via their login email address
        val userReference = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val username :String= dataSnapshot.child("user").child(currentUser!!.uid).child("userName").value.toString()
                    var email = currentUser!!.email
                    var contactNo = dataSnapshot.child("user").child(currentUser!!.uid).child("userContactNo").value.toString()
                    var address = dataSnapshot.child("user").child(currentUser!!.uid).child("userAddress").value.toString()
                    var profilePicture = dataSnapshot.child("user").child(currentUser!!.uid).child("userProfilePicture").value.toString()

                    editTextProfileUsername.setText(username)
                    editTextProfileEmail.setText(email)
                    editTextOrganisationContactNo.setText(contactNo)
                    editTextOrganisationAddress.setText(address)
                    Picasso.get().load(profilePicture).into(imageButtonUploadProfile)
                }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        userDatabase!!.addValueEventListener(userReference)

        // TO ENABLE EDITING
        buttonEdit.setOnClickListener {

            imageButtonUploadProfile.isClickable = true
            editTextProfileUsername.isEnabled = true
            editTextOrganisationAddress.isEnabled = true
            editTextOrganisationContactNo.isEnabled = true
        }

        //TO UPLOAD IMAGE
        imageButtonUploadProfile.setOnClickListener {
            //check runtime permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(this?.let { it1 ->
                        ContextCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    } == PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //always request for permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }else{
                    //permission granted
                    pickImageFromGallery()
                }
            }else{
                pickImageFromGallery()
            }
        }

        //TO SAVE CHANGES
        buttonSave.setOnClickListener {
            updateUserDetails()
            imageButtonUploadProfile.isClickable = false
            editTextProfileUsername.isEnabled = false
            editTextOrganisationAddress.isEnabled = false
            editTextOrganisationContactNo.isEnabled = false
        }
    }



    private fun updateUserDetails(){
        //val locUrl:Uri? = storageReference!!.child("profImages/$uploadFileName").getDownloadUrl().getResult()
        val username:String = editTextProfileUsername.text.toString()
        val userAddress: String = editTextOrganisationAddress.text.toString()
        val userEmail: String = editTextProfileEmail.text.toString()
        val userContactNo: String= editTextOrganisationContactNo.text.toString()
        val userProfilePicture: String = "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwjSjqyHtf3mAhVrxDgGHdwCByoQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.pinterest.com%2Fpin%2F521995413059056104%2F&psig=AOvVaw1chZfUAzbKlR34KCY1y4mM&ust=1578896257966983"
        val user = User(username
            , userEmail
            , userContactNo
            , userAddress
            ,userProfilePicture)

        userDatabase!!.child("user").child(currentUser!!.uid).child("userName").setValue(user.userName)
        userDatabase!!.child("user").child(currentUser!!.uid).child("userEmail").setValue(user.userEmail)
        userDatabase!!.child("user").child(currentUser!!.uid).child("userContactNo").setValue(user.userContactNo)
        userDatabase!!.child("user").child(currentUser!!.uid).child("userAddress").setValue(user.userAddress)
        userDatabase!!.child("user").child(currentUser!!.uid).child("userProfilePicture").setValue(user.userProfilePicture)

//        val userValues = user.toMap()
//        val childUpdates = HashMap<String, Any>()
//
//        //ADD NEW ENTITY
//        val key = userDatabase!!.child("user").push().key
//
//        //ADD THE INTO THE NEWLY CREATED ENTITY
//        childUpdates.put("/user/$key", userValues)
//
//        userDatabase!!.updateChildren(childUpdates)
//        Toast.makeText(this, "Successfully Updated Profile Details", Toast.LENGTH_SHORT).show()
    }

    private fun uploadFile(){
        if(filePath != null){
            Toast.makeText(this,"Uploading", Toast.LENGTH_SHORT).show()
        }else{
            uploadFileName = UUID.randomUUID().toString()
            val imageRef = storageReference!!.child("profImages/" + uploadFileName)
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    Toast.makeText(this,"File success to uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this,"File failed to upload", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    companion object {
        //IMAGE PICK CODE
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }else{
                    //permission from popup denied
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //handle image pick result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Picasso.get().load(data?.data).into(imageButtonUploadProfile)
            filePath = data?.data
        }
    }
}

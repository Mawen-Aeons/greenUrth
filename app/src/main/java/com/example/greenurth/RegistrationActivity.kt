package com.example.greenurth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    //access database table
    private var userDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        //INITIALISE FIREBASEDATABASE
        userDatabase = FirebaseDatabase.getInstance().reference


        buttonSubmit.setOnClickListener {
            //addNewUser()
            registration()
        }

        buttonCancel.setOnClickListener {

            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun registration(){

        //START OF VALIDATION FOR REGISTRATION
        if(editTextRegisterEmail.text.toString().isEmpty()){
            editTextRegisterEmail.error = "Email Address cannot be empty!"
            editTextRegisterEmail.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(editTextRegisterEmail.text.toString()).matches()){

            editTextRegisterEmail.error = "Please enter a valid Email Address!"
            editTextRegisterEmail.requestFocus()
            return
        }

        if(editTextRegisterPassword.text.toString().isEmpty()){
            editTextRegisterPassword.error = "Password cannot be empty!"
            editTextRegisterPassword.requestFocus()
            return
        }
        //END OF VALIDATION FOR REGISTRATION

        //CREATE USER
        mAuth.createUserWithEmailAndPassword(editTextRegisterEmail.text.toString(), editTextRegisterPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = mAuth.currentUser

                    //To send verification email to user
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // successful creation of user and redirect back to login page while ending this activity
                                Toast.makeText(this, "Successfully created an account!", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                        }

                }
                else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Error, failed to create an account!", Toast.LENGTH_SHORT).show()

                }

            }
    }

//    private fun addNewUser(){
//        //val locUrl:Uri? = storageReference.child("locImages/$uploadFileName").getDownloadUrl().getResult()
//        val username:String? = ""
//        val userAddress: String? = ""
//        val userEmail: String = editTextRegisterEmail.text.toString()
//        val userContactNo: String?= ""
//        val userProfilePicture: String = "https://ih0.redbubble.net/image.524527453.3004/flat,1000x1000,075,f.u1.jpg"
//        val user = User(username
//            , userEmail
//            , userContactNo
//            , userAddress
//            ,userProfilePicture)
//
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
//    }
}

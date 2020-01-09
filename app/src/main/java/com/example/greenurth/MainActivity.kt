package com.example.greenurth

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forgot_password_dialog.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance()


        visibleButton.setOnClickListener {

            if (visibleButton.text.toString().equals("-")) {

                editTextPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                visibleButton.setBackgroundResource(R.drawable.visible_30dp)
                visibleButton.text = ""
            } else {

                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                visibleButton.setBackgroundResource(R.drawable.not_visible_30dp)
                visibleButton.text = "-"
            }

        }

        buttonRegister.setOnClickListener {

            var i = Intent(this, RegistrationActivity::class.java)
            startActivity(i)
        }

        buttonLogin.setOnClickListener {

            login()

        }

        buttonForgotPassword.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter registered email to reset password")
            val view = layoutInflater.inflate(R.layout.forgot_password_dialog,null)
            val email = view.findViewById<EditText>(R.id.editTextResetPasswordEmail)
            builder.setView(view)

            builder.setPositiveButton("RESET", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(email)
            })
            builder.setNegativeButton("CANCEL", DialogInterface.OnClickListener { _, _ ->  })
              builder.show()



        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if(currentUser.isEmailVerified){

                //ADD next page code
            }
        }
    }

    private fun login() {

            //START OF VALIDATION FOR REGISTRATION
            if (editTextEmail.text.toString().isEmpty()) {
                editTextEmail.error = "Email Address cannot be empty!"
                editTextEmail.requestFocus()
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) {

                editTextEmail.error = "Please enter a valid Email Address!"
                editTextEmail.requestFocus()
                return
            }

            if (editTextPassword.text.toString().isEmpty()) {
                editTextPassword.error = "Password cannot be empty!"
                editTextPassword.requestFocus()
                return
            }
            //END OF VALIDATION FOR REGISTRATION

            mAuth.signInWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Please verify your email", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }



    private fun forgotPassword(email : EditText){
        if(email.text.toString().isEmpty()){
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return

        }

        mAuth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password has been reset, please check your email",Toast.LENGTH_LONG).show()
                }
            }
    }
}


package com.example.safespace_admin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.safespace_admin.entity.Admin
import com.google.firebase.auth.FirebaseAuth




class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportActionBar?.title = "Admin Safe Space"
        button_login.setOnClickListener {
            processLogin()
        }


    }
    private fun processLogin(){
        //Loading animation
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()


        val email = editTextEmail.text.toString().trim()
        val password = editTextPass.text.toString().trim()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            // Connecting to Firebase Auth
            val auth = FirebaseAuth.getInstance()


            //Getting login credentials
            val email = editTextEmail.text.toString().trim()
            val password = editTextPass.text.toString().trim()
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(this) {

                Log.d(TAG,auth.uid)
                if(auth.uid!=null){
                    //Connecting to firebase database
                    val ref = FirebaseDatabase.getInstance().getReference("/admin").child("${auth.uid}")
                    ref.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            val admin = p0.getValue(Admin::class.java)
                            if(admin!=null){
                                pDialog.dismissWithAnimation()
                                //If the user id exists as an  admin, take user to admin dashboard
                                val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                            }
                            else{
                                pDialog.dismissWithAnimation()
                                showError("Login","Please enter right combination of email and password")

                            }

                        }


                        override fun onCancelled(p0: DatabaseError) {
                            pDialog.dismissWithAnimation()
                            Log.d(TAG,"onCancelled")
                            showError("Login", "Error:${p0.message}")
                        }
                    })
                }


                //Check whether login user is admin



            }.addOnFailureListener {
                pDialog.dismissWithAnimation()
                showError("Login","Please enter right combination of email and password")

            }
        }
        else{
            pDialog.dismissWithAnimation()
            showError("Login", "Login email and password cannot be empty")

        }
    }

    private fun showError(title: String, msg: String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }
}

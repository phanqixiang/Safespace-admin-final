package com.example.safespace_admin



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_livechat.*
import java.lang.ref.Reference


class LivechatActivity: AppCompatActivity() {
    val TAG = "LivechatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livechat)

        supportActionBar?.title = "Livechat"


        loadTrainingTxt()


        var listener: ChildEventListener? = null
        val ref = FirebaseDatabase.getInstance().getReference("/users")


        button_ready.setOnClickListener {
            val adminId = FirebaseAuth.getInstance().uid?:""
            //Change admin status
            val ref1 = FirebaseDatabase.getInstance().getReference("/admin").child(adminId).child("status")
            ref1.setValue("ready")
            button_ready.visibility = View.GONE
            button_cancel.visibility = View.VISIBLE


            listener  = ref.addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG,"Stop Listening")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val status = p0.child("status").value.toString()
                    val userId = p0.child("userId").value.toString()
                    val userName = p0.child("nickname").value.toString()
                    val adminId = FirebaseAuth.getInstance().uid

                    if(adminId.equals(status)){

                        val intent = Intent(this@LivechatActivity, ChatroomActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("userName", userName)
                        startActivity(intent)

                    }
                    Log.d(TAG, "Status:${status}")



                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    val status = p0.child("status").value.toString()
                    val userId = p0.child("userId").value.toString()
                    val userName = p0.child("nickname").value.toString()
                    val adminId = FirebaseAuth.getInstance().uid

                    if(adminId.equals(status)){
                        val intent = Intent(this@LivechatActivity, ChatroomActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("userName", userName)
                        startActivity(intent)

                    }
                    Log.d(TAG, "Status:${status}")
                    Log.d(TAG,"OnChanged")
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }
            })

        }

        button_cancel.setOnClickListener {
            ref.removeEventListener(listener!!)

            button_ready.visibility = View.VISIBLE
            button_cancel.visibility = View.GONE

            val adminId = FirebaseAuth.getInstance().uid?:""
            val ref = FirebaseDatabase.getInstance().getReference("/admin").child(adminId).child("status")
            ref.setValue("offline")

        }

    }

    private fun setupUserListener(){



    }


    private fun loadTrainingTxt(){
        val showPatienceTxt = "Show <b>P</b>atience"
        val showEmpathyTxt = "Show <b>E</b>mpathy"
        val showKindnessTxt = "Show <b>K</b>indness"
        val showUnderstandingTxt = "Show <b>U</b>nderstanding"
        textView7.setText(Html.fromHtml(showPatienceTxt))
        textView8.setText(Html.fromHtml(showEmpathyTxt))
        textView9.setText(Html.fromHtml(showKindnessTxt))
        textView11.setText(Html.fromHtml(showUnderstandingTxt))
    }

}
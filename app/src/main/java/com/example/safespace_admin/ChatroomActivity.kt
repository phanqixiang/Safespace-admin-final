package com.example.safespace_admin

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.safespace_admin.entity.Admin
import com.example.safespace_admin.entity.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatroom.*
import kotlinx.android.synthetic.main.chat_message_from.view.*
import kotlinx.android.synthetic.main.chat_message_to.view.*
import kotlinx.android.synthetic.main.chat_user_offline.view.*
import kotlinx.android.synthetic.main.chat_user_online.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatroomActivity : AppCompatActivity() {
    val TAG = "ChatroomActivity"
    val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var senderId: String
    private lateinit var senderName: String

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        resetStatus()
        removeChat()
        super.onStop()
    }

    private fun resetStatus() {
        val adminId = FirebaseAuth.getInstance().uid ?: ""
        val ref =
            FirebaseDatabase.getInstance().getReference("/admin").child(adminId).child("status")
        ref.setValue("offline")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_chatroom)


        supportActionBar?.title = "Chatroom"

        senderName=  ""

        // Getting intent data
        val bundle = intent.extras
        if (bundle != null) {
            receiverId = String.format("%s", bundle.get("userId"))
            receiverName = String.format("%s", bundle.get("userName"))


        }

        //Getting admin name
        senderId = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/admin").child(senderId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                senderName = p0.child("name").value.toString()


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        //Display chat person
        adapter.add(UserOnline(receiverName, getCurrentTime()))


        //Listening for message
        setupMessageListener()


        //Listening to user status change
        setupStatusListener()


        //Setup chat button
        sendBtn.setOnClickListener {
            sendMessage(receiverId)
        }


        // Setup recycler for chat logs
        findViewById<RecyclerView>(R.id.recyclerViewChat).adapter = adapter

        // Must be defined for recycler view to work(can be defined in xml too)
        findViewById<RecyclerView>(R.id.recyclerViewChat).layoutManager =
            LinearLayoutManager(this@ChatroomActivity)


    }

    private fun sendMessage(receiverId: String) {
        //Getting input chat message
        val senderId = FirebaseAuth.getInstance().uid
        val messageTxt = editTextChat.text.toString().trim()


        7
        if (messageTxt.isNotEmpty() && senderId != null) {


            val ref = FirebaseDatabase.getInstance().getReference("/message").push()
            val chat = Message(
                ref.key!!,
                messageTxt,
                senderId,
                receiverId,
                System.currentTimeMillis(),
                getCurrentTime()
            )

            ref.setValue(chat).addOnSuccessListener {
                Log.d(TAG, "Saved our chat message:${ref.key}")

            }.addOnFailureListener {
                showError(
                    "Livechat",
                    "Message cannot be sent. Check your network connection and try again"
                )
            }

            //Clear input chat box
            editTextChat.setText("")

            editTextChat.requestFocus()
        }


    }

    //Get current time in 24hour format
    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm")
        val time24Format = sdf.format(Date())


        return time24Format
    }

    private fun setupMessageListener() {
        val ref = FirebaseDatabase.getInstance().getReference("/message").orderByChild("senderId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val msg = p0.getValue(Message::class.java)
                if (msg != null) {

                    if (receiverId.equals(msg.receiverId) && senderId.equals(msg.senderId)) {
                        adapter.add(ChatToItem(senderName, msg.messageTxt, msg.time))
                    } else if (senderId.equals(msg.receiverId)) {
                        adapter.add(ChatFromItem(receiverName, msg.messageTxt))
                    }
                    findViewById<RecyclerView>(R.id.recyclerViewChat).scrollToPosition(
                        findViewById<RecyclerView>(
                            R.id.recyclerViewChat
                        ).adapter!!.itemCount - 1
                    )
                }


            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "Changed")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })


    }

    private fun setupStatusListener() {
        val ref = FirebaseDatabase.getInstance().getReference("/users").child(receiverId)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                val status = p0.child("status").value.toString()
                Log.d(TAG, status)

                adapter.add(UserOffline(receiverName, getCurrentTime()))

                //Disable text
                editTextChat.setFocusable(false)
                editTextChat.setEnabled(false)
                editTextChat.setBackgroundColor(resources.getColor(R.color.colorlightGrey))
                editTextChat.setCursorVisible(false)
                editTextChat.setKeyListener(null)

                //Disable send button
                sendBtn.setEnabled(false)
                sendBtn.background = ContextCompat.getDrawable(this@ChatroomActivity, R.drawable.offline_send_button)


            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    class ChatFromItem(val receiverName: String, val msg: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.message_from_content.text = "From Message"
            viewHolder.itemView.message_from_name.text = receiverName
            viewHolder.itemView.message_from_content.text = msg
        }

        override fun getLayout(): Int {
            return R.layout.chat_message_from
        }
    }

    class ChatToItem(val nickname: String, val msg: String, val time: String) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.message_to_name.text = nickname
            viewHolder.itemView.message_to_content.text = msg
            viewHolder.itemView.message_to_time.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_message_to
        }
    }

    class UserOffline(val nickname: String, val time: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.offlineMessage.text = "${nickname} has left the chatroom"
            viewHolder.itemView.offlineTime.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_user_offline
        }
    }


    class UserOnline(val nickname: String, val time: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.onlineMessage.text = "${nickname} has joined the chatroom"
            viewHolder.itemView.onlineTime.text = time
        }

        override fun getLayout(): Int {
            return R.layout.chat_user_online
        }
    }

    private fun removeChat() {
        val ref = FirebaseDatabase.getInstance().getReference("/message").orderByChild("senderId")
            .equalTo(senderId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    it.ref.removeValue()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun showError(title: String, msg: String) {

        SweetAlertDialog(this@ChatroomActivity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(msg)
            .show()
    }


}


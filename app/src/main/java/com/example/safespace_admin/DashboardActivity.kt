package com.example.safespace_admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity: AppCompatActivity(){
    val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)


        supportActionBar?.title="Admin Dashboard"


        // If the admin has logged in before on the device, he or she comes straight to this page without login again and again
        val userId = FirebaseAuth.getInstance().uid
        if(userId==null){
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }



        button_livechat.setOnClickListener {
            val intent = Intent(this, LivechatActivity::class.java)
            startActivity(intent)
        }





        button_report.setOnClickListener {
            //
        }

        btn_danger_zone.setOnClickListener {
            val intent = Intent(this, dangerAreaAdmin::class.java)
            startActivity(intent)
        }

        button_article.setOnClickListener {
            val intent = Intent(this, ArticlePost::class.java)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@DashboardActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.admin_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
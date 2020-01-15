package com.example.safespace_admin

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.safespace_admin.entity.dangerArea
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_danger_area_admin.*
import org.w3c.dom.Text
import java.time.LocalTime

class dangerAreaAdmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_danger_area_admin)

        val ref = FirebaseDatabase.getInstance().getReference("DangerArea")
        val LID = ref.push().key

        val btn = findViewById(R.id.btnS) as Button
        btn.setOnClickListener() {
            val Desc = findViewById(R.id.desc) as TextView
            val la = findViewById(R.id.Lati) as TextView
            val laValue = la.text.toString()
            val lo = findViewById(R.id.Long) as TextView
            val loValue = lo.text.toString()
            val rad = findViewById(R.id.rad) as TextView
            val radValue = rad.text.toString()
            val sos = dangerArea(desc.text.toString(), laValue.toDouble(), loValue.toDouble(), radValue.toDouble())

            ref.child(LID.toString()).setValue(sos).addOnSuccessListener {
                val build = AlertDialog.Builder(this)
                build.setTitle("Done")
                build.setMessage("Complete")
                build.setPositiveButton("yes", null);
                build.show()
            }
        }
    }
}

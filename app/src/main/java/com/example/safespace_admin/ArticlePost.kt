package com.example.safespace_admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safespace_admin.entity.Article
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.post_article.*
import java.io.IOException
import java.util.*

class ArticlePost : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var title: EditText
    lateinit var desc : EditText
    lateinit var upload : Button
    lateinit var chooseImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_article)

        title = findViewById(R.id.title)
        desc = findViewById(R.id.description)
        upload = findViewById(R.id.upload)
        chooseImage = findViewById(R.id.imageFile)

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        upload.setOnClickListener{
            postArticle()
        }

        chooseImage.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun postArticle(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            ref?.putFile(filePath!!)?.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                Toast.makeText(this@ArticlePost, "Image Uploaded", Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    val title = title.text.toString()
                    val desc = desc.text.toString()

                    val image = "$it"

                    val ref = FirebaseDatabase.getInstance().getReference("articles")

                    val articleId = ref.push().key.toString()

                    val article = Article(articleId, image, title, desc)

                    ref.child(articleId).setValue(article).addOnCompleteListener {
                        Toast.makeText(applicationContext, "Succesfully", Toast.LENGTH_LONG).show()
                    }
                }
            })?.addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(this@ArticlePost, "Image Uploading Failed " + e.message, Toast.LENGTH_SHORT).show()
            })
        }else{
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }
}
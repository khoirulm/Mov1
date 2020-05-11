package com.kmproject.mov.sign.signup

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.kmproject.mov.home.HomeActivity
import com.kmproject.mov.R
import com.kmproject.mov.utils.Preferences
import kotlinx.android.synthetic.main.activity_sign_up_photoscreen.*
import java.util.*
import android.app.Activity
import com.github.dhaval2404.imagepicker.ImagePicker

class SignUpPhotoscreenActivity : AppCompatActivity(), PermissionListener{


    val REQUEST_IMAGE_CAPTURE = 1
    var statusAdd:Boolean = false
    lateinit var filePath: Uri

    lateinit var storage: FirebaseStorage
    lateinit var  storageReferance: StorageReference
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_photoscreen)

        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance();
        storageReferance = storage.getReference();
        tv_hello.text = "Welcome\n"+intent.getStringExtra("nama")

        iv_add.setOnClickListener{
            if(statusAdd){
                statusAdd = false
                btn_save.visibility = View.INVISIBLE
                iv_add.setImageResource(R.drawable.ic_btn_upload)
                iv_profile.setImageResource(R.drawable.user_pic)

            } else {
                ImagePicker.with(this)
                    .cameraOnly()	//User can only capture image using Camera
                    .start()
            }
        }

        btn_home.setOnClickListener {

            finishAffinity()

            val intent = Intent(this@SignUpPhotoscreenActivity,
                HomeActivity::class.java)
            startActivity(intent)
        }
        btn_save.setOnClickListener {
            if (filePath != null){
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                val ref = storageReferance.child("images/" + UUID.randomUUID().toString())
                ref.putFile(filePath)
                    .addOnSuccessListener{
                        progressDialog.dismiss()
                        Toast.makeText(this@SignUpPhotoscreenActivity, "Uploaded", Toast.LENGTH_LONG).show()

                        ref.downloadUrl.addOnSuccessListener {
                            preferences.setValues("url", it.toString())
                        }

                        finishAffinity()
                        val intent = Intent(this@SignUpPhotoscreenActivity,
                            HomeActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener{ e ->
                        progressDialog.dismiss()
                        Toast.makeText(this@SignUpPhotoscreenActivity, "Failed" + e.message, Toast.LENGTH_LONG).show()

                    }
                    .addOnProgressListener{ taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                        progressDialog.setMessage("Uploaded" + progress.toInt() + "%")
                    }
            }
        }

    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
       //To change body of created functions use File | Settings | File Templates.
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }

        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: com.karumi.dexter.listener.PermissionRequest?,
        token: PermissionToken?
    ) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(this, "You didn't add a profile photo", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Hasty? Click the Upload Later button", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            statusAdd = true
            filePath = data?.data!!


           Glide.with(this)
               .load(filePath)
               .apply(RequestOptions.circleCropTransform())
               .into(iv_profile)

            btn_save.visibility = View.VISIBLE
            iv_add.setImageResource(R.drawable.ic_btn_delete)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}

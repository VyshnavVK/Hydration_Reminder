package app.vyshnav.machinetest.UI.EditProfile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import app.vyshnav.machinetest.Database.db
import app.vyshnav.machinetest.UI.Profile.ProfileActivity
import app.vyshnav.machinetest.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditProfileBinding
     var resultUri : Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                resultUri = result.uri
                Glide.with(this).load(resultUri).into(binding.imgProfile)
                Glide.with(this@EditProfileActivity)
                    .load(resultUri)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(binding.imgBg)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    fun validation():Boolean = binding.etUsername.text.isNotEmpty()


    override fun onBackPressed() {
        startActivity(
            Intent(
                this@EditProfileActivity,
                ProfileActivity::class.java
            )
        )
        super.onBackPressed()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val db = Room.databaseBuilder(
            applicationContext,
            db::class.java, "profile_db"
        ).build()

        binding.apply {

            imgProfile.setOnClickListener {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this@EditProfileActivity)
            }

            lifecycleScope.launch (Dispatchers.IO){
                val profile =   db.ProfileDAO().getProfile()
                lifecycleScope.launch (Dispatchers.Main){
                    val image = File(profile?.imageLocation)

                    Glide.with(this@EditProfileActivity).load(image).into(imgProfile)

                    Glide.with(this@EditProfileActivity)
                        .load(image)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                        .into(imgBg)

                    etUsername.setText(profile?.name)
                    tvEmail.text = "Email : ${profile?.email}"
                    tvDOB.text = "DOB : ${profile?.dob}"
                    tvGender.text = "Gender : ${profile?.gender}"
                }
            }

            binding.btnSave.setOnClickListener {
                if(validation()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val profile = db.ProfileDAO().getProfile()
                        db.ProfileDAO().editProfile(
                            path = (resultUri?.path ?: profile?.imageLocation).toString(),
                            name = binding.etUsername.text.toString()
                        )
                        lifecycleScope.launch(Dispatchers.Main) {
                            startActivity(
                                Intent(
                                    this@EditProfileActivity,
                                    ProfileActivity::class.java
                                )
                            )
                            finish()
                        }

                    }
                }else{
                   binding.etUsername.error = "Username cannot be empty!"
                }
            }
            imgClose.setOnClickListener {
                startActivity(
                    Intent(
                        this@EditProfileActivity,
                        ProfileActivity::class.java
                    )
                )
                finish()
            }
        }
    }
}
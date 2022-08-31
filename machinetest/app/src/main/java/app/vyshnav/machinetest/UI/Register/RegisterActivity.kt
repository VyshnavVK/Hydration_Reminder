package app.vyshnav.machinetest.UI.Register

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import app.vyshnav.machinetest.Database.Profile
import app.vyshnav.machinetest.Database.db
import app.vyshnav.machinetest.UI.Login.MainActivity
import app.vyshnav.machinetest.Util.Util.isValidEmail
import app.vyshnav.machinetest.databinding.ActivityRegisterBinding
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
     var resultUri : Uri? = null
    fun selectDate(){


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            binding.etDOB.setText("$dayOfMonth/${monthOfYear+1}/$year")
        }, year, month, day)

        dpd.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                 resultUri = result.uri
                Glide.with(this).load(resultUri).into(binding.imgProfile)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Room.databaseBuilder(
            applicationContext,
            db::class.java, "profile_db"
        ).build()

        binding.apply {
        etDOB.setOnClickListener {
            selectDate()
        }
        imgProfile.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this@RegisterActivity)
        }

        imgBack.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            if(validation()){
               val profile = Profile(name=binding.etName.text.toString(),
                   dob =  binding.etDOB.text.toString(),
                   email = binding.etEmail.text.toString(),
                   password = binding.etPassword.text.toString(),
                   imageLocation = resultUri?.path.toString(),
                   isLoggedIn = 0,
                   id= null,
                   gender = findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId).text.toString()
               )
                lifecycleScope.launch(Dispatchers.IO) {
                    db.ProfileDAO().insertProfile(profile)
                        lifecycleScope.launch (Dispatchers.Main){
                            Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                            finish()
                        }

                }
            }
        }

        }
    }

    private fun validation():Boolean {
        binding.apply {
            return if (etEmail.text.isNotEmpty()
                && etDOB.text.isNotEmpty()
                && etPassword.text.isNotEmpty()
                && etName.text.isNotEmpty()
                && resultUri !=null
                ) {
                if (!etEmail.text.toString().isValidEmail()) {
                    etEmail.error = "Enter a valid email!"
                    Toast.makeText(this@RegisterActivity, "google", Toast.LENGTH_SHORT).show()
                }

                true
            } else {
                if (!etEmail.text.isValidEmail()) {
                    etEmail.error = "Enter a valid email!"
                }
                if (etPassword.text.isEmpty()) {
                    etEmail.error = "Enter a your password!"
                }
                if (etName.text.toString().isEmpty()) {
                    etName.error = "Enter a valid name!"
                }
                if (etDOB.text.toString().isEmpty()) {
                    etDOB.error = "Enter a valid Date of birth!"
                }
                if (etPassword.text.toString().isEmpty()) {
                    etPassword.error = "Enter a password!"
                }

                if(resultUri ==null){
                    Toast.makeText(this@RegisterActivity, "Select a profile picture!", Toast.LENGTH_SHORT).show()
                }
                if(rgGender.checkedRadioButtonId>0){
                    Toast.makeText(this@RegisterActivity, "Select the gender!", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }

}
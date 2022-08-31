package app.vyshnav.machinetest.UI.Profile

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import app.vyshnav.machinetest.Database.db
import app.vyshnav.machinetest.R
import app.vyshnav.machinetest.UI.EditProfile.EditProfileActivity
import app.vyshnav.machinetest.UI.Login.MainActivity
import app.vyshnav.machinetest.databinding.ActivityProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.Exception

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val db = Room.databaseBuilder(
            applicationContext,
            db::class.java, "profile_db"
        ).build()

        binding.apply {
            btnEdit.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
                finish()
            }
            imgMenu.setOnClickListener {
                val builder =   AlertDialog.Builder(this@ProfileActivity)
                builder.setTitle("Alert!")
                    .setMessage("Do you want to logout?")
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setPositiveButton("Yes")  { dialogInterface, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.ProfileDAO().getProfile()
                                ?.let { it1 -> db.ProfileDAO().deleteProfile(it1) }
                            lifecycleScope.launch (Dispatchers.IO){
                                startActivity(Intent(this@ProfileActivity,MainActivity::class.java))
                                finish()
                            }

                        }
                        dialogInterface.dismiss()
                    }
                    .create().show()
            }
                lifecycleScope.launch(Dispatchers.IO) {
                    var profile = db.ProfileDAO().getProfile()
                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            val image = File(profile?.imageLocation.toString())

                            Glide.with(this@ProfileActivity).load(image).into(imgProfile)

                            Glide.with(this@ProfileActivity)
                                .load(image)
                                .apply(bitmapTransform(BlurTransformation(25, 3)))
                                .into(imgBg)

                            tvUsername.text = profile?.name
                            tvEmail.text = "Email : ${profile?.email}"
                            tvDOB.text = "DOB : ${profile?.dob}"
                            tvGender.text = "Gender : ${profile?.gender}"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }


            }
        }

}

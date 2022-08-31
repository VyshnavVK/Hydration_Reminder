package app.vyshnav.hydrationreminder.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import app.vyshnav.hydrationreminder.R
import app.vyshnav.hydrationreminder.databinding.ActivityMainBinding
import app.vyshnav.hydrationreminder.home.HomeActivity
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Glide.with(this).load(R.drawable.drinking_water).into(binding.imageView)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        },3000)

    }
}
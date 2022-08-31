package app.vyshnav.hydrationreminder.drink_water

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.vyshnav.hydrationreminder.databinding.ActivityDrinkWaterBinding

class DrinkWaterActivity : AppCompatActivity() {
    lateinit var binding : ActivityDrinkWaterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkWaterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
package app.goodbits.machine_test.UI.StateActivity


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.goodbits.machine_test.databinding.ActivityMainBinding
import com.tailormade.api.NetWorkCall
import com.tailormade.data.MainViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        viewModel.stateWise(this).observe(this, Observer {
            when(it){
                is NetWorkCall.Success ->{
                    Log.d("Success",it.response.toString())
                }
                is NetWorkCall.Error ->{
                    Log.d("Error",it.toString())
                }
                else -> {
                    Log.d("Error",it.toString())
                }
            }
        })
    }
}
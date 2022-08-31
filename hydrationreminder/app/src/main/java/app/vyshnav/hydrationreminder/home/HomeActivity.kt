package app.vyshnav.hydrationreminder.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import app.vyshnav.hydrationreminder.R
import app.vyshnav.hydrationreminder.Receiver.AlarmReceiver
import app.vyshnav.hydrationreminder.databinding.ActivityHomeBinding
import app.vyshnav.hydrationreminder.home.Adapter.IntervalsAdapter
import app.vyshnav.hydrationreminder.home.Adapter.NotificationAdapter
import app.vyshnav.hydrationreminder.home.Model.intrevalsModel
import app.vyshnav.hydrationreminder.home.Model.notificationModel
import com.app.myapplication.util.sp

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var mp : MediaPlayer
    var spinnerCheck = 0

    fun getIntervals() : List<intrevalsModel>{
        val list : MutableList<intrevalsModel> = arrayListOf()
        list.add(intrevalsModel("1 hour",60))
        list.add(intrevalsModel("3 hour",3*60))
        list.add(intrevalsModel("6 hour",6*60))
        list.add(intrevalsModel("8 hour",8*60))
        return list
    }
    fun getNotification() : List<notificationModel>{
        val list : MutableList<notificationModel> = arrayListOf()
        list.add(notificationModel("It's time to drink some water!", R.raw.it_s_time_to_drink_some_water))
        list.add(notificationModel("It's time to get hydrated!", R.raw.it_s_time_to_get_hydrated))
        list.add(notificationModel("Time to drink some water my love!", R.raw.time_to_drink_some_water_my_love))
        list.add(notificationModel("Yo! Babe drink some water!", R.raw.yo_babe_drink_some_water))
        return list
    }

    fun playMedia(uri:Uri){
        try {
            mp.reset()
            mp.setDataSource(this@HomeActivity, uri)
            mp.prepare()
            mp.setOnPreparedListener {
                if (!it.isPlaying)
                    it.start()
            }
            mp.setOnCompletionListener {
                it.pause()
                it.stop()
            }
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    fun setAdapters(){
       mp = MediaPlayer()
        binding.apply {
            sp1.adapter = IntervalsAdapter(this@HomeActivity,getIntervals())
            sp2.adapter = NotificationAdapter(this@HomeActivity,getNotification())


            sp2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(++spinnerCheck>1)
                    playMedia(Uri.parse("android.resource://" + this@HomeActivity.packageName + "/" + getNotification()[position].file))
                }

            }


            btnStart.setOnClickListener {
                confirmAlert()
            }
        }



    }

    fun confirmAlert(){
        binding.apply {
          val builder =   AlertDialog.Builder(this@HomeActivity)
            builder.setTitle("Warning!")
                .setMessage("Do you want to set an reminder on every ${getIntervals()[sp1.selectedItemPosition].hoursText} with your selected notification format?")
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("Yes")  { dialogInterface, _ ->
                    sp.with(this@HomeActivity)
                        .putInt(sp.INTERVALS_VALUE_KEY,getIntervals()[sp1.selectedItemPosition].value)
                        .putString(sp.NOTIFICATION_KEY,getNotification()[sp2.selectedItemPosition].text)
                        .putInt(sp.NOTIFICATION_VALUE_KEY,getNotification()[sp2.selectedItemPosition].file)
                        .putBoolean(sp.SOUND_KEY,sw1.isChecked)
                    setAlarm()
                    dialogInterface.dismiss()
                }
                .create().show()
        }

    }

    fun setAlarm(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium AlarmManager Demo")

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val ALARM_DELAY_IN_SECOND = 10
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarmTimeAtUTC,
                (60*sp.with(this).getInt(sp.INTERVALS_VALUE_KEY,0)*1000).toLong(),pendingIntent)
        }
    }

    @SuppressLint("ShortAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapters()
    }

    override fun onPause() {
        super.onPause()
        mp.let {
            it.pause()
            it.stop()
            it.release()
        }


    }


}
package com.example.metropickerwithmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.metropickerwithmenu.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity() {

    private var mSelectedStation: TextView? = null
    private var mBL: TextView? = null
    private val mStorage: Storage
        get() = Storage(this)
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, ACTION_MAIN, Toast.LENGTH_SHORT).show()

        // 3 dots for menu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // station
        mSelectedStation = findViewById(R.id.selectedStation)

        // battery
        mBL = findViewById(R.id.textBattery)
    }

    override fun onStart() {
        super.onStart()

        // station
        mSelectedStation!!.text = mStorage.getStation()

        // battery
        mBL!!.text = getBatteryPercentage().toString()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun onClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(ACTION_PICK_STATION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CODE_SELECT_STATION)
        } else {
            Toast.makeText(this, R.string.no_activity_msg, Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_SELECT_STATION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mSelectedStation!!.text = data
                        .getStringExtra(ListViewActivity.EXTRA_SELECTED_STATION)
                }
            } else {
                mSelectedStation!!.setText(
                    R.string.no_station_selected_msg
                )
            }
        }
    }

// 1-ый - мы использовали способ сохранения данных при каждом клике пользователя и в onCreate()
// 2-ой - сохранять данные при получении (resultCode) из 2ой активности в onActivityResult
// 3-ий - сохранять данные при запуске onStop() т.е. в момент закрытии приложения

    @SuppressLint("ObsoleteSdkInt")
    private fun getBatteryPercentage(): Int {
        return when {
            Build.VERSION.SDK_INT >= 21 -> {
                val bm = this.getSystemService(BATTERY_SERVICE) as BatteryManager
                bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            }
            else -> {
                val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val batteryStatus: Intent? = this.registerReceiver(null, iFilter)
                val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                val batteryPct = level!! / scale!!.toDouble()
                (batteryPct * 100).toInt()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_clear -> {
                mStorage.setStation(null)                        // clean selected item in storage
                mSelectedStation!!.text = mStorage.getStation()  // set text
                true
            }
            R.id.item_exit  -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val CODE_SELECT_STATION = 1
        const val ACTION_PICK_STATION = "PICK_METRO_STATION"
    }
}

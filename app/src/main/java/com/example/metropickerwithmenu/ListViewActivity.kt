@file:Suppress("DEPRECATION")

package com.example.metropickerwithmenu

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.metropickerwithmenu.MainActivity.Companion.ACTION_PICK_STATION


@Suppress("DEPRECATION")
class ListViewActivity : ListActivity(), OnItemClickListener {
    private val mStorage: Storage
        get() = Storage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mStations = resources.getStringArray(R.array.stations)
        val aa = ArrayAdapter(this, R.layout.list_item, mStations)
        listView.adapter = aa
        listView.onItemClickListener = this

        registerForContextMenu(listView)

        Toast.makeText(this, ACTION_PICK_STATION, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedStation = (view as TextView).text
        sendStation(selectedStation)
        /** позволяет закрыть активность и прейти обратно на главный экран MainActivity **/
    }

    private fun sendStation(selectedStation: CharSequence?) {
        val result = Intent()
        result.putExtra(EXTRA_SELECTED_STATION, selectedStation)
        setResult(RESULT_OK, result)

        mStorage.setStation(selectedStation as String)

        finish()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_station_pick, menu)

        // for presentation which station selected
        val info: AdapterView.AdapterContextMenuInfo =
            menuInfo as AdapterView.AdapterContextMenuInfo
        val selectedStation = getStationFromMenuInfo(info)

        menu?.setHeaderTitle(selectedStation)
//        val selectedStation = (v as TextView).onFocusChangeListener
//        menu?.setHeaderTitle("Контекстное меню")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_send -> {
                val info: AdapterView.AdapterContextMenuInfo =
                    item.menuInfo as AdapterView.AdapterContextMenuInfo
                val selectedStation = getStationFromMenuInfo(info)
                sendStation(selectedStation)
                true
            }
            R.id.item_exit -> {
                finish()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun getStationFromMenuInfo(info: AdapterView.AdapterContextMenuInfo): CharSequence? {
        val textView = info.targetView as TextView
        return textView.text
    }

    companion object {
        const val EXTRA_SELECTED_STATION = "SELECTED_STATION"
    }
}
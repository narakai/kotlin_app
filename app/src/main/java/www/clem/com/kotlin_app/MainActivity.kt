package www.clem.com.kotlin_app

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "MainActivityLog"

        private fun getCurrentTimeStamp(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val now = Date()
            return simpleDateFormat.format(now)
        }
    }

    // 1
    private val taskList: MutableList<String> = mutableListOf()
    private val adapter by lazy { makeAdapter(taskList) }
    private val tickReceiver by lazy { makeBroadcastReceiver() }
    private val ADD_TASK_REQUEST = 1
    private val PREFS_TASKS = "prefs_tasks"
    private val KEY_TASKS_LIST = "tasks_list"

    private fun makeBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent?.action == Intent.ACTION_TIME_TICK) {
                    dateTimeTextView.text = getCurrentTimeStamp()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2
        super.onCreate(savedInstanceState)
        // 3
        setContentView(R.layout.activity_main)

        // 4
        taskListView.adapter = adapter

        // 5
        taskListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            taskSelected(position)
        }

        val savedList = getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).getString(KEY_TASKS_LIST, null)
        if (savedList != null) {
            val items = savedList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            taskList.addAll(items)
        }
    }

    override fun onResume() {
        // 1
        super.onResume()
        // 2
        dateTimeTextView.text = getCurrentTimeStamp()
        // 3
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onPause() {
        // 4
        super.onPause()
        // 5
        try {
            unregisterReceiver(tickReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e(MainActivity.LOG_TAG, "Time tick Receiver not registered", e)
        }
    }

    override fun onStop() {
        super.onStop()

        // Save all data which you want to persist.
        val savedList = StringBuilder()
        for (task in taskList) {
            savedList.append(task)
            savedList.append(",")
        }

        getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).edit()
                .putString(KEY_TASKS_LIST, savedList.toString()).apply()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    // 6
    fun addTaskClicked(view: View) {
        val intent = Intent(this, TaskDescriptionActivity::class.java)
        startActivityForResult(intent, ADD_TASK_REQUEST)
    }

    // 7
    private fun makeAdapter(list: List<String>): ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

    private fun taskSelected(position: Int) {
        // 1
        AlertDialog.Builder(this)
                // 2
                .setTitle(R.string.alert_title)
                // 3
                .setMessage(taskList[position])
                .setPositiveButton(R.string.delete, { _, _ ->
                    taskList.removeAt(position)
                    adapter.notifyDataSetChanged()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ ->
                    dialog.cancel()
                })
                // 4
                .create()
                // 5
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 1
        if (requestCode == ADD_TASK_REQUEST) {
            // 2
            if (resultCode == Activity.RESULT_OK) {
                // 3
                val task = data?.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION)
                task?.let {
                    taskList.add(task)
                    // 4
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}

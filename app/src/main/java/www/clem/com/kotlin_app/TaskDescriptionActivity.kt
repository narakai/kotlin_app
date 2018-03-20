package www.clem.com.kotlin_app

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_task_description.*

class TaskDescriptionActivity : AppCompatActivity() {

    // 1 相当于java的static
    companion object {
        val EXTRA_TASK_DESCRIPTION = "task"
    }

    // 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_description)
    }

    // 3
    fun doneClicked(view: View) {
        // 1
        val taskDescription = descriptionText.text.toString()

        if (!taskDescription.isEmpty()) {
            // 2
            val result = Intent()
            result.putExtra(EXTRA_TASK_DESCRIPTION, taskDescription)
            setResult(Activity.RESULT_OK, result)
        } else {
            // 3
            setResult(Activity.RESULT_CANCELED)
        }
        // 4
        finish()
    }
}

package com.example.to_doapp

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class EditTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        var back = findViewById<ImageView>(R.id.eBackIcon)
        back.setOnClickListener {
            finish()
        }
        //bringing db
        val db = Firebase.firestore
        db.collection("Tasks")
            .get()

        var task = intent.getSerializableExtra("task") as com.example.to_doapp.model.Task

        var editTitleEditText = findViewById<EditText>(R.id.editTaskTitle)
        editTitleEditText.text = Editable.Factory.getInstance().newEditable(task.title)

        var editDateEditText = findViewById<EditText>(R.id.editTaskDate)
        editDateEditText.text = Editable.Factory.getInstance().newEditable(task.dueDate.toString())

        var editNotesEditText = findViewById<EditText>(R.id.editTaskNotes)
        editNotesEditText.text = Editable.Factory.getInstance().newEditable(task.notes)

        editDateEditText.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                var calendar = Calendar.getInstance()
                var year = calendar.get(Calendar.YEAR)
                var month = calendar.get(Calendar.MONTH)
                var day = calendar.get(Calendar.DAY_OF_MONTH)
                var datePicker = DatePickerDialog(
                    this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        editDateEditText.setText("$dayOfMonth/${month + 1}/$year")
                    },
                    year,
                    month,
                    day
                )
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis())
                datePicker.show()
            }
            }

        var editBtn = findViewById<Button>(R.id.buttonEditTask)
        editBtn.setOnClickListener {
            if (editTitleEditText.toString().isEmpty() || editDateEditText.toString()
                    .isEmpty() || editNotesEditText.toString().isEmpty()
            ) {
                Toast.makeText(this, "fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                db.collection("Tasks").document(task.id!!)
                    .update(
                        mapOf(
                            "taskName" to editTitleEditText.text.toString()!!,
                            "taskNote" to editNotesEditText.text.toString()!!,
                            "dueDate" to editDateEditText?.text.toString()!!,
                            "CreationDate" to task.creationDate
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.update_message), Toast.LENGTH_LONG)
                            .show()
                        var intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
                    }
            }

        }

    }
}

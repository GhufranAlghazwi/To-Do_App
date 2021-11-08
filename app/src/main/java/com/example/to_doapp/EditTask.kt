package com.example.to_doapp

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

class EditTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        var back = findViewById<ImageView>(R.id.eBackIcon)
        back.setOnClickListener {
            finish()
        }

        var task = intent.getSerializableExtra("task") as com.example.to_doapp.model.Task

        var editTitleEditText = findViewById<EditText>(R.id.editTaskTitle)
        editTitleEditText.text = Editable.Factory.getInstance().newEditable(task.title)

        var editDateEditText = findViewById<EditText>(R.id.editTaskDate)
        editDateEditText.text = Editable.Factory.getInstance().newEditable(task.dueDate)

        var editNotesEditText = findViewById<EditText>(R.id.editTaskNotes)
        editNotesEditText.text = Editable.Factory.getInstance().newEditable(task.notes)

        editDateEditText.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                var constraintsBuilder = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select task due date")
                    // Opens the date picker with today's date selected.
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
                datePicker.show(supportFragmentManager, "date_picker")
                datePicker.addOnPositiveButtonClickListener {
                    var selectedDate = datePicker.headerText
                    editDateEditText.setText(selectedDate)
                }

                val db = Firebase.firestore
                db.collection("Tasks")
                    .get()

                var editBtn = findViewById<Button>(R.id.buttonEditTask)
                editBtn.setOnClickListener {
                    db.collection("Tasks").document(task.id!!)
                        .update(
                            mapOf(
                                "taskName" to editTitleEditText.text.toString()!!,
                                "taskNote" to editNotesEditText.text.toString()!!,
                                "dueDate" to editDateEditText?.text.toString()!!,
                                "CreationDate" to "SSS"
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_LONG)
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
}
package com.example.to_doapp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Toolbar part
        var mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mToolbar)
        mToolbar.title = getString(R.string.app_name)
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener {
            finish()
        }

        //Tasks List
        var taskList = mutableListOf<Task>()

        //Link RV to its adapter
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        //initialize db
        val db = Firebase.firestore

        //Create a collection
        db.collection("Tasks")
            .get().addOnSuccessListener { result: QuerySnapshot ->
                for (document in result) {
                    taskList.add(
                        Task(
                            document.getString("taskName")!!, document.getString("taskNote")!!,
                            document.getString("dueDate")!!, document.getString("CreationDate")!!
                        )
                    )
                }
                mRecyclerView.adapter = TaskAdapter(taskList)
                mRecyclerView.adapter?.notifyDataSetChanged()
            }


        //Floating Btn to add task
        var fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        fabAdd.setOnClickListener {
            var addTaskDialogView = layoutInflater.inflate(R.layout.add_task_dialog, null)
            var addTaskCustomDialog = AlertDialog.Builder(this)
                .setView(addTaskDialogView)
                .show()

            //picking a date
            var date = addTaskDialogView.findViewById<EditText>(R.id.inputDueDate)
            date.setOnFocusChangeListener { view, b ->
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
                        date.setText(selectedDate)
                    }

                    var dialogAddButton = addTaskDialogView.findViewById<Button>(R.id.buttonAdd)
                    //Add btn in the dialog
                    dialogAddButton.setOnClickListener {
                        var taskTitle =
                            addTaskCustomDialog.findViewById<EditText>(R.id.inputTaskTitle)?.text.toString()
                        var taskNotes =
                            addTaskCustomDialog.findViewById<EditText>(R.id.inputTaskNotes)?.text.toString()
                        var dueDate =
                            addTaskCustomDialog.findViewById<EditText>(R.id.inputDueDate)?.text.toString()

                        if (taskTitle.isEmpty() || taskNotes.isEmpty() || dueDate.isEmpty()) {
                            addTaskCustomDialog.dismiss()
                            Toast.makeText(this, "fill all the fields", Toast.LENGTH_LONG).show()
                        }
                        else {
                            val task = hashMapOf(
                                "taskName" to taskTitle,
                                "taskNote" to taskNotes,
                                "dueDate" to dueDate,
                                "CreationDate" to "SSS"
                            )
                            db.collection("Tasks")
                                .add(task)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "task has been added", Toast.LENGTH_SHORT)
                                        .show()
                                    mRecyclerView.adapter?.notifyDataSetChanged()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "something went wrong! ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        addTaskCustomDialog.dismiss()
                        //onResume()
                    }


                }

            }

//            date.setOnClickListener {
//                //Toast.makeText(this, "Date edit text clicked!", Toast.LENGTH_SHORT).show()
//                // Makes only dates from today forward selectable.
//                var constraintsBuilder = CalendarConstraints.Builder()
//                    .setValidator(DateValidatorPointForward.now())
//                val datePicker = MaterialDatePicker.Builder.datePicker()
//                    .setTitleText("Select task due date")
//                    // Opens the date picker with today's date selected.
//                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                    .setCalendarConstraints(constraintsBuilder.build())
//                    .build()
//                datePicker.show(supportFragmentManager, "date_picker")
//                var selected = datePicker.selection.toString() as Editable
//            }
        }


    }

    override fun onResume() {
        super.onResume()
        //Tasks List
        var taskList = mutableListOf<Task>()

        //Link RV to its adapter
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mRecyclerView.adapter = TaskAdapter(taskList)

        //initialize db
        val db = Firebase.firestore

        //Create a collection
        db.collection("Tasks")
            .get().addOnSuccessListener { result: QuerySnapshot ->
                for (document in result) {
                    taskList.add(
                        Task(
                            document.getString("taskName")!!, document.getString("taskNote")!!,
                            document.getString("dueDate")!!, document.getString("CreationDate")!!
                        )
                    )
                }
                mRecyclerView.adapter = TaskAdapter(taskList)
                mRecyclerView.adapter?.notifyDataSetChanged()
            }
    }

    //menu options\btns
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_item -> {
                Toast.makeText(this, "Filter item clicked", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
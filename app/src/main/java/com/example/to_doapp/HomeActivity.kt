package com.example.to_doapp

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.model.Task
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class HomeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

        //current date
//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
//        val currentDate = current.format(formatter).toString()

        val current = LocalDate.now()
        var day = "${current.dayOfMonth}/${current.month.value}/${current.year}"
//        val sdf = SimpleDateFormat("dd/MM/yyyy")
//        var currentFormatted = sdf.parse(day)

        var homeVM = HomeViewModel()

        //identify RV layout
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        homeVM.getAllTasks().observe(this, { list ->
            mRecyclerView.adapter = TaskAdapter(list)
            //mRecyclerView.adapter?.notifyDataSetChanged()

        })

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
                    var calendar = Calendar.getInstance()
                    var year = calendar.get(Calendar.YEAR)
                    var month = calendar.get(Calendar.MONTH)
                    var day = calendar.get(Calendar.DAY_OF_MONTH)
                    val datePicker = DatePickerDialog(
                        this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            date.setText("$dayOfMonth/${month + 1}/$year")
                        },
                        year,
                        month,
                        day
                    )
                    datePicker.show()
                }
                        // Opens the date picker with today's date selected.
//                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                        .setCalendarConstraints(constraintsBuilder.build())
//                        .build()
//                    datePicker.show(supportFragmentManager, "date_picker")
//                    datePicker.addOnPositiveButtonClickListener {
//                        var selectedDate = datePicker.headerText
//
//                    }
            }

            var dialogAddButton = addTaskDialogView.findViewById<Button>(R.id.buttonAdd)
            //Add btn in the dialog
            dialogAddButton.setOnClickListener {
                var db = Firebase.firestore
                var taskTitle =
                    addTaskCustomDialog.findViewById<EditText>(R.id.inputTaskTitle)?.text.toString()
                var taskNotes =
                    addTaskCustomDialog.findViewById<EditText>(R.id.inputTaskNotes)?.text.toString()
                var dueDate =
                    addTaskCustomDialog.findViewById<EditText>(R.id.inputDueDate)?.text.toString()

                if (taskTitle.isEmpty() || taskNotes.isEmpty() || dueDate.isEmpty()) {
                    Toast.makeText(this, "fill all the fields", Toast.LENGTH_LONG).show()
                } else {
                    val task = hashMapOf(
                        "taskName" to taskTitle,
                        "taskNote" to taskNotes,
                        "dueDate" to dueDate,
                        "CreationDate" to day,
                        "status" to false
                    )
                    db.collection("Tasks")
                        .add(task)
                        .addOnSuccessListener {
                            Toast.makeText(this, "task has been added", Toast.LENGTH_SHORT)
                                .show()
                            addTaskCustomDialog.dismiss()
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


            }

        }


    }


    //menu options\btns
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Sort and Filter in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var homeVM = HomeViewModel()

        //identify RV layout
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        when (item.itemId) {

            R.id.filter_item -> {
                homeVM.filterUncompleted().observe(this, { list ->
                    mRecyclerView.adapter = TaskAdapter(list)
                })

                Toast.makeText(this, "Task filtered", Toast.LENGTH_SHORT).show()
            }
            R.id.sort_item -> {
                homeVM.sortTask().observe(this, { list ->
                    mRecyclerView.adapter = TaskAdapter(list)
                })

                Toast.makeText(this, "Task are sorted now.", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
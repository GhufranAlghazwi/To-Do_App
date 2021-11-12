package com.example.to_doapp

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
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


        val current = LocalDate.now()
        var day = "${current.dayOfMonth}/${current.month.value}/${current.year}"

        var homeVM = HomeViewModel()

        //identify RV layout
        var mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        homeVM.getUncomplete().observe(this, { list ->
            mRecyclerView.adapter = TaskAdapter(list)

        })

        // TEST
        var mRecyclerViewCompleted = findViewById<RecyclerView>(R.id.mRecyclerView2)
        mRecyclerViewCompleted.layoutManager = LinearLayoutManager(this)
        homeVM.getComplete().observe(this, { list ->
            mRecyclerViewCompleted.adapter = TaskAdapter(list)

        })


        mRecyclerView.adapter?.notifyDataSetChanged()

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
                    var calendar = Calendar.getInstance()
                    var year = calendar.get(Calendar.YEAR)
                    var month = calendar.get(Calendar.MONTH)
                    var day = calendar.get(Calendar.DAY_OF_MONTH)
                    var datePicker = DatePickerDialog(
                        this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            date.setText("$dayOfMonth/${month + 1}/$year")
                        },
                        year,
                        month,
                        day
                    )
                    datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis())
                    datePicker.show()

                }

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

        var mRecyclerView2 = findViewById<RecyclerView>(R.id.mRecyclerView2)
        mRecyclerView2.layoutManager = LinearLayoutManager(this)

        mRecyclerView.adapter?.notifyDataSetChanged()

        when (item.itemId) {

            R.id.filter_item -> {
                homeVM.getUncomplete().observe(this, { list ->
                    mRecyclerView.adapter = TaskAdapter(list)
                    mRecyclerView.adapter?.notifyDataSetChanged()
                })
                Toast.makeText(this, getString(R.string.task_filtered), Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.sort_item -> {
                homeVM.sortUnompleteTask().observe(this, { list ->
                    mRecyclerView.adapter = TaskAdapter(list)

                })
                homeVM.sortCompleteTask().observe(this, { list ->
                    mRecyclerView2.adapter = TaskAdapter(list)

                })
                //Toast.makeText(this, getString(R.string.sorted_message), Toast.LENGTH_SHORT).show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

}
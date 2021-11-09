package com.example.to_doapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import com.example.to_doapp.model.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TaskDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        var backIcon = findViewById<ImageView>(R.id.backNavigation)
        backIcon.setOnClickListener {
            finish()
        }

        var task = intent.getSerializableExtra("task") as Task

        var titleCardView = findViewById<CardView>(R.id.cardViewTitle)
        titleCardView.setBackgroundResource(R.drawable.details_card_bg)

        var title = findViewById<TextView>(R.id.txtCardTaskTitle)
        var date = findViewById<TextView>(R.id.textViewDetailsDate)
//        var status = findViewById<TextView>(R.id.textViewStatus)
        var notes = findViewById<TextView>(R.id.textViewDetailsNotes)

        title.text = task.title
        date.text = task.dueDate
        notes.text = task.notes

//        if (task.status)
//            status.text="Completed"
//        else
//            status.text="Uncompleted"

        //bring db
        val db = Firebase.firestore
//        db.collection("Tasks")
//            .get()

        var deleteBtn = findViewById<Button>(R.id.buttonDelete)
        deleteBtn.setOnClickListener {
            var deleteConfirmDialog = AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Do you want to delete this task?")
                .setPositiveButton("Yes"){dialog, which ->
                    db.collection("Tasks").document(task.id!!)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e->
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    finish()
                }
                .setNegativeButton("No"){dialog, which ->
                    dialog.dismiss()
                }
                .setIcon(R.drawable.ic_baseline_delete_24)
                .show()
        }

        var editBtn = findViewById<Button>(R.id.buttonEdit)
        editBtn.setOnClickListener {
            var intent = Intent(this,EditTask::class.java)
            intent.putExtra("task",task)
            startActivity(intent)
        }
    }
}
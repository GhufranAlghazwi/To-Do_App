package com.example.to_doapp

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.to_doapp.model.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel() : ViewModel() {
    //initialize db
    val db = Firebase.firestore

    fun getAllTasks(): MutableLiveData<MutableList<Task>> {

        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        //initialize db
//        val db = Firebase.firestore

        //Create a collection
        db.collection("Tasks")
            .get().addOnSuccessListener { result: QuerySnapshot ->
                var taskList = mutableListOf<Task>()
                for (document in result) {
                    taskList.add(
                        Task(
                            document.id,
                            document.getString("taskName")!!,
                            document.getString("taskNote")!!,
                            document.getString("dueDate")!!,
                            document.getString("CreationDate")!!,
                            document.getBoolean("status")!!
                        )
                    )
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

    fun sortTask():MutableLiveData<MutableList<Task>>{
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        //Create a collection
        db.collection("Tasks").orderBy("taskName",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                var taskList = mutableListOf<Task>()
                for (document in result) {
                    taskList.add(
                        Task(
                            document.id,
                            document.getString("taskName")!!,
                            document.getString("taskNote")!!,
                            document.getString("dueDate")!!,
                            document.getString("CreationDate")!!,
                            document.getBoolean("status")!!
                        )
                    )
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }
}
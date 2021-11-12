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

        //Create a collection
        db.collection("Tasks")
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
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
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

    fun sortCompleteTask(): MutableLiveData<MutableList<Task>> {
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        //Create a collection
        db.collection("Tasks")
            .orderBy("taskName", Query.Direction.ASCENDING)
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
                    for (document in result) {
                        var task= Task(
                                document.id,
                                document.getString("taskName")!!,
                                document.getString("taskNote")!!,
                                document.getString("dueDate")!!,
                                document.getString("CreationDate")!!,
                                document.getBoolean("status")!!
                            )
                        if (task.status)
                            taskList.add(task)
                    }
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

    fun sortUnompleteTask(): MutableLiveData<MutableList<Task>> {
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        db.collection("Tasks")
            .orderBy("taskName", Query.Direction.ASCENDING)
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
                    for (document in result) {
                        var task= Task(
                            document.id,
                            document.getString("taskName")!!,
                            document.getString("taskNote")!!,
                            document.getString("dueDate")!!,
                            document.getString("CreationDate")!!,
                            document.getBoolean("status")!!
                        )
                        if (!task.status)
                            taskList.add(task)
                    }
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }


    fun sortTaskDesc(): MutableLiveData<MutableList<Task>> {
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        db.collection("Tasks").orderBy("taskName", Query.Direction.DESCENDING)
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
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
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

    fun getUncomplete(): MutableLiveData<MutableList<Task>> {
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        //Create a collection
        db.collection("Tasks").whereEqualTo("status", false)
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
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
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

    fun getComplete(): MutableLiveData<MutableList<Task>> {
        var mutableLiveData = MutableLiveData<MutableList<Task>>()

        //Create a collection
        db.collection("Tasks").whereEqualTo("status", true)
            .addSnapshotListener() { result, error ->
                var taskList = mutableListOf<Task>()
                if (result != null) {
                    taskList.clear()
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
                }
                mutableLiveData.postValue(taskList)
            }
        return mutableLiveData
    }

}
package com.example.to_doapp.model

import java.io.Serializable
import java.util.*

data class Task(var id:String?= null,var title:String, var notes:String, var dueDate: String, var creationDate: String, var status:Boolean):Serializable {
}
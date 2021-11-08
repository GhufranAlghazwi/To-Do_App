package com.example.to_doapp

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.model.Task

class TaskAdapter(var data: List<Task>):RecyclerView.Adapter<TaskHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
       var v = LayoutInflater.from(parent.context).inflate(R.layout.list_row_task,parent,false)
        return TaskHolder(v)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.tvTitle.text = data[position].title.capitalize()
        holder.tvNotes.text = data[position].notes
        holder.tvDueDate.text = data[position].dueDate
        holder.tvCreationDate.text = "Created:\n" + data[position].creationDate.toString()


        holder.doneBtn.setOnClickListener{
            if (holder.doneBtn.isChecked)
                holder.tvTitle.paintFlags = holder.tvTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView.context, TaskDetails::class.java)
            var task = data[position]
            intent.putExtra("task",data[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class TaskHolder(v: View): RecyclerView.ViewHolder(v){
    var tvTitle = v.findViewById<TextView>(R.id.textViewTaskTitle)
    var tvNotes = v.findViewById<TextView>(R.id.textViewNotes)
    var tvDueDate = v.findViewById<TextView>(R.id.textViewDate)
    var tvCreationDate = v.findViewById<TextView>(R.id.textViewCreationDate)
    var doneBtn = v.findViewById<RadioButton>(R.id.doneRadioBtn)
}
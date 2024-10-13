package com.example.taskmasterapp.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmasterapp.R
import com.example.taskmasterapp.models.Task

class TaskAdapter(private val context: Context,val listener: TaskClickListener):
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    private val taskList = ArrayList<Task>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
        val item = taskList[position]
        holder.name.text = item.name
        holder.name.isSelected = true
        holder.description.text = item.description
        holder.priority.text = item.priority
        holder.status.text = item.status
        holder.deadlineDate.text = item.deadlineDate
        holder.deadlineDate.isSelected = true
        holder.deadlineTime.text = item.deadlineTime
        holder.deadlineTime.isSelected = true
        holder.todo_layout.setOnClickListener {
            listener.onItemClicked(taskList[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateList(newList: List<Task>){
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val todo_layout = itemView.findViewById<CardView>(R.id.card_layout)
        val name = itemView.findViewById<TextView>(R.id.tv_name)
        val description = itemView.findViewById<TextView>(R.id.tv_description)
        val priority = itemView.findViewById<TextView>(R.id.tv_priority)
        val status = itemView.findViewById<TextView>(R.id.tv_status)
        val deadlineDate = itemView.findViewById<TextView>(R.id.tv_date)
        val deadlineTime = itemView.findViewById<TextView>(R.id.tv_time)
    }

    interface TaskClickListener {
        fun onItemClicked(todo: Task)
    }
}
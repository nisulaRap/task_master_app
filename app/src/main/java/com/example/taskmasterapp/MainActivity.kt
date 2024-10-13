package com.example.taskmasterapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmasterapp.adaptors.TaskAdapter
import com.example.taskmasterapp.database.TaskDatabase
import com.example.taskmasterapp.databinding.ActivityMainBinding
import com.example.taskmasterapp.models.Task
import com.example.taskmasterapp.models.TaskViewModel

class MainActivity : AppCompatActivity(), TaskAdapter.TaskClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: TaskDatabase
    lateinit var viewModel: TaskViewModel
    lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        setupSearchView()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)

        viewModel.allTask.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }

        database = TaskDatabase.getDatabase(this)
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = TaskAdapter(this, this)
        binding.recyclerView.adapter = adapter

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = result.data?.getSerializableExtra("task") as? Task
                    if (task != null) {
                        viewModel.insertTask(task) // Fixed this line
                    }
                }
            }

        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            getContent.launch(intent)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchTaskList(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchTaskList(it) }
                return false
            }
        })
    }

    private fun searchTaskList(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchTask(searchQuery).observe(this) { list ->
            list?.let { adapter.updateList(it) }
        }
    }

    private val updateOrDeleteTask =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = result.data?.getSerializableExtra("task") as Task
                val isDelete = result.data?.getBooleanExtra("delete_task", false) ?: false
                if (task != null) {
                    if (isDelete) {
                        viewModel.deleteTask(task)
                    } else {
                        viewModel.updateTask(task)
                    }
                }
            }
        }

    override fun onItemClicked(task: Task) {
        val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
        intent.putExtra("current_task", task)
        updateOrDeleteTask.launch(intent)
    }
}

package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var todos: MutableList<String>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.listView)
        val userData: EditText = findViewById(R.id.user_data)
        val button: Button = findViewById(R.id.button)

        // Load saved todos
        todos = loadTodos()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, i, _ ->
            val text = listView.getItemAtPosition(i).toString()
            showEditDialog(text, i)
        }

        button.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text.isNotEmpty()) {
                adapter.insert(text, 0)
                userData.text.clear()
                saveTodos()
            } else {
                Toast.makeText(this, "Введите текст задачи", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTodos(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("ToDoList", Context.MODE_PRIVATE)
        val savedTodos = sharedPreferences.getStringSet("todos", setOf()) ?: setOf()
        return savedTodos.toMutableList()
    }

    private fun saveTodos() {
        val sharedPreferences = getSharedPreferences("ToDoList", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putStringSet("todos", todos.toSet())
            apply()
        }
    }

    private fun showEditDialog(text: String, position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.setText(text)

        dialogBuilder.setTitle("Редактировать дело")
        dialogBuilder.setView(input)
        dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
            val newText = input.text.toString().trim()
            if (newText.isNotEmpty()) {
                todos[position] = newText
                adapter.notifyDataSetChanged()
                saveTodos()
                Toast.makeText(this, "Дело обновлено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
        dialogBuilder.setNeutralButton("Удалить") { dialog, _ ->
            adapter.remove(text)
            saveTodos()
            Toast.makeText(this, "Дело удалено", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }
}

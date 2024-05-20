package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var userData: EditText
    private lateinit var button: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val todos: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        userData = findViewById(R.id.user_data)
        button = findViewById(R.id.button)

        // Load saved todos
        loadTodos()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val text = listView.getItemAtPosition(position).toString()
            showDeleteConfirmationDialog(text)
        }

        button.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text.isNotEmpty()) {
                adapter.insert(text, 0)
                userData.text.clear()
                saveTodos()
            } else {
                Toast.makeText(this, "Введіть текст справи", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(text: String) {
        AlertDialog.Builder(this)
            .setTitle("Видалити справу")
            .setMessage("Ви впевнені, що хочете видалити цю справу?")
            .setPositiveButton("Так") { _, _ ->
                adapter.remove(text)
                saveTodos()
                Toast.makeText(this, "Дело удалено: $text", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Ні", null)
            .show()
    }

    private fun saveTodos() {
        val sharedPreferences = getSharedPreferences("ToDoList", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("todos", todos.toSet())
        editor.apply()
    }

    private fun loadTodos() {
        val sharedPreferences = getSharedPreferences("ToDoList", Context.MODE_PRIVATE)
        val savedTodos = sharedPreferences.getStringSet("todos", emptySet()) ?: emptySet()
        todos.clear()
        todos.addAll(savedTodos)
    }
}

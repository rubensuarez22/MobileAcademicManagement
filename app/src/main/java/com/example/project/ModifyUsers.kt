package com.example.project

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.ActivityModifyUsersBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ModifyUsers : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityModifyUsersBinding
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()
    private val originalUserList = mutableListOf<User>() // Para mantener la lista original sin filtrar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.svAPI.setOnQueryTextListener(this)
        initRecyclerView()
        loadUsersFromFirebase()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(userList)
        binding.recyclerView.adapter = adapter
    }

    private fun loadUsersFromFirebase() {
        db.collection("users") // Asegúrate de que tu colección de usuarios se llama "users"
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                originalUserList.clear()
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val roleString = document.getString("rol") ?: "-1" // Obtén el rol como String
                    val role = try {
                        roleString.toInt()
                    } catch (e: NumberFormatException) {
                        -1 // Maneja el caso en que el String no sea un número válido
                    }
                    val userId = document.id
                    val user = User(name, role.toString(), userId)
                    userList.add(user)
                    originalUserList.add(user) // Guarda la lista original
                }
                adapter.notifyDataSetChanged()
                if (userList.isEmpty()) {
                    binding.noResults.visibility = android.view.View.VISIBLE
                    binding.recyclerView.visibility = android.view.View.GONE
                } else {
                    binding.noResults.visibility = android.view.View.GONE
                    binding.recyclerView.visibility = android.view.View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener usuarios: $exception")
                binding.noResults.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
                binding.noResults.text = "Error al cargar usuarios."
            }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false // No necesitamos realizar ninguna acción especial al enviar la búsqueda
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { query ->
            filterUsersByName(query)
        }
        return true
    }

    private fun filterUsersByName(query: String) {
        val filteredList = originalUserList.filter { user ->
            user.name.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))
        }

        userList.clear()
        userList.addAll(filteredList)
        adapter.notifyDataSetChanged()

        if (userList.isEmpty()) {
            binding.noResults.visibility = android.view.View.VISIBLE
            binding.recyclerView.visibility = android.view.View.GONE
            binding.noResults.text = "No users found matching '$query'."
        } else {
            binding.noResults.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE
        }
    }
}
package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class attendance : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listaAsistencias: MutableList<Asistencia>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Asegúrate que tu XML tiene este RecyclerView con el ID correcto
        recyclerView = findViewById(R.id.rvAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)

        listaAsistencias = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        if (userID == null) {
            Log.e("ATTENDANCE", "Usuario no autenticado")
            return
        }

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Asistencias")
            .child(userID)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAsistencias.clear()

                for (snap in snapshot.children) {
                    val asistencia = snap.getValue(Asistencia::class.java)
                    asistencia?.let { listaAsistencias.add(it) }
                }

                Log.d("ATTENDANCE", "Asistencias cargadas: ${listaAsistencias.size}")

                val adapter = AttendanceAdapter(listaAsistencias)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ATTENDANCE", "Error al cargar datos: ${error.message}")
            }
        })
    }
}

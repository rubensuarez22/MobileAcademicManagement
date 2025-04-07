package com.example.project

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TeacherGrades : AppCompatActivity() {

    private lateinit var rvStudentGrades: RecyclerView
    private lateinit var adapter: StudentGradesAdapter
    private val studentsList = mutableListOf<StudentGrade>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_grades)

        rvStudentGrades = findViewById(R.id.rvStudentGrades)
        rvStudentGrades.layoutManager = LinearLayoutManager(this)

        // Configuramos el adaptador y definimos la acción al asignar la calificación.
        adapter = StudentGradesAdapter(studentsList) { student, gradeText ->
            storeGrade(student, gradeText)
        }
        rvStudentGrades.adapter = adapter

        val subjectId = intent.getStringExtra("subjectId")
        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: materia no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            loadEnrolledStudents(subjectId)
        }
    }

    /**
     * Carga los alumnos inscritos y los muestra en el RecyclerView.
     */
    private fun loadEnrolledStudents(subjectId: String) {
        db.collection("subjects").document(subjectId).get()
            .addOnSuccessListener { document ->
                val enrolledStudents = document.get("enrolledStudents") as? List<String> ?: emptyList()
                if (enrolledStudents.isEmpty()) {
                    Toast.makeText(this, "No hay alumnos inscritos en esta materia", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Consulta la subcolección "grades" para obtener las calificaciones asignadas
                db.collection("subjects")
                    .document(subjectId)
                    .collection("grades")
                    .get()
                    .addOnSuccessListener { gradesSnapshot ->
                        // Construye un mapa de studentId a grade
                        val gradesMap = mutableMapOf<String, String>()
                        for (doc in gradesSnapshot.documents) {
                            // Se espera que en los documentos de grades el campo que identifica al estudiante sea "studentId"
                            val studentId = doc.getString("studentId") ?: "StudentId NOT FOUND"
                            if (studentId.isNotEmpty()) {
                                val grade = doc.getString("grade") ?: "grade NOT FOUND"
                                gradesMap[studentId] = grade
                            }
                        }
                        // Ahora consulta la información de cada alumno (desde la colección "users")
                        val tasks = enrolledStudents.map { userId ->
                            db.collection("users").document(userId).get()
                        }
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                            .addOnSuccessListener { snapshots ->
                                studentsList.clear()
                                for (snapshot in snapshots) {
                                    val name = snapshot.getString("name") ?: "Desconocido"
                                    val studentId = snapshot.id
                                    // Si existe calificación asignada, se asigna; si no, queda vacía
                                    val grade = gradesMap[studentId] ?: "Current grade not defined"
                                    val studentGrade = StudentGrade(name, studentId, grade)
                                    studentsList.add(studentGrade)
                                }
                                adapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error cargando alumnos", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al obtener calificaciones", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener la materia", Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Guarda la calificación asignada en Firestore, por ejemplo en la subcolección "grades" de la materia.
     */
    private fun storeGrade(student: StudentGrade, gradeText: String) {
        val subjectId = intent.getStringExtra("subjectId") ?: return

        val data = mapOf(
            "studentName" to student.name,
            "studentId" to student.id,
            "grade" to gradeText
        )

        db.collection("subjects")
            .document(subjectId)
            .collection("grades")
            .document(student.id)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Calificación asignada a ${student.name}", Toast.LENGTH_SHORT)
                    .show()
                // Actualiza el objeto en la lista local:
                val index = studentsList.indexOfFirst { it.id == student.id }
                if (index != -1) {
                    // Actualizamos el StudentGrade usando copy() (esto asume que StudentGrade es una data class)
                    studentsList[index] = student.copy(grade = gradeText)
                    adapter.notifyItemChanged(index)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al asignar calificación", Toast.LENGTH_SHORT).show()
            }
    }
}


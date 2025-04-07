package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceMain  : BottomSheetDialogFragment() {

    private lateinit var rvAttendance: androidx.recyclerview.widget.RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceList = mutableListOf<DataAttendance>()
    private val db = FirebaseFirestore.getInstance()
    private var subjectId: String? = null
    private var fechaSeleccionada: String? = null // 👈 nueva variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperamos el subjectId pasado en los argumentos
        subjectId = arguments?.getString("subjectId")
        fechaSeleccionada = arguments?.getString("fecha")
        Log.e("FechaA","${fechaSeleccionada}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Puedes reutilizar tu layout actual (activity_attendance.xml) o renombrarlo a bottom_sheet_attendance.xml
        return inflater.inflate(R.layout.activity_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAttendance = view.findViewById(R.id.rvAttendance)
        rvAttendance.layoutManager = LinearLayoutManager(requireContext())
        attendanceAdapter = AttendanceAdapter(attendanceList)
        rvAttendance.adapter = attendanceAdapter

        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: materia no encontrada", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            startAttendanceListener(subjectId!!)
        }
    }

    private fun startAttendanceListener(subjectId: String) {
        // 1. Obtenemos la lista de IDs de estudiantes inscritos desde el documento del subject.
        db.collection("subjects")
            .document(subjectId)
            .get()
            .addOnSuccessListener { doc ->
                // Se espera que el campo se llame "enrolledStudents" y contenga una lista de userIds.
                val enrolledStudents = doc.get("enrolledStudents") as? List<String>
                if (enrolledStudents.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No hay estudiantes inscritos", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                // 2. Recuperamos los nombres de los usuarios usando una consulta whereIn.
                db.collection("users")
                    .whereIn(FieldPath.documentId(), enrolledStudents)
                    .get()
                    .addOnSuccessListener { usersSnapshot ->
                        val userNames = mutableMapOf<String, String>()
                        for (userDoc in usersSnapshot.documents) {
                            val uid = userDoc.id
                            val name = userDoc.getString("name") ?: uid
                            userNames[uid] = name
                        }
                        // 3. Escuchamos la subcolección "attendance" en tiempo real.
                        db.collection("subjects")
                            .document(subjectId)
                            .collection("attendance")
                            .whereEqualTo("date", fechaSeleccionada)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    Log.w("attendance", "Error al escuchar asistencia", error)
                                    return@addSnapshotListener
                                }
                                // Creamos un mapa con la asistencia: clave = studentId, valor = time.
                                val attendanceMap = mutableMapOf<String, String>()
                                if (snapshot != null) {
                                    for (docAtt in snapshot.documents) {
                                        val studentId = docAtt.getString("studentId") ?: continue
                                        val time = docAtt.getString("time") ?: "Sin hora"
                                        attendanceMap[studentId] = time
                                    }
                                }
                                // 4. Recorremos la lista de inscritos y definimos su estado.
                                attendanceList.clear()
                                enrolledStudents.forEach { userId ->
                                    // Obtenemos el nombre del usuario; si no se encontró, se muestra el userId.
                                    val displayName = userNames[userId] ?: userId
                                    if (attendanceMap.containsKey(userId)) {
                                        val time = attendanceMap[userId] ?: "Sin hora"
                                        attendanceList.add(DataAttendance(displayName, time, R.drawable.ic_check_circle))
                                    } else {
                                        attendanceList.add(DataAttendance(displayName, "Sin registro", R.drawable.ic_missing))
                                    }
                                }
                                attendanceAdapter.notifyDataSetChanged()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al obtener datos de usuarios", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener estudiantes inscritos", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(subjectId: String, fecha: String): AttendanceMain {
            val fragment = AttendanceMain()
            val args = Bundle()
            args.putString("subjectId", subjectId)
            args.putString("fecha", fecha) // 👈 agregamos la fecha
            fragment.arguments = args
            return fragment
        }
    }
}
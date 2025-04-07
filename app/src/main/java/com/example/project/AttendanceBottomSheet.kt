package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceBottomSheet : BottomSheetDialogFragment() {

    private lateinit var rvAttendance: androidx.recyclerview.widget.RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceList = mutableListOf<DataAttendance>()
    private val db = FirebaseFirestore.getInstance()
    private var subjectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recupera el subjectId pasado en los argumentos
        subjectId = arguments?.getString("subjectId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Infla el layout que actualmente usas para la Activity
        return inflater.inflate(R.layout.activity_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAttendance = view.findViewById(R.id.rvAttendance)
        rvAttendance.layoutManager = LinearLayoutManager(context)

        // Inicializa el adapter y lo asigna al RecyclerView
        attendanceAdapter = AttendanceAdapter(attendanceList)
        rvAttendance.adapter = attendanceAdapter

        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(context, "Error: materia no encontrada", Toast.LENGTH_SHORT).show()
            dismiss() // Cierra el Bottom Sheet en lugar de finalizar una Activity
        } else {
            startAttendanceListener(subjectId!!)
        }
    }

    private fun startAttendanceListener(subjectId: String) {
        db.collection("subjects")
            .document(subjectId)
            .collection("attendance")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("attendance", "Error al escuchar asistencia", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("attendance", "Documentos recibidos: ${snapshot.documents.size}")
                    attendanceList.clear()
                    for (document in snapshot.documents) {
                        val studentName = document.getString("name") ?: "Sin nombre"
                        val time = document.getString("time") ?: "Sin hora"
                        val attendanceEntry = DataAttendance(studentName, time, R.drawable.ic_missing)
                        attendanceList.add(attendanceEntry)
                    }
                    attendanceAdapter.notifyDataSetChanged()
                }
            }
    }

    companion object {
        fun newInstance(subjectId: String): AttendanceBottomSheet {
            val fragment = AttendanceBottomSheet()
            val args = Bundle()
            args.putString("subjectId", subjectId)
            fragment.arguments = args
            return fragment
        }
    }
}

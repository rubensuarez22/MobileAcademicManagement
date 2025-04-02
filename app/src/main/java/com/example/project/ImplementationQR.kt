package com.example.project

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class ImplementationQR(private val activity: AppCompatActivity) {

    private lateinit var scanQrBtn: Button
    private var isScannerInstalled = false
    private lateinit var scanner: GmsBarcodeScanner

    fun init() {
        installGoogleScanner()
        initVars()
        registerUiListener()
    }

    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(activity)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(activity))
            .build()

        moduleInstall.installModules(moduleInstallRequest)
            .addOnSuccessListener {
                isScannerInstalled = true
            }
            .addOnFailureListener {
                isScannerInstalled = false
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun initVars() {
        scanQrBtn = activity.findViewById(R.id.btnScanQr)

        val options = initializeGoogleScanner()
        scanner = GmsBarcodeScanning.getClient(activity, options)
    }

    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
    }

    private fun registerUiListener() {
        scanQrBtn.setOnClickListener {
            if (isScannerInstalled) {
                startScanning()
            } else {
                Toast.makeText(activity, "Please try again...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScanning() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val result = barcode.rawValue
                result?.let { subjectId ->
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val studentId = user.uid
                        val studentName = user.displayName ?: "Estudiante"
                        val timestamp = System.currentTimeMillis()

                        val data = hashMapOf(
                            "studentId" to studentId,
                            "studentName" to studentName,
                            "timestamp" to timestamp
                        )

                        val db = FirebaseFirestore.getInstance()
                        db.collection("subjects")
                            .document(subjectId)
                            .collection("attendance")
                            .document(studentId) // o usa .add(data) si quieres permitir múltiples registros
                            .set(data)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "¡Asistencia registrada en $subjectId!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity, "Error al registrar asistencia", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnCanceledListener {
                Toast.makeText(activity, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

}


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
                result?.let { qrString ->
                    // En lugar de registrar la asistencia aquí, delegamos la lógica a StudentMain
                    if (activity is StudentMain) {
                        (activity as StudentMain).handleScannedQr(qrString)
                    } else {
                        // En caso de que no sea StudentMain, simplemente mostramos el resultado
                        Toast.makeText(activity, "QR escaneado: $qrString", Toast.LENGTH_SHORT).show()
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

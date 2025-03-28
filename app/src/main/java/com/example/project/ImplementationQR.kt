package com.example.project

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class ImplementationQR(private val activity: AppCompatActivity) {

    private lateinit var scanQrBtn: Button
    private lateinit var scannedValueTv: TextView
    private var isScannerInstalled = false
    private lateinit var scanner: GmsBarcodeScanner

    // Método de inicialización que debe llamarse desde onCreate() de la Activity
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
        // Asegúrate de que en el layout de la Activity existan las vistas con estos IDs
        scanQrBtn = activity.findViewById(R.id.btnScanAttendance)

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
            .addOnSuccessListener {
                val result = it.rawValue
                result?.let { value ->
                    scannedValueTv.text = "Scanned Value: $value"
                }
            }
            .addOnCanceledListener {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}

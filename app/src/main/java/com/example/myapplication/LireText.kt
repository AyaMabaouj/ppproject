package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException
import java.util.Locale

class LireText : AppCompatActivity() {
    private var textView: TextView? = null
    private var surfaceView: SurfaceView? = null
    private var cameraSource: CameraSource? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lire_text)
        surfaceView = findViewById(R.id.camera)
        textView = findViewById(R.id.text)
        startCameraSurface()
        initializeTextToSpeech()
    }

    private fun startCameraSurface() {
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (!textRecognizer.isOperational) {
            Log.w("Tag", "Dependencies not loaded yet")
        } else {
            cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

            surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                    if (ActivityCompat.checkSelfPermission(
                            this@LireText,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@LireText, arrayOf(Manifest.permission.CAMERA),
                            PERMISSION
                        )
                        return
                    }
                    try {
                        cameraSource?.start(surfaceView!!.holder)
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    surfaceHolder: SurfaceHolder,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                    // Handle surfaceChanged event
                }

                override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                    cameraSource?.stop()
                    textToSpeech?.stop() // Stop any ongoing speech when the camera is closed
                }
            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {
                    // Implement release method
                }

                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items: SparseArray<TextBlock> = detections.detectedItems
                    if (items.size() != 0) {
                        for (i in 0 until items.size()) {
                            val item: TextBlock = items.valueAt(i)
                            val recognizedText: String = item.value

                            // Speak each detected block of text immediately
                            speakText(recognizedText)

                            // Optionally, update the TextView if needed
                            runOnUiThread { textView!!.text = recognizedText }
                        }
                    }
                }
            })
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = textToSpeech!!.setLanguage(Locale.getDefault())
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Language not supported")
                } else {
                    Log.d("TextToSpeech", "TextToSpeech initialization successful")
                }
            } else {
                Log.e("TextToSpeech", "Initialization failed")
            }
        }
    }

    private fun speakText(text: String) {

        val speakResult = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        if (speakResult == TextToSpeech.ERROR) {
            Log.e("TextToSpeech", "Error while speaking text")
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val PERMISSION = 100
    }
}

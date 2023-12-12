package com.example.myapplication
import android.util.Log
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.RelativeLayout
import android.widget.Toast
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class HomeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private val SPEECH_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        // Find TextView elements by their ID
        val objects = findViewById<RelativeLayout>(R.id.objetRelativeLayout)
        val texts = findViewById<RelativeLayout>(R.id.textRelativeLayout)
        val parametre = findViewById<RelativeLayout>(R.id.settingsRelativeLayout)
        val navigate = findViewById<RelativeLayout>(R.id.navRelativeLayout)
        val vocale = findViewById<RelativeLayout>(R.id.vocaleRelativeLayout)

        // Add a click listener to the TextView elements
        objects.setOnClickListener {
            speakText("Object")
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
        }

        texts.setOnClickListener {
            speakText("Text to speech")
            val intent = Intent(this@HomeActivity, TextToSpeechActivity::class.java)
            startActivity(intent)
        }

        parametre.setOnClickListener {
            speakText("Parameter")
            // Add logic for opening parameter activity
        }

        navigate.setOnClickListener {
            speakText("Navigation")
            val intent = Intent(this@HomeActivity, Map::class.java)
            startActivity(intent)
        }

        vocale.setOnClickListener {
            checkPermissionAndStartListening()
        }
    }

    private fun checkPermissionAndStartListening() {
        if (isPermissionGranted()) {
            startListening()
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            SPEECH_REQUEST_CODE
        )
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Speech recognition is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                processSpokenText(spokenText)
            }
        }
    }

    private fun processSpokenText(spokenText: String) {
        Log.d("SpokenText", "Recognized: $spokenText")

        when {
            spokenText.contains("object") -> {
                Log.d("SpokenText", "Object command recognized")
                speakText(" Objets")
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
            spokenText.contains("text to speech") -> {
                Log.d("SpokenText", "Text to Speech command recognized")
                speakText(" Texte to Speech")
                val intent = Intent(this@HomeActivity, TextToSpeechActivity::class.java)
                startActivity(intent)
            }
            spokenText.contains("parameter") -> {
                Log.d("SpokenText", "Parameter command recognized")
                speakText("Parameters")
                // Add logic for opening parameter activity
            }
            spokenText.contains("navigation") -> {
                Log.d("SpokenText", "Navigation command recognized")
                speakText("Maps")
                val intent = Intent(this@HomeActivity, Map::class.java)
                startActivity(intent)
            }
            else -> {
                Log.d("SpokenText", "Command not recognized")
                speakText("Command not recognized")
            }
        }
    }

    // Function to speak text using TextToSpeech
    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Implement the OnInitListener method
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Default language: you can change the language if necessary
            val result = textToSpeech.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language is not supported, you can handle it here
            }
        } else {
            // Error during TextToSpeech initialization
        }
    }

    // Make sure to release TextToSpeech resources when closing the activity
    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        speechRecognizer.destroy()
        super.onDestroy()
    }
}

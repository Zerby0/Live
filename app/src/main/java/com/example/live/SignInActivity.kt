    package com.example.live

    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Toast
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.example.live.databinding.ActivitySignInBinding
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.LocationServices
    import com.google.firebase.analytics.FirebaseAnalytics
    import com.google.firebase.auth.FirebaseAuth

    class SignInActivity : AppCompatActivity() {

        // Telemetria Firebase
        private lateinit var fbAuth: FirebaseAuth
        private lateinit var fbAnalytics: FirebaseAnalytics
        private lateinit var fusedLocProvider : FusedLocationProviderClient
        // Altro
        private lateinit var binding: ActivitySignInBinding
        private val REQUEST_CODE = 101

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            fbAuth = FirebaseAuth.getInstance()
            fbAnalytics = FirebaseAnalytics.getInstance(this)
            fusedLocProvider = LocationServices.getFusedLocationProviderClient(this)

            // Evento di test (capire perché Firebase non apre i dettagli)
            val testBundle = Bundle()
            testBundle.putString("test_key", "test_value")
            fbAnalytics.logEvent("test_event", testBundle)

            binding = ActivitySignInBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Se utente già loggato, skippa tutto e vai a MainActivity
            val sharedPref = getSharedPreferences("logged_users", Context.MODE_PRIVATE)
            val loggedUser = sharedPref.getString("user", null)
            val loggedPw = sharedPref.getString("pw", null)

            if (loggedUser != null && loggedPw != null) {
                fbAuth.signInWithEmailAndPassword(loggedUser, loggedPw).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Invia dati utente a Firebase
                        val userData = collectUserData()
                        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, userData)
                        // Manda user a MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login automatico fallito.", Toast.LENGTH_LONG).show()
                    }
                }

            }

            // Manda utente a sign-up se non registrato
            binding.textView3.setOnClickListener {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            // Attendi azione di sign-in
            binding.button2.setOnClickListener {
                // Get user data
                val userMail = binding.editTextText7.text.toString()
                val pw = binding.editTextTextPassword.text.toString()

                // Check campi vuoti
                if (userMail.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(this, "Non sono ammessi campi vuoti!", Toast.LENGTH_LONG).show()
                } else {
                    fbAuth.signInWithEmailAndPassword(userMail, pw).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            // Salva dati di login in SharedPreferences per login futuri
                            val edit = sharedPref.edit()
                            edit.putString("user", userMail)
                            edit.putString("pw", pw)
                            // Chiudi editor
                            edit.apply()
                            // Manda dati utente a Firebase
                            val userData = collectUserData()
                            fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, userData)
                            // Manda utente a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            Toast.makeText(this, "Login fallito, riprovare.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        private fun collectUserData() : Bundle {
            val dataBundle = Bundle()
            // Info dispositivo
            dataBundle.apply {
                putLong("time", System.currentTimeMillis())
                putString("device", android.os.Build.DEVICE)
                putString("model", android.os.Build.MODEL)
                putString("brand", android.os.Build.BRAND)
                putString("manufacturer", android.os.Build.MANUFACTURER)
                putString("hardware", android.os.Build.HARDWARE)
                putString("deviceOS", android.os.Build.VERSION.RELEASE)
                putString("position", getLocation(fusedLocProvider))
            }
            // Info posizione
            dataBundle.apply {

            }

            return dataBundle
        }


        private fun getLocation(fusedLocProvClient: FusedLocationProviderClient) : String {
            var pos = ""
            // Controlla permessi posizione
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            // Ottieni posizione
            val location = fusedLocProvClient.lastLocation
            location.addOnSuccessListener {
                if (it != null) {
                    pos = "${it.latitude}, ${it.longitude}"
                }
            }
            return pos
        }


    }
package ru.netology.nmedia

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        checkGoogleAvailability()
    }

    private fun checkGoogleAvailability() {
        with(GoogleApiAvailabilityLight.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                return@with
            }
            Toast.makeText(this@AppActivity, R.string.google_api_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

}
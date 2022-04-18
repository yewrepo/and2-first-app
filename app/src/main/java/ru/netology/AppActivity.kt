package ru.netology

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.Success
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.vm.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var googleApiAvailabilityLight: GoogleApiAvailabilityLight

    private lateinit var binding: ActivityAppBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkGoogleAvailability()

        viewModel.data.observe(this) { authState ->
            if (authState.id > 0) {
                getNavigation().navigateUp()
            }
        }
        viewModel.loadingState.observe(this) { state ->
            if (state == Success) {
                invalidateOptionsMenu()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                getNavigation().navigate(R.id.action_feedFragment_to_authFragment)
                true
            }
            R.id.signout -> {
                viewModel.removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNavigation() = findNavController(R.id.nav_host_fragment_container)

    private fun checkGoogleAvailability() {
        with(googleApiAvailabilityLight) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                return@with
            }
            Toast.makeText(this@AppActivity, R.string.google_api_unavailable, Toast.LENGTH_SHORT)
                .show()
        }
    }

}
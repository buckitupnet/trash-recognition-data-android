package com.stetsiuk.cameraapp.ui.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.stetsiuk.cameraapp.MainActivity
import com.stetsiuk.cameraapp.R
import com.stetsiuk.cameraapp.app.util.showShortToast
import com.stetsiuk.cameraapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsBtn: Button
    private lateinit var messageCardView: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var mainActivity: MainActivity

    private val multiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted = true
            var isNotRationable = false
            for (permission in permissions) {
                if (permission.value == false) isGranted = false
                if (!shouldShowRequestPermissionRationale(permission.key)) isNotRationable = true
            }
            if (isGranted) {
                mainActivity.goToCameraFromSplash()
            } else {
                if (isNotRationable) {
                    settingsBtn.visibility = View.VISIBLE
                    messageCardView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    mainActivity.showShortToast(getString(R.string.permissions_are_not_granted_tost))
                } else {
                    mainActivity.finishAfterTransition()
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        settingsBtn = binding.settingsBtn
        messageCardView = binding.messageCardView
        progressBar = binding.splashProgressBar
        binding.settingsBtn.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts(SETTINGS_SCHEME, activity?.packageName, null)
            ).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!areAllPermissionGranted(requireContext())) {
            multiplePermissions.launch(PERMISSIONS_REQUIRED)
        } else {
            mainActivity.goToCameraFromSplash()
        }
    }

    private fun areAllPermissionGranted(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val SETTINGS_SCHEME = "package"
        private val PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
package com.siabucketup.ecotaxi

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.siabucketup.ecotaxi.databinding.ActivityMainBinding
import com.siabucketup.ecotaxi.ui.camera.CameraNavigation
import com.siabucketup.ecotaxi.ui.confirm.ConfirmationNavigation
import com.siabucketup.ecotaxi.ui.demo.DemoNavigation
import com.siabucketup.ecotaxi.ui.splash.SplashNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SplashNavigation, CameraNavigation,
    ConfirmationNavigation, DemoNavigation {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fragmentContainer = binding.fragmentContainer
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun goToCameraFromSplash() {
        lifecycleScope.launchWhenStarted {
            findNavController(R.id.fragment_container)
                .navigate(R.id.action_permissions_to_camera)
        }
    }

    override fun goToConfirmFromCamera() {
        findNavController(R.id.fragment_container)
            .navigate(R.id.action_camera_to_confirmation)
    }

    override fun goToConfirmFromCamera(touchX: Double, touchY: Double) {
        findNavController(R.id.fragment_container)
            .navigate(R.id.action_camera_to_confirmation)
    }

    override fun goToDemoFromCamera() {
        findNavController(R.id.fragment_container)
            .navigate(R.id.action_cameraFragment_to_demoFragment)
    }

    override fun goBackToCameraFromConfirmation() {
        popBackStack()
    }

    override fun goBackToCameraFromDemo() {
        popBackStack()
    }

    private fun popBackStack() {
        findNavController(R.id.fragment_container)
            .popBackStack()
    }

    companion object {
        const val TAG = "TrashRecognitions"
    }
}
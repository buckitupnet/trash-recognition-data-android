package com.siabucketup.ecotaxi.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.siabucketup.ecotaxi.MainActivity
import com.siabucketup.ecotaxi.R
import com.siabucketup.ecotaxi.app.BROWSER_LINK
import com.siabucketup.ecotaxi.app.util.showShortToast
import com.siabucketup.ecotaxi.databinding.FragmentCameraBinding
import com.siabucketup.ecotaxi.ui.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var imageCapture: ImageCapture? = null
    private val viewModel: CameraViewModel by viewModels()
    private val sharedVM: SharedViewModel by activityViewModels()
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraCaptureBtn: ImageButton
    private lateinit var linkToBrowserBtn: CircleImageView
    private lateinit var goToDemoBtn: ImageButton
    private lateinit var previewView: PreviewView
    private lateinit var clickableView: View
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        setScreenAspectRatio()
        init()
        return binding.root
    }

    private fun setScreenAspectRatio() {
        val displayMetrics = mainActivity.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()
        val screenRatio: Float = screenHeight / screenWidth

        if (screenRatio < MIN_AVAILABLE_ASPECT_RATIO) {
            CAMERA_ASPECT_RATIO = AspectRatio.RATIO_4_3
            val cs = ConstraintSet()
            cs.clone(binding.root)
            cs.setDimensionRatio(R.id.preview_view, RATIO_3_4)
            cs.applyTo(binding.root)
        }
    }

    private fun setXYOnCenter() {
        sharedVM.setXY(clickableView.width.toDouble() / TWO, clickableView.height.toDouble() / TWO)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        cameraCaptureBtn = binding.cameraCaptureBtn
        linkToBrowserBtn = binding.linkToBrowserBtn
        goToDemoBtn = binding.goToDemoBtn
        previewView = binding.previewView
        clickableView = binding.clickableView
        cameraCaptureBtn.setOnClickListener {
            setXYOnCenter()
            takePhotoAndGoToConfirmScreen()
        }
        linkToBrowserBtn.setOnClickListener {
            openBrowserLink()
        }
        goToDemoBtn.setOnClickListener {
            mainActivity.goToDemoFromCamera()
        }
        clickableView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    val xTouch = motionEvent.x.toDouble()
                    val yTouch = motionEvent.y.toDouble()
                    sharedVM.setXY(xTouch, yTouch)
                    takePhotoAndGoToConfirmScreen()
                }
            }
            return@setOnTouchListener true
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val CAMERA_ASPECT_RATIO_RES =
                if (CAMERA_ASPECT_RATIO == AspectRatio.RATIO_16_9)
                    Size(480, 854)
                else
                    Size(480, 640)
            val preview = Preview.Builder()
                .setTargetResolution(CAMERA_ASPECT_RATIO_RES)
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(CAMERA_ASPECT_RATIO_RES)
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(MainActivity.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(mainActivity))
    }

    //ToRefactor
    private fun takePhotoAndGoToConfirmScreen() {
        val imageCapture = imageCapture ?: return
        val pictureName = viewModel.generatePictureName()
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                mainActivity.contentResolver,
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                viewModel.generateContentValues(pictureName)
            )
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mainActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    mainActivity.showShortToast(getString(R.string.failed_to_take_a_photo))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let {
                        val savedPicture = SavedPicture(pictureName, it)
                        sharedVM.setSavedPhotoUri(savedPicture)
                        sharedVM.setMaxHeight(binding.previewView.height - binding.confirmButtonsContainer.height)
                        mainActivity.goToConfirmFromCamera()
                    }
                }
            }
        )
    }

    private fun openBrowserLink() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BROWSER_LINK)))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        private const val TWO = 2
        private const val MIN_AVAILABLE_ASPECT_RATIO: Float = 16f / 9f
        private const val RATIO_3_4 = "3:4"
        private var CAMERA_ASPECT_RATIO = AspectRatio.RATIO_16_9

    }
}
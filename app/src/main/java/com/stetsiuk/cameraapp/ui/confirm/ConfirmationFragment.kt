package com.stetsiuk.cameraapp.ui.confirm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.stetsiuk.cameraapp.MainActivity
import com.stetsiuk.cameraapp.R
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.app.util.showShortToast
import com.stetsiuk.cameraapp.app.util.toBitmap
import com.stetsiuk.cameraapp.databinding.FragmentConfirmationBinding
import com.stetsiuk.cameraapp.databinding.PopUpScreenBinding
import com.stetsiuk.cameraapp.model.TransformImage
import com.stetsiuk.cameraapp.ui.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfirmationFragment : Fragment(), OnTagClickListener {

    private val viewModel: ConfirmationViewModel by viewModels()
    private val sharedVM: SharedViewModel by activityViewModels()
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawingView: DrawingView
    private lateinit var photoHolder: ImageView
    private lateinit var popupWindow: PopupWindow
    private lateinit var cancelBtn: ImageButton
    private lateinit var confirmImageBtn: ImageButton
    private lateinit var bitmap: Bitmap
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        init()
        sharedVM.touchX.value?.let { x ->
            sharedVM.touchY.value?.let { y ->
                drawingView.onStartPutRectangleIn(x, y)
            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun init() {
        photoHolder = binding.takenPhoto
        drawingView = binding.drawingView
        cancelBtn = binding.cancelImageBtn
        confirmImageBtn = binding.confirmImageBtn
        sharedVM.picture.observe(viewLifecycleOwner) {
            binding.takenPhoto.setImageURI(it.savedUri)
            bitmap = it.savedUri.toBitmap(mainActivity.contentResolver)
            viewModel.startToGenerateBitmap(bitmap)
        }
        viewModel.saveResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success<Uri> -> {
                    mainActivity.showShortToast(getString(R.string.toast_photo_saved))
                    lifecycleScope.launch {
                        binding.saveProgressBar.visibility = View.GONE
                        delay(DELAY_BEFORE_GO_TO_CAMERA)
                        mainActivity.goBackToCameraFromConfirmation()
                    }
                }
                else -> {
                    mainActivity.showShortToast(getString(R.string.failed_to_save))
                    removePictureAndGoBackToCamera()
                }
            }
        }
        cancelBtn.setOnClickListener {
            sharedVM.picture.value?.let {
                removePictureAndGoBackToCamera()
            }
        }
        confirmImageBtn.setOnClickListener {
            showPopUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun removePictureAndGoBackToCamera() {
        try {
            sharedVM.picture.value?.let {
                mainActivity.contentResolver?.delete(it.savedUri, null)
                mainActivity.goBackToCameraFromConfirmation()
            }
        } catch (ex: Exception) {
            Log.e(MainActivity.TAG, "delete picture err: ${ex.message}", ex)
        }
    }

    @SuppressLint("InflateParams")
    private fun showPopUp() {
        val popUpScreenBinding: PopUpScreenBinding = PopUpScreenBinding.inflate(layoutInflater)
        val popupCancelBtn: Button = popUpScreenBinding.popupCancel
        popupWindow = PopupWindow(
            popUpScreenBinding.root,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        val adapter = TagsAdapter(this)
        popUpScreenBinding.tags.adapter = adapter
        popupWindow.showAsDropDown(binding.root)
        setDim()
        popupCancelBtn.setOnClickListener {
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener {
            removeDim()
        }
    }

    private fun setDim() {
        val attributes = mainActivity.window.attributes
        attributes.alpha = OPEN_POPUP_ALPHA
        mainActivity.window.attributes = attributes
    }

    private fun removeDim() {
        val attributes = mainActivity.window.attributes
        attributes.alpha = CLOSE_POPUP_ALPHA
        mainActivity.window.attributes = attributes
    }

    override fun onTagClick(data: String) {
        popupWindow.dismiss()
        setTagAndLockDrawingView(data)
        setViewsStateOnSaving()
        sharedVM.picture.value?.let {
            val toTransformData = TransformImage(
                it.savedUri,
                photoHolder.width,
                photoHolder.height,
                bitmap.width,
                bitmap.height,
                drawingView.tag.toString(),
                it.name
            )
            viewModel.saveData(
                toTransformData,
                drawingView.getRectangleArea
            )
        }
    }

    private fun setViewsStateOnSaving() {
        binding.cancelImageBtn.isEnabled = false
        binding.confirmImageBtn.isEnabled = false
        binding.saveProgressBar.visibility = View.VISIBLE
    }

    private fun setTagAndLockDrawingView(tag: String) {
        drawingView.tag = tag
        drawingView.invalidate()
        drawingView.lockMovingRectangle()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val OPEN_POPUP_ALPHA = 0.3F
        private const val CLOSE_POPUP_ALPHA = 1F
        private const val DELAY_BEFORE_GO_TO_CAMERA: Long = 1000
    }
}
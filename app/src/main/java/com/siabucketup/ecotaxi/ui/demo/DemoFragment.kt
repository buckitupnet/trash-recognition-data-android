package com.siabucketup.ecotaxi.ui.demo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.siabucketup.ecotaxi.MainActivity
import com.siabucketup.ecotaxi.R
import com.siabucketup.ecotaxi.databinding.FragmentDemoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoHolder: VideoView
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        videoHolder = binding.videoHolder
        initVideo()
        binding.exitBtn.setOnClickListener {
            stopVideoAndGoToCamera()
        }
        videoHolder.setOnCompletionListener {
            videoHolder.start()
        }
    }

    private fun initVideo() {
        videoHolder.setVideoPath("android.resource://${mainActivity.packageName}/${R.raw.demo_video}")
        videoHolder.requestFocus()
        videoHolder.start()
    }

    private fun stopVideoAndGoToCamera() {
        if (videoHolder.isPlaying) {
            videoHolder.pause()
            mainActivity.goBackToCameraFromDemo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.stetsiuk.cameraapp.ui.demo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.stetsiuk.cameraapp.MainActivity
import com.stetsiuk.cameraapp.R
import com.stetsiuk.cameraapp.databinding.FragmentDemoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoHolder: VideoView
    private lateinit var pauseBtn: ImageView
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
        pauseBtn = binding.pauseBtn
        videoHolder = binding.videoHolder
        initVideo()
        binding.exitBtn.setOnClickListener {
            mainActivity.goBackToCameraFromDemo()
        }
        pauseBtn.setOnClickListener {
            pauseOrStartVideo()
        }
        videoHolder.setOnClickListener {
            pauseOrStartVideo()
        }
        videoHolder.setOnCompletionListener {
            pauseOrStartVideo()
        }
    }

    private fun initVideo() {
        videoHolder.setVideoPath("android.resource://${mainActivity.packageName}/${R.raw.demo_video}")
        videoHolder.requestFocus()
        videoHolder.start()
    }

    private fun pauseOrStartVideo() {
        if (videoHolder.isPlaying) {
            videoHolder.pause()
            pauseBtn.visibility = GONE
        } else {
            videoHolder.start()
            pauseBtn.visibility = VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
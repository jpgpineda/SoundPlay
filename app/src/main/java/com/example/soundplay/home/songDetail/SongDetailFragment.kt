package com.example.soundplay.home.songDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.example.soundplay.R
import com.example.soundplay.core.model.Song
import com.example.soundplay.databinding.FragmentSongDetailBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.util.Locale


class SongDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentSongDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SondDetailViewModel>()
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = requireArguments().getParcelable("song")
            ?: error("Song argument required")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSongDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindSongInfo()
        setupListeners()
        observeViewModel()

        viewModel.prepare(song)
    }

    private fun bindSongInfo() {
        binding.tvTitle.text = song.name
        binding.tvArtist.text = song.artistName
        binding.tvAlbum.text = song.albumName
        Glide.with(binding.ivCover)
            .load(song.albumImage)
            .centerCrop()
            .into(binding.ivCover)
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.tvCurrent.text = formatTime(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) = Unit
            override fun onStopTrackingTouch(sb: SeekBar?) {
                sb?.progress?.let { viewModel.seekTo(it) }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.durationMs.collect { duration ->
                        binding.seekBar.max = duration
                        binding.tvTotal.text = formatTime(duration)
                    }
                }

                launch {
                    viewModel.currentPositionMs.collect { pos ->
                        binding.seekBar.progress = pos
                        binding.tvCurrent.text = formatTime(pos)
                    }
                }

                launch {
                    viewModel.isPlaying.collect { playing ->
                        binding.btnPlayPause.setImageResource(
                            if (playing) android.R.drawable.ic_media_pause
                            else android.R.drawable.ic_media_play
                        )
                    }
                }
            }
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        // Opcional: pausar si el usuario sale de la pantalla
        if (viewModel.isPlaying.value) viewModel.togglePlayPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
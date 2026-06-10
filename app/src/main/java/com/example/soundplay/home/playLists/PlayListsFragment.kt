package com.example.soundplay.home.playLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soundplay.R
import com.example.soundplay.core.FragmentCommunicator
import com.example.soundplay.core.ResponseService
import com.example.soundplay.databinding.FragmentPlayListsBinding
import com.example.soundplay.home.songs.SongsAdapter
import com.example.soundplay.home.songs.SongsSharedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PlayListsFragment : Fragment() {

    private var _binding: FragmentPlayListsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PlayListsViewModel>()
    private val sharedViewModel by activityViewModels<SongsSharedViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private val adapter = SongsAdapter { song ->
        sharedViewModel.selectSong(song)
        findNavController().navigate(R.id.action_playListsFragment_to_songDetailFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayListsBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        observeState()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Refresca la lista cada vez que el usuario vuelve a esta pantalla,
        // por si agregó o quitó favoritos desde el detalle.
        viewModel.loadFavorites()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.tvEmpty.visibility = View.GONE
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            adapter.submitList(state.data)
                            binding.tvEmpty.visibility =
                                if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

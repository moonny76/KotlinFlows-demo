package org.scarlet.android.movies.features

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.scarlet.R
import org.scarlet.util.Resource
import org.scarlet.util.TopSpacingItemDecoration
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.scarlet.android.movies.MovieApplication
import org.scarlet.android.movies.OpStyle
import org.scarlet.android.movies.adapter.MoviesListAdapter
import org.scarlet.android.movies.model.Movie

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MovieListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: MoviesListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var root: View

    private val args: MovieListFragmentArgs by navArgs()

    private val viewModel: MovieListViewModel by viewModels {
        val app = requireContext().applicationContext as MovieApplication
        MovieListViewModelFactory(app.injection.repository!!)
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(layoutInflater, container, savedInstanceState)
        root = layoutInflater.inflate(R.layout.fragment_movie_list, container, false)

        getViews(root)

        return root
    }

    private fun getViews(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        progressBar = root.findViewById(R.id.progressBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        when (args.style) {
            OpStyle.LIVEDATA -> {
                viewModel.moviesLiveData.observe(viewLifecycleOwner) { resource ->
                    handleResource(resource)
                }
            }

            OpStyle.FLOW -> {
                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        viewModel.moviesFlow.collect { resource ->
                            handleResource(resource)
                        }
                    }
                }
            }

            OpStyle.FLOW_V2 -> {
                val job = lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        viewModel.moviesFlowV2.collect { resource ->
                            handleResource(resource)
                        }
                    }
                }

                lifecycleScope.launch {
                    delay(3_000)
                    job.cancel()
                }
            }
        }

        viewModel.searchMovies(args.query)
    }

    private fun handleResource(resource: Resource<List<Movie>>) {
        Log.d(TAG, "[MovieListFragment]: handleResource = $resource")
        when (resource) {
            is Resource.Success -> {
                showLoading(false)
                listAdapter.submitList(resource.data!!)
            }

            is Resource.Loading -> showLoading(true)
            is Resource.Error -> showSnackBar(resource.message!!)
            else -> {}
        }
    }

    private fun setupRecyclerView() {
        listAdapter = MoviesListAdapter(object : MoviesListAdapter.Interaction {
            override fun onClickItem(movie: Movie) {
                showSnackBar("${movie.title} is selected")
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isFocusable = false
            addItemDecoration(TopSpacingItemDecoration(20))
            adapter = listAdapter
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        progressBar.visibility = if (status) View.VISIBLE else View.GONE
        recyclerView.visibility = if (status) View.GONE else View.VISIBLE
    }

    companion object {
        const val TAG = "Movies"
    }
}

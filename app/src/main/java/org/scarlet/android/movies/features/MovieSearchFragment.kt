package org.scarlet.android.movies.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import org.scarlet.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.scarlet.android.movies.OpStyle

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MovieSearchFragment : Fragment() {
    private lateinit var search1: Button
    private lateinit var search2: Button
    private lateinit var search3: Button

    private lateinit var movieTitle: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        getViews(root)

        search1.setOnClickListener {
            navigateToSearchResults(OpStyle.LIVEDATA, movieTitle.text.toString())
        }
        search2.setOnClickListener {
            navigateToSearchResults(OpStyle.FLOW, movieTitle.text.toString())
        }
        search3.setOnClickListener {
            navigateToSearchResults(OpStyle.FLOW_V2, movieTitle.text.toString())
        }

        return root
    }

    private fun getViews(root: View) {
        movieTitle = root.findViewById(R.id.movie_title)
        search1 = root.findViewById(R.id.search_button1)
        search2 = root.findViewById(R.id.search_button2)
        search3 = root.findViewById(R.id.search_button3)
    }

    private fun navigateToSearchResults(style: OpStyle, query: String) {
        findNavController(requireView()).navigate(
            MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieListFragment(query, style)
        )
    }
}

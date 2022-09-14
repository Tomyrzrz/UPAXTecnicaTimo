package com.softim.upaxtecnica.ui.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.domain.data.adapters.AdapterMovies
import com.softim.upaxtecnica.databinding.FragmentMoviesBinding
import com.softim.upaxtecnica.domain.data.models.Movie
import com.softim.upaxtecnica.domain.data.models.MoviesResponse
import com.softim.upaxtecnica.domain.data.services.MovieAPIInterface
import com.softim.upaxtecnica.domain.data.services.MovieApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentMoviesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    val viewModel = MoviesViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movies.observe(viewLifecycleOwner) { items ->
            binding.rcvMovies.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = AdapterMovies(items)
            }
        }

        val categories = resources.getStringArray(R.array.movies_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spnCategory.adapter = adapter
        binding.spnCategory.setSelection(0)
        binding.spnCategory.onItemSelectedListener = this
        viewModel.category.observe(viewLifecycleOwner) { cate ->
            Toast.makeText(requireContext(),cate,Toast.LENGTH_SHORT).show()
            viewModel.updateMovieData(cate){
                binding.rcvMovies.adapter = AdapterMovies(it)
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        viewModel.category.apply { value = binding.spnCategory.selectedItem.toString().toLowerCase()}
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.category.value = p0?.selectedItem.toString().toLowerCase()
    }


}

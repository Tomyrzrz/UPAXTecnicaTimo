package com.softim.upaxtecnica.ui.movies

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.data.local.MoviesBDLocal
import com.softim.upaxtecnica.data.utils.ExceptionDialogFragment
import com.softim.upaxtecnica.databinding.FragmentMoviesBinding
import com.softim.upaxtecnica.domain.CheckInternet
import com.softim.upaxtecnica.domain.data.adapters.AdapterMovies
import com.softim.upaxtecnica.domain.data.models.Movie

class MoviesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentMoviesBinding
    private var moviesLocal = mutableListOf<Movie>()

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
            moviesLocal.addAll(items)
            setupLocalBDMovies(items)
        }
        setupLocalBDMovies(mutableListOf())

        val categories = resources.getStringArray(R.array.movies_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spnCategory.adapter = adapter
        binding.spnCategory.setSelection(0)
        binding.spnCategory.onItemSelectedListener = this
        viewModel.category.observe(viewLifecycleOwner) { cate ->
            viewModel.updateMovieData(cate){
                binding.rcvMovies.adapter = AdapterMovies(it)
                moviesLocal.addAll(it)
                setupLocalBDMovies(it)
            }
        }
    }

    private fun setupLocalBDMovies(movies: List<Movie>) {
        val admin = MoviesBDLocal(requireContext(), "movies_bd", null, 1)

        if (CheckInternet.checkForInternet(requireContext())) {
            val bd = admin.writableDatabase
            bd.delete("movies", null, null)
            for (movie in movies) {
                val registro = ContentValues()
                registro.put("id", movie.id)
                registro.put("title", movie.title)
                registro.put("overview", movie.overview)
                registro.put("poster_path", movie.poster)
                registro.put("vote_average", movie.vote_average)
                bd.insert("movies", null, registro)
            }
            bd.close()
        } else {
            val bd = admin.writableDatabase
            val fila = bd.rawQuery("select * from movies", null)
            if (fila.moveToFirst()) {
                moviesLocal.clear()
                do {
                    val mov = Movie(fila.getInt(0).toString(), fila.getString(1),
                        fila.getString(2), fila.getString(3), fila.getString(4))
                    moviesLocal.add(mov)
                } while (fila.moveToNext())
                binding.rcvMovies.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = AdapterMovies(moviesLocal)
                    binding.rcvMovies.adapter?.notifyDataSetChanged()
                }
            } else {
                ExceptionDialogFragment(
                    "Debes conectarte a Internet, No hay Datos por Mostrar",
                    "No Internet"
                ).show(parentFragmentManager, ExceptionDialogFragment.TAG)
            }
            bd.close()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        viewModel.category.apply { value = binding.spnCategory.selectedItem.toString().toLowerCase()}
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.category.value = p0?.selectedItem.toString().toLowerCase()
    }


}

package com.softim.upaxtecnica.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.databinding.FragmentMoviesBinding
import com.softim.upaxtecnica.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileViewModel = ProfileViewModel()
        val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"
        profileViewModel.profile.observe(viewLifecycleOwner) { person ->
            binding.txtNameProfile.text = person.name
            binding.txtBiography.text = person.biography
            "Popularity: ${person.popularity}".also { binding.txtPopularity.text = it }
            Glide.with(this).load(IMAGE_BASE + person.profile_path).into(binding.imgProfilePic)
        }
        setupLocal()
    }

    private fun setupLocal() {
        val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"
        "Brad Pitt".also { binding.txtNameProfile.text = it }
        ("William Bradley \"Brad\" Pitt (born December 18, 1963) is an American actor " +
                "and film producer. Pitt has received two Academy Award nominations and four " +
                "Golden Globe Award nominations, winning one. He has been described as one of " +
                "the world's most attractive men, a label for which he has received substantial " +
                "media attention. Pitt began his acting career with television guest appearances, " +
                "including a role on the CBS prime-time soap opera Dallas in 1987. He later " +
                "gained recognition as the cowboy hitchhiker who seduces Geena Davis's character " +
                "in the 1991 road movie Thelma & Louise. Pitt's first leading roles in big-budget" +
                " productions came with A River Runs Through It (1992) and Interview with " +
                "the Vampire (1994).").also { binding.txtBiography.text = it }
        "Popularity: 10.647".also { binding.txtPopularity.text = it }
        Glide.with(this).load(IMAGE_BASE + "/kU3B75TyRiCgE270EyZnHjfivoq.jpg").into(binding.imgProfilePic)

    }

}
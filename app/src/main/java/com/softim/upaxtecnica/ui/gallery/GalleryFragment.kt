package com.softim.upaxtecnica.ui.gallery

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.ui.utils.ExceptionDialogFragment
import com.softim.upaxtecnica.databinding.FragmentGalleryBinding
import com.softim.upaxtecnica.domain.core.CheckInternet
import java.io.IOException
import java.util.*


class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val IMAGEN = 5
    private var filePath: Uri? = null
    private val storageReference = Firebase.storage
    val uniqueID = UUID.randomUUID().toString()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.imgUploadPhoto.setOnClickListener {
            selectImage()
        }
        binding.btnUploadPhoto.setOnClickListener {
            if (filePath != null)
                uploadImage()
            else {
                val message = "Firts Select An Image."
                ExceptionDialogFragment(message, "Error").show(
                    parentFragmentManager,
                    ExceptionDialogFragment.TAG
                )
            }
        }
        return root
    }

    private fun uploadImage() {
        if (CheckInternet.checkForInternet(requireContext())) {
            val sharedPreferences =
                activity?.getSharedPreferences("user_gallery", AppCompatActivity.MODE_PRIVATE)
            val user_local = sharedPreferences?.getString("user", "")

            if (user_local == "") {
                val editor = sharedPreferences.edit()
                editor?.putString("user", uniqueID)
                editor?.apply()
            }
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref: StorageReference = storageReference.reference
                .child("images/" + uniqueID)

            val uploadTask = ref.putFile(filePath!!)
            uploadTask.addOnFailureListener {
                progressDialog.dismiss()
                val message = "Failed: ${it.message}"
                ExceptionDialogFragment(message, "Fail").show(
                    parentFragmentManager,
                    ExceptionDialogFragment.TAG
                )

            }.addOnSuccessListener { tarea ->
                if (tarea.task.isComplete) {
                    val uriTask: Task<Uri> = tarea.storage.downloadUrl
                    while ((!uriTask.isComplete))
                        Log.e("error", "Aun no")
                    progressDialog.dismiss()
                    Thread.sleep(1000)
                    clearData()
                }
            }
                .addOnProgressListener(
                    object : OnProgressListener<UploadTask.TaskSnapshot?> {
                        override fun onProgress(
                            taskSnapshot: UploadTask.TaskSnapshot
                        ) {
                            val progress = ((100.0
                                    * taskSnapshot.bytesTransferred
                                    / taskSnapshot.totalByteCount))
                            progressDialog.setMessage(
                                ("Uploaded " + progress.toInt() + "%")
                            )
                        }
                    })
        }else{
            ExceptionDialogFragment("You dont have internet for upload images", "No Internet")
                .show(parentFragmentManager, ExceptionDialogFragment.TAG)
        }
    }

    private fun clearData() {
        binding.imgUploadPhoto.setImageResource(R.drawable.select)
        filePath = null
        val message = "Image Upload Complete"
        ExceptionDialogFragment(message,"Complete").show(
            parentFragmentManager,
            ExceptionDialogFragment.TAG
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            IMAGEN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === this.IMAGEN && resultCode === RESULT_OK && data?.data != null) {
            filePath = data.data
            try {
                binding.imgUploadPhoto.setImageURI(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
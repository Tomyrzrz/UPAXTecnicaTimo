package com.softim.upaxtecnica.data.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.softim.upaxtecnica.R

//Clase para generar un DialogFragment que muestra las exepciones de la App
class ExceptionDialogFragment(val msj: String, val tittle: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(tittle)
            .setMessage(msj)
            .setPositiveButton(getString(R.string.title_ok)) { _,_ ->
            }
            .create()

    companion object {
        const val TAG = "Exception Error"
    }
}
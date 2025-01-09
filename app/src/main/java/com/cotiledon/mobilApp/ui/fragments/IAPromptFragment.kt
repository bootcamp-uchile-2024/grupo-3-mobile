package com.cotiledon.mobilApp.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.backend.ia.RetrofitIAClient
import com.cotiledon.mobilApp.ui.dataClasses.ia.IABase64Request
import com.cotiledon.mobilApp.ui.dataClasses.ia.IAResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class IAPromptFragment : Fragment() {
    private var photoPath: String? = null
    private lateinit var iaPrompt: EditText
    private lateinit var nextButton: Button
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val iaClient = RetrofitIAClient.createIAClient()

    companion object {
        private const val MAX_IMAGE_DIMENSION = 800  // Maximum width or height in pixels
        private const val COMPRESSION_QUALITY = 80   // Compression quality (0-100)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ia_prompt, container, false)
        iaPrompt = view.findViewById(R.id.ia_prompt_edit_text)
        nextButton = view.findViewById(R.id.ia_prompt_continue_button)
        photoPath = arguments?.getString("photo_path")
        nextButton.setOnClickListener {
            processImageAndSend()
        }
        return view
    }

    private fun processImageAndSend() {
        val consulta = iaPrompt.text.toString().trim().takeIf { it.isNotBlank() } ?: ""

        coroutineScope.launch {
            try {
                showLoading(true)
                val base64Image = convertImageToBase64()

                // Create the request with the compressed image
                val request = IABase64Request(
                    base64 = base64Image,
                    consulta = consulta
                )

                val response = withContext(Dispatchers.IO) {
                    iaClient.iaApiService.sendImageBase64(request)
                }

                handleSuccessResponse(response)
            } catch (e: Exception) {
                handleError(e)
            } finally {
                showLoading(false)
            }
        }
    }

    private suspend fun convertImageToBase64(): String {
        return withContext(Dispatchers.IO) {
            var byteArrayOutputStream: ByteArrayOutputStream? = null

            try {
                // First, we need to get the image dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true  // This option allows us to read dimensions without loading the full image
                }
                BitmapFactory.decodeFile(photoPath, options)

                // Calculate the scaling factor
                val scaleFactor = calculateScaleFactor(
                    originalWidth = options.outWidth,
                    originalHeight = options.outHeight,
                    targetSize = MAX_IMAGE_DIMENSION
                )

                // Now load the scaled down image
                val bitmap = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inSampleSize = scaleFactor
                }.let { scaledOptions ->
                    BitmapFactory.decodeFile(photoPath, scaledOptions)
                } ?: throw IllegalStateException("Failed to load image")

                // Resize the bitmap if it's still too large
                val resizedBitmap = resizeBitmapIfNeeded(bitmap, MAX_IMAGE_DIMENSION)

                // Convert to byte array with compression
                byteArrayOutputStream = ByteArrayOutputStream()
                resizedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    COMPRESSION_QUALITY,
                    byteArrayOutputStream
                )

                // Create base64 string with proper header
                val imageBytes = byteArrayOutputStream.toByteArray()
                val base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                "data:image/jpeg;base64,$base64String"

            } finally {
                // Clean up resources
                byteArrayOutputStream?.close()
            }
        }
    }

    private fun calculateScaleFactor(
        originalWidth: Int,
        originalHeight: Int,
        targetSize: Int
    ): Int {
        // Calculate the smallest power of 2 that will result in dimensions <= targetSize
        var scaleFactor = 1
        while (originalWidth / (scaleFactor * 2) >= targetSize ||
            originalHeight / (scaleFactor * 2) >= targetSize) {
            scaleFactor *= 2
        }
        return scaleFactor
    }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // If the bitmap is already small enough, return it as is
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        // Calculate new dimensions while maintaining aspect ratio
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }


    private fun handleSuccessResponse(response: IAResponse) {
        // Instead of passing a PlantFilterParams object, pass individual values
        val catalogFragment = CatalogFragment().apply {
            arguments = Bundle().apply {
                putInt("environment", response.data.idEntorno)
                putBoolean("pet_friendly", response.data.petFriendly)
                putInt("lighting", response.data.idIluminacion)
                putInt("temperature", response.data.idToleranciaTemperatura)
                putInt("irrigation", response.data.idTipoRiego)
                putString("size", response.data.sizePlant)
                putString("ai_explanation", response.message)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, catalogFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleError(error: Exception) {
        val errorMessage = when (error) {
            is IllegalStateException -> "Error loading image: ${error.message}"
            else -> "Image too large to process. Please try a smaller image."
        }

        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    // Show loading state with progress indicator
    private fun showLoading(show: Boolean) {
        view?.findViewById<ProgressBar>(R.id.loading_indicator)?.visibility =
            if (show) View.VISIBLE else View.GONE
        nextButton.isEnabled = !show
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
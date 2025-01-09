package com.cotiledon.mobilApp.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.cotiledon.mobilApp.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IAMainFragment : Fragment() {

    private lateinit var checkBox: CheckBox
    private lateinit var continueButton: Button

    // Camera constants and variables
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }

    private var photoFile: File? = null
    private var currentPhotoPath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ia_main, container, false)

        // Initialize views
        checkBox = view.findViewById(R.id.ia_checkBox)
        continueButton = view.findViewById(R.id.ia_continue_button)

        // Set up button click listener
        continueButton.setOnClickListener {
            if (checkBox.isChecked) {
                checkCameraPermissionAndOpen()
            } else {
                Toast.makeText(requireContext(), "Please accept the permission first", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Function to check camera permission and open camera
    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    // Create a file to store the image
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name with timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Get the directory for storing images
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create the file
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // Function to open camera
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.cotiledon.mobilApp.fileprovider", // Update this to match your manifest
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    // Handle permission result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        // Show dialog explaining why we need camera permission
                        AlertDialog.Builder(requireContext())
                            .setTitle("Camera Permission Needed")
                            .setMessage("We need camera access to take pictures of plants for identification.")
                            .setPositiveButton("Grant Permission") { _, _ ->
                                requestPermissions(
                                    arrayOf(Manifest.permission.CAMERA),
                                    CAMERA_PERMISSION_CODE
                                )
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Camera permission is required to use this feature",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .create()
                            .show()
                    } else {
                        // Permission permanently denied, direct user to settings
                        AlertDialog.Builder(requireContext())
                            .setTitle("Permission Required")
                            .setMessage("Camera permission has been denied. Please enable it in app settings to use this feature.")
                            .setPositiveButton("Go to Settings") { _, _ ->
                                // Open app settings
                                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", requireActivity().packageName, null)
                                })
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
    }

    // Handle camera result
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Show dialog to confirm or retake picture
            showImageConfirmationDialog()
        }
    }

    // Dialog to confirm or retake picture
    private fun showImageConfirmationDialog() {
        // Get file size
        val fileSize = photoFile?.length() ?: 0L
        val fileSizeMB = bytesToMB(fileSize)

        // Log the file size
        Log.d("ImageCapture", "Image size: ${String.format("%.2f", fileSizeMB)} MB")

        AlertDialog.Builder(requireContext())
            .setTitle("Photo Captured")
            .setMessage("Photo size: ${String.format("%.2f", fileSizeMB)} MB\n\nWould you like to use this photo or take another one?")
            .setPositiveButton("Use Photo") { dialog, _ ->
                dialog.dismiss()
                navigateToPromptFragment()
            }
            .setNegativeButton("Retake") { dialog, _ ->
                dialog.dismiss()
                openCamera()
            }
            .show()
    }

    private fun bytesToMB(bytes: Long): Double {
        return bytes.toDouble() / (1024 * 1024)
    }

    // Navigate to prompt fragment
    private fun navigateToPromptFragment() {
        // Create bundle with photo path
        val bundle = Bundle().apply {
            putString("photo_path", currentPhotoPath)
        }

        // Create and setup prompt fragment
        val promptFragment = IAPromptFragment().apply {
            arguments = bundle
        }

        // Perform fragment transaction
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, promptFragment)
            .addToBackStack(null)
            .commit()
    }
}
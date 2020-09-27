package com.example.anlosia.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.MainActivity
import com.example.anlosia.R
import com.example.anlosia.model.FaceRecognitionResponse
import com.example.anlosia.model.Location
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_camera_presence.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraPresenceActivity : AppCompatActivity() {
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var sharedPref : SharedPreferences

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var cameraPresenceViewModel = CameraPresenceViewModel()
    private lateinit var client: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_presence)

        client = LocationServices.getFusedLocationProviderClient(applicationContext)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //Get shared pref instance
        sharedPref = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

        val id_user = sharedPref.getInt("id", 0)
        val id_company = sharedPref.getInt("id_company", 0)
        val date_presence = SimpleDateFormat("YYYY-MM-dd").format(Date().time)
        val start_presence = SimpleDateFormat("HH:mm:ss").format(Date().time)

        val faceRecognitionObserver = Observer<FaceRecognitionResponse> {
            it?.let {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    client.lastLocation.addOnSuccessListener {
                        val loc = Location(it.latitude, it.longitude)
                        var polygon: Array<Array<Double>?> = arrayOf()
                        val sharedPreferences =
                            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                        var companyLocation = sharedPreferences.all["location"].toString()
                        companyLocation = companyLocation.replace("],[", "|")
                        companyLocation = companyLocation.replace("[[", "")
                        companyLocation = companyLocation.replace("]]", "")
                        var n = 0
                        companyLocation.split("|").forEach {
                            var latlng = it.split(",")
                            polygon = Util.append(
                                polygon,
                                arrayOf(latlng[0].toDouble(), latlng[1].toDouble())
                            )
                        }
                        val isInside = Util.isInsidePolygon(loc, polygon)
                        Util.logD(isInside.toString())
                    }
                }
                cameraPresenceViewModel.postPresenceStart(id_user, id_company, date_presence, start_presence)
            }
        }

        val presenceStartObserver = Observer<PresenceResponse> {
            it?.let {
                val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
                with (sharedPreferences.edit()) {
                    putInt("id_presence", it.id)
                    putInt("is_presenced", 1)

                    apply()
                }

                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        //Initiate view model
        cameraPresenceViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[CameraPresenceViewModel::class.java]

        cameraPresenceViewModel.getFaceRecognitionResponse().observe(this, faceRecognitionObserver)
        cameraPresenceViewModel.getPresenceStart().observe(this, presenceStartObserver)

        // Setup the listener for take photo button
        camera_capture_button.setOnClickListener {
            takePhoto()
        }
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .setTargetResolution(Size(500, 500))
                .build()

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(500, 500))
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture =  imageCapture ?: return

        Log.d(TAG, "Not returning file")

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    cameraPresenceViewModel.postUploadFile(photoFile)
                }
            })
    }

    private fun allPermissionsGranted() = false

    fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.d(TAG, "Permisssion granted")
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permission not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}   
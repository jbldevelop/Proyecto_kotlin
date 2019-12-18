package mx.jbl.ejemplo1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class MainActivityCam : AppCompatActivity() , LifecycleOwner{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cam)

        viewFinder = findViewById(R.id.view_finder)

        // Request camera permissions
        if(allPermissionsGranted()){
            viewFinder.post{startCamera()}
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener{_,_,_,_,_,_,_,_,_ ->
            updateTransform()
        }

    }


    private val executor = Executors.newSingleThreadExecutor()
    private lateinit  var viewFinder: TextureView

    private fun updateTransform(){
        val matrix =  Matrix()
        // compute the center of the view finder
        val centerX = viewFinder.width /2f
        val centerY = viewFinder.height /2f

        val rotationDegrees = when(viewFinder.display.rotation){
            Surface.ROTATION_0   -> 0
            Surface.ROTATION_90  -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(),centerX,centerY)

        //Finally , applu transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun startCamera(){
        val previewConfig = PreviewConfig.Builder().apply{
            setTargetResolution(Size(640,640))
        }.build()

        // Build the  viewFinder use case
        val preview = Preview(previewConfig)
        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener{
            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder,0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }



        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        //Build the  image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        findViewById<ImageButton>(R.id.capture_button).setOnClickListener{
            val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file,executor,
                object : ImageCapture.OnImageSavedListener{
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        cause: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp",msg, cause)
                        viewFinder.post{
                            Toast.makeText(baseContext,msg,Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeded : ${file.absolutePath}"
                        Log.d("CameraXApp",msg)
                        viewFinder.post{
                            Toast.makeText(baseContext, msg,Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            )
        }

        val analysisConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode( ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        val analyzerUseCase = ImageAnalysis(analysisConfig).apply {
            setAnalyzer(executor,LuminosityAnalyzer())
        }

        CameraX.bindToLifecycle(this, preview, imageCapture,analyzerUseCase)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                viewFinder.post{startCamera()}
            }else{
                Toast.makeText(this, "Permissions not granted by user", Toast.LENGTH_LONG).show()
                finish()
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it ) == PackageManager.PERMISSION_GRANTED
    }



    private class LuminosityAnalyzer : ImageAnalysis.Analyzer{

        private var lastAnalyzedTimestamp = 0L


        private fun ByteBuffer.toByteArray(): ByteArray{
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy, rotationDegrees: Int) {

            val currentTimestamp = System.currentTimeMillis()

            if(currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)){
                val buffer = image.planes[0].buffer
                val data = buffer.toByteArray()
                val pixels = data.map {it.toInt() and 0xFF}
                val luma = pixels.average()

                lastAnalyzedTimestamp = currentTimestamp
            }
        }


    }



}





package eu.acolombo.plukke.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import eu.acolombo.plukke.example.utils.CodeSequenceBuilder
import eu.acolombo.plukke.example.utils.CodeSequenceBuilder.Code.Highlight
import eu.acolombo.plukke.example.utils.CodeSequenceBuilder.Code.Keyword.Extension
import eu.acolombo.plukke.example.utils.CodeSequenceBuilder.Code.Keyword.Variable
import eu.acolombo.plukke.example.utils.load
import eu.acolombo.plukke.pickImage
import eu.acolombo.plukke.takePicture
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity(R.layout.activity_demo) {

    private val viewModel: DemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Choose between taking a photo or picking from gallery using PlukkeResultContracts.Choose
        buttonPicker.setOnClickListener {
            pickImage { uri ->
                viewModel.savePicker(uri) // Do your own thing here instead
            }
        }

        // Taking a photo using ActivityResultContracts.TakePicture without the need of a FileProvider
        buttonCamera.setOnClickListener {
            takePicture { uri ->
                viewModel.saveCamera(uri) // Do your own thing here instead
            }
        }

        // Examples are on multiple lines for clarity, but they lend themselves pretty well to one-liners:
        // i.e. buttonPicker.setOnClickListener { pickImage { uri -> imagePicker.load(uri) } }
        setupDemo()
    }

    private fun setupDemo() {
        // Using a crude implementation of ViewModel just to retain state on configuration change
        // The resulting uri from the pickers gets saved in the vm and observed below
        // On change the uri gets loaded in the target image using Glide
        viewModel.pickerResult.observe(this, Observer { uri ->
            imagePicker.load(uri)
            imagePicker.alpha = 1f
        })
        viewModel.cameraResult.observe(this, Observer { uri ->
            imageCamera.load(uri)
            imageCamera.alpha = 1f
        })

        buttonCamera.setOnLongClickListener {
            imageCamera.animate().alpha(0.0f).withEndAction { viewModel.clearCamera() }
            true
        }
        buttonPicker.setOnLongClickListener {
            imagePicker.animate().alpha(0.0f).withEndAction { viewModel.clearPicker() }
            true
        }

        setupDemoCodePreview()
    }

    private fun setupDemoCodePreview() = mapOf(
        textPicker to Triple(
            ::buttonPicker.name,
            ComponentActivity::pickImage.name,
            DemoViewModel::savePicker.name
        ),
        textCamera to Triple(
            ::buttonCamera.name,
            ComponentActivity::takePicture.name,
            DemoViewModel::saveCamera.name
        )
    ).forEach {
        it.key.text = CodeSequenceBuilder(this)
            .append(it.value.first + ".setOnClickListener {")
            .indent(Extension(it.value.second), " { ", Highlight("uri"), " ->")
            .indent(Variable("viewModel"), ".${it.value.third}(", Highlight("uri"), ")")
            .recess("}")
            .recess("}")
            .build()
    }

}

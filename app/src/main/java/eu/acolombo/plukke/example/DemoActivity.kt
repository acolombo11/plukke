package eu.acolombo.plukke.example

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import eu.acolombo.plukke.example.utils.CodeBuilder
import eu.acolombo.plukke.example.utils.CodeBuilder.Code.Highlight
import eu.acolombo.plukke.example.utils.CodeBuilder.Code.Keyword.Extension
import eu.acolombo.plukke.example.utils.CodeBuilder.Code.Keyword.Variable
import eu.acolombo.plukke.pickImage
import eu.acolombo.plukke.takePicture
import kotlinx.android.synthetic.main.activity_demo.*


class DemoActivity : AppCompatActivity(R.layout.activity_demo) {

    private val viewModel: DemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Choose between taking a photo or picking from gallery using Plukke's ActivityResultContracts.Choose
        buttonPicker.setOnClickListener {
            pickImage { uri ->
                viewModel.savePickerResult(uri) // Do your own thing here instead
            }
        }

        // Taking a photo using Ktx's ActivityResultContracts.TakePicture without the need of a FileProvider
        buttonCamera.setOnClickListener {
            takePicture { uri ->
                viewModel.saveCameraResult(uri) // Do your own thing here instead
            }
        }

        // Examples are on multiple lines for clarity, but they lend themselves pretty well to one-liners:
        // i.e. cardPicker.setOnClickListener { pickImage { uri -> imagePicker.load(uri) } }
        setupDemo()
    }

    // Using a crude implementation of ViewModel just to retain state on configuration change
    // Using a viewModel might be the best choice for yourself too, but make sure you don't copy this implementation
    // It's as simple as it gets just to keep the examples short
    class DemoViewModel : ViewModel() {
        val picker = MutableLiveData<Uri>()
        val camera = MutableLiveData<Uri>()

        fun savePickerResult(uri: Uri) { picker.value = uri }
        fun saveCameraResult(uri: Uri) { picker.value = uri }
    }

    private fun setupDemo() {
        viewModel.picker.observe(this, Observer { uri -> imagePicker.load(uri) })
        viewModel.camera.observe(this, Observer { uri -> imageCamera.load(uri) })
        setupDemoCodePreview()
    }

    private fun setupDemoCodePreview() {
        mapOf<TextView, Triple<String, String, String>>(
            textPicker to Triple(::buttonPicker.name, ComponentActivity::pickImage.name, ::imageCamera.name),
            textCamera to Triple(::buttonCamera.name, ComponentActivity::takePicture.name, ::imagePicker.name)
        ).forEach {
            it.key.text = CodeBuilder(this).apply {
                append(it.value.first + ".setOnClickListener {")
                indent(Extension(it.value.second), " { ", Highlight("uri"), " ->")
                indent(Variable(it.value.third), ".load(", Highlight("uri"), ")")
                recess("}")
                recess("}")
            }.build()
        }
    }

    private fun ImageView.load(uri: Uri) {
        Glide.with(this).load(uri).into(imagePicker)
    }

}

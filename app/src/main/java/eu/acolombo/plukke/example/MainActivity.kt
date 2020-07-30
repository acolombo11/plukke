package eu.acolombo.plukke.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import eu.acolombo.plukke.pickImage
import eu.acolombo.plukke.takePicture
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Choose between taking a photo or picking from gallery using library's ActivityResultContracts.Choose
        cardPicker.setOnClickListener { pickImage { viewModel.picker.postValue(it) } }

        // Taking a photo using system's ActivityResultContracts.TakePicture without the need of FileProvider
        cardCamera.setOnClickListener { takePicture { viewModel.camera.postValue(it) } }

        // Using a crude implementation of ViewModel just to retain state on configuration change
        viewModel.picker.observe(this, Observer { imagePicker.load(it) })
        viewModel.camera.observe(this, Observer { imageCamera.load(it) })

    }

    class MainViewModel : ViewModel() {
        var picker = MutableLiveData<Any?>()
        var camera = MutableLiveData<Uri>()
    }

}

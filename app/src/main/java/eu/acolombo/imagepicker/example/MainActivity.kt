package eu.acolombo.imagepicker.example

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import eu.acolombo.imagepicker.pickImage
import eu.acolombo.imagepicker.takePicture
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Choose between taking a photo or picking from gallery using library's ActivityResultContracts.Choose
        buttonChoose.setOnClickListener { pickImage { viewModel.image.postValue(it) } }

        // Taking a photo using system's ActivityResultContracts.TakePicture without the need of FileProvider
        buttonTakePicture.setOnClickListener { takePicture { viewModel.photo.postValue(it) } }

        // Using a crude implementation of ViewModel just to retain state on configuration change
        // You can directly load the images on results but remember to manage the lifecycle
        viewModel.image.observe(this, Observer { imageChoose.load(it) })
        viewModel.photo.observe(this, Observer { imageTakePicture.load(it) })

    }

    class MainViewModel : ViewModel() {
        var image = MutableLiveData<Any?>()
        var photo = MutableLiveData<Uri>()
    }

    private fun ImageView.load(uri: Uri) {
        Glide.with(context).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
    }

    private fun ImageView.load(any: Any?) {
        Glide.with(context).load(any).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
    }

}

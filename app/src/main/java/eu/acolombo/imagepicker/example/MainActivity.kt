package eu.acolombo.imagepicker.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.acolombo.imagepicker.ImagePicker
import eu.acolombo.imagepicker.pick
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonPhoto.setOnClickListener { pick(ImagePicker.Photo(this)) { image.load(it) } }

        buttonCapture.setOnClickListener { pick(ImagePicker.Capture) { image.load(it) } }

        buttonGallery.setOnClickListener { pick(ImagePicker.Pick) { image.load(it) } }

        buttonContent.setOnClickListener { pick(ImagePicker.Content) { image.load(it) } }

        buttonMultiple.setOnClickListener { pick(ImagePicker.MultipleContent) { image.load(it.getOrNull(1) ?: it.first()) } }

        buttonPicker.setOnClickListener { pick(ImagePicker.Picker()) { image.load(it.first()) } }

    }

}

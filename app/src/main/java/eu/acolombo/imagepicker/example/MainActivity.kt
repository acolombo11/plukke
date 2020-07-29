package eu.acolombo.imagepicker.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.acolombo.imagepicker.pickImage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonPhoto.setOnClickListener { pickImage { image.load(it) } }

    }

}

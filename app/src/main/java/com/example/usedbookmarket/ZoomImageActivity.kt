package com.example.usedbookmarket

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ZoomImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)

        val formImage = intent.getStringExtra("formImage")
        val imageView= findViewById<ImageView>(R.id.zoom_imageView)

        Glide
            .with(this)
            .load(formImage)
            .into(imageView)

    }
}
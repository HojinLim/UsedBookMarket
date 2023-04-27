package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class ZoomInSlider: AppCompatActivity() {
    lateinit var sliderViewPager: ViewPager2
    lateinit var layoutIndicator: LinearLayout

private lateinit var images: Array<String?>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_in_slider)

        val formModel: ArticleForm = intent.getParcelableExtra("formModel")!!

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userIdSt = currentUser?.email
        val statusImageView= findViewById<ImageView>(R.id.zoom_status_image)
        bindStatus(statusImageView, "load")
        val aid= formModel.aid

        val size= intent.getIntExtra("photos",5)
        images = arrayOfNulls(size)

        // 이미지 폴더 경로 참조
        val listRef = FirebaseStorage.getInstance().reference
            .child("userImages/formPhotos/$userIdSt/$aid")

        // listAll(): 폴더 내의 모든 이미지를 가져오는 함수

        listRef.listAll()
            .addOnSuccessListener { list ->
                images = arrayOfNulls<String>(list.items.size)
                for ((i, item) in list.items.withIndex()) {

                    // reference의 item(이미지) url 받아오기
                    item.downloadUrl.addOnSuccessListener {
                        images[i] = (it.toString())


                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "hello there", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        Handler(Looper.getMainLooper()).postDelayed({
            //실행할 코드
            sliderViewPager = findViewById(R.id.sliderViewPager)
            layoutIndicator = findViewById(R.id.layoutIndicators)

            sliderViewPager.offscreenPageLimit = 1
            sliderViewPager.adapter = ImageSliderAdapter(this, images)
            sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })
            setupIndicators(images.size)
            bindStatus(statusImageView,"stop")
        }, 1500)
    }
    private fun bindStatus(
        statusImageView: ImageView, status: String
    ) {
        when(status){
            "load"->{
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.loading_animation)
            }
            "stop"->{
                statusImageView.visibility = View.GONE
            }
        }
    }

    private fun setupIndicators(count: Int) {
        val indicators: Array<ImageView?> = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.bg_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = params
            layoutIndicator.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = layoutIndicator.childCount
        for (i in 0 until childCount) {
            val imageView: ImageView = layoutIndicator.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                    )
                )
            }
        }
    }

    inner class ImageSliderAdapter(context: Context, sliderImage: Array<String?>) :
        RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {
        private val context: Context
        private val sliderImage: Array<String?>

        init {
            this.context = context
            this.sliderImage = sliderImage
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slider, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindSliderImage(sliderImage[position])
        }

        override fun getItemCount(): Int {
            return sliderImage.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mImageView: ImageView

            init {
                mImageView = itemView.findViewById(R.id.imageSlider)

            }

            fun bindSliderImage(imageURL: String?) {
                Glide.with(context)
                    .load(imageURL)
                    .into(mImageView)
            }
        }
    }

}


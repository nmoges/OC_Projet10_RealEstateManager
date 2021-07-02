package com.openclassrooms.realestatemanager.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.model.Photo
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

class MediaDisplayHandler {

    companion object {

        /**
         * [Int] extension.
         */
        private fun Int.toPx(activity: MainActivity) =
            this * activity.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

        /**
         * Handles the [ImageView] creation containing a new photo.
         */
        private fun createNewImageView(uri: Uri, mainActivity: MainActivity): ImageView {
            var bitmap: Bitmap
            val imageView: ImageView = ImageView(mainActivity)
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            val activity: MainActivity = mainActivity

            params.setMargins(0.toPx(activity), 0, 20.toPx(activity), 20.toPx(activity))
            imageView.layoutParams = params
            imageView.layoutParams.width = 150.toPx(activity)
            imageView.layoutParams.height = 150.toPx(activity)
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            }
            else {
                activity.contentResolver?.let {
                    val source = ImageDecoder.createSource(it, uri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(bitmap)
                }
            }
            return imageView
        }

        /**
         * Handles the banner [View] creation displayed.
         */
        private fun createNewBackgroundView(mainActivity: MainActivity): View {
            val view: View = View(mainActivity)
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0.toPx(mainActivity), 20.toPx(mainActivity),
                             20.toPx(mainActivity), 20.toPx(mainActivity))
            params.gravity = Gravity.BOTTOM
            view.layoutParams = params
            view.layoutParams.width = 150.toPx(mainActivity)
            view.layoutParams.height = 50.toPx(mainActivity)
            view.setBackgroundColor(Color.parseColor("#77666666"))
            return view
        }

        /**
         * Handles the [TextView] creation displaying the name of the associated displayed
         * photo.
         */
        private fun createText(name: String?, mainActivity: MainActivity): TextView {
            val textView: TextView = TextView(mainActivity)
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(20.toPx(mainActivity), 0.toPx(mainActivity),
                              0.toPx(mainActivity), 40.toPx(mainActivity))
            params.gravity = Gravity.BOTTOM
            textView.layoutParams = params
            textView.text = name
            textView.setTextColor(Color.parseColor("#FFFFFF"))
            textView.textSize = 12.0F
            return textView
        }

        /**
         * Handles the [FrameLayout] creation to contain a new photo.
         */
        fun createNewFrameLayout(photo: Photo, activity: MainActivity): FrameLayout {
            // Set image
            val imageView: ImageView = createNewImageView(photo.photoUri.toUri(), activity)

            // Set translucent view
            val view: View = createNewBackgroundView(activity)

            // Set text
            val text: TextView = createText(photo.name, activity)
            val frameLayout: FrameLayout = FrameLayout(activity)
            frameLayout.addView(imageView)
            frameLayout.addView(view)
            frameLayout.addView(text)

            return frameLayout
        }
    }
}
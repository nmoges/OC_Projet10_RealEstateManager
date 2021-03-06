package com.openclassrooms.realestatemanager.ui

import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.openclassrooms.data.model.Photo
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

/**
 * Class defined to handle display of new FrameLayouts containing selected estate photos.
 */
class MediaDisplayHandler {

    companion object {
        /**
         * [Int] extension.
         */
        private fun Int.toPx(activity: MainActivity) =
            this * activity.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

        /**
         * Handles the [ImageView] creation containing a new photo.
         * @param photoUri : uri of a photo
         * @param mainActivity : parent activity
         * @return ImageView : image to display
         */
        private fun createNewImageView(photoUri: Uri, mainActivity: MainActivity): ImageView {
            val imageView = ImageView(mainActivity)
            // Defines FrameLayout parameters
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0.toPx(mainActivity), 0, 20.toPx(mainActivity), 20.toPx(mainActivity))
            // Defines ImageView parameters
            imageView.layoutParams = params
            imageView.layoutParams.width = 150.toPx(mainActivity)
            imageView.layoutParams.height = 150.toPx(mainActivity)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            // Display image
            Glide.with(mainActivity)
                .load(photoUri)
                .centerCrop()
                .override(imageView.width, imageView.height)
                .into(imageView)
            return imageView
        }

        /**
         * Handles the banner [View] creation displayed.
         * @param mainActivity : parent activity
         * @return View : background view
         */
        private fun createNewBackgroundView(mainActivity: MainActivity): View {
            val view = View(mainActivity)
            // Defines FrameLayout parameters
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0.toPx(mainActivity), 20.toPx(mainActivity),
                             20.toPx(mainActivity), 20.toPx(mainActivity))
            params.gravity = Gravity.BOTTOM
            // Define view parameters
            view.layoutParams = params
            view.layoutParams.width = 150.toPx(mainActivity)
            view.layoutParams.height = 50.toPx(mainActivity)
            view.setBackgroundColor(Color.parseColor("#77666666"))
            return view
        }

        /**
         * Handles the [TextView] creation displaying the name of the associated displayed
         * photo.
         * @param name : photo name
         * @param mainActivity : parent activity
         * @return TextView : textview to display
         */
        private fun createText(name: String?, mainActivity: MainActivity): TextView {
            val textView = TextView(mainActivity)
            // Defines FrameLayout parameters
            val params: FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(20.toPx(mainActivity), 0.toPx(mainActivity),
                              0.toPx(mainActivity), 40.toPx(mainActivity))
            params.gravity = Gravity.BOTTOM
            // Define textView parameters
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
            val imageView: ImageView = createNewImageView(photo.uriConverted.toUri(), activity)
            // Set translucent view
            val view: View = createNewBackgroundView(activity)
            // Set text
            val text: TextView = createText(photo.name, activity)
            val frameLayout = FrameLayout(activity)
            frameLayout.addView(imageView)
            frameLayout.addView(view)
            frameLayout.addView(text)
            return frameLayout
        }
    }
}
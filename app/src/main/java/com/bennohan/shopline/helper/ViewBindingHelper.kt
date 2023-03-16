package com.bennohan.shopline.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bennohan.shopline.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            imageUrl?.let {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(view)


            }

        }
        @JvmStatic
        @BindingAdapter(value = ["imageUrlCircle"], requireAll = false)
        fun loadImageRecipeCircle(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            imageUrl?.let {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(view)


            }

        }

    }
}
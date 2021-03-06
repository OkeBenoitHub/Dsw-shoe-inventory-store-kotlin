package com.www.dswstore.util

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.balysv.materialripple.MaterialRippleLayout
import com.www.dswstore.R
import com.www.dswstore.database.Shoe
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/*
@BindingAdapter("shoeImageCircular")
fun setShoeImageCircular(view: CircleImageView, item: Shoe?) {
    item?.let {
        PhotoUtil().loadPhotoFileWithGlide(
            view.context, item.picture_path,
            null, view,
            R.drawable.shoe_128
        )
    }
}*/

@BindingAdapter("shoeImageCircular")
fun setShoeImageCircular(view: CircleImageView, imagePath: String?) {
    imagePath?.let {
        PhotoUtil().loadPhotoFileWithGlide(
            view.context, imagePath,
            null, view,
            R.drawable.shoe_128
        )
    }
}


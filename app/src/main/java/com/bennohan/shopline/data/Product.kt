package com.bennohan.shopline.data


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @Expose
    @SerializedName("active")
    val active: String?,
    @Expose
    @SerializedName("category_id")
    val categoryId: Int?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("description")
    val description: String?,
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("image")
    val image: String?,
    @Expose
    @SerializedName("name")
    val nameProduct: String?,
    @Expose
    @SerializedName("qty")
    val totalProduct: Int?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
    @Expose
    @SerializedName("total_cost")
    val totalCost: String?,
    @SerializedName("rating")
    val rating: Float?,
    @SerializedName("sizes")
    val sizes: List<Sizes?>?,
    @SerializedName("image_sliders")
    val imageSliders: List<ImageSlide>?,
    @SerializedName("variants")
    val variants: List<Variant?>?,
    @SerializedName("product")
    val productt: Productt?

) : Parcelable {
    @Parcelize
    data class Variant(
        @Expose
        @SerializedName("id")
        val id: Int?,
        @Expose
        @SerializedName("product_id")
        val productId: Int?,
        @Expose
        @SerializedName("name")
        val colorName: String?,
        @Expose
        @SerializedName("image")
        val image: String?,
        @Expose
        @SerializedName("created_at")
        val createdAt: String?,
        @Expose
        @SerializedName("updated_at")
        val updatedAt: String?,
        var selected: Boolean
    ) : Parcelable

    @Parcelize
    data class Sizes(
        @Expose
        @SerializedName("id")
        val id: Int?,
        @Expose
        @SerializedName("variant_id")
        val variantId: Int?,
        @Expose
        @SerializedName("size")
        val size: String?,
        @Expose
        @SerializedName("stock")
        val stock: String?,
        @Expose
        @SerializedName("price")
        val price: String?,
        @Expose
        @SerializedName("created_at")
        val createdAt: String?,
        @Expose
        @SerializedName("updated_at")
        val updatedAt: String?,
        var selected: Boolean
    ) : Parcelable

    @Parcelize
    data class Productt(
        @Expose
        @SerializedName("active")
        val active: String?,
        @Expose
        @SerializedName("category_id")
        val categoryId: Int?,
        @Expose
        @SerializedName("created_at")
        val createdAt: String?,
        @Expose
        @SerializedName("description")
        val description: String?,
        @Expose
        @SerializedName("id")
        val id: Int?,
        @Expose
        @SerializedName("image")
        val image: String?,
        @Expose
        @SerializedName("name")
        val nameProduct: String?,
        @Expose
        @SerializedName("updated_at")
        val updatedAt: String?,
    ) : Parcelable

}





package com.bennohan.shopline.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey
    @Expose
    @SerializedName("id_room")
    val idRoom: Int,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("image")
    val image: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)
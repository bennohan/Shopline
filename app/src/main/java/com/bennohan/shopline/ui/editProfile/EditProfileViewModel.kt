package com.bennohan.shopline.ui.editProfile

import androidx.lifecycle.viewModelScope
import com.bennohan.shopline.api.ApiService
import com.bennohan.shopline.base.BaseViewModel
import com.bennohan.shopline.data.User
import com.bennohan.shopline.data.room.UserDao
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
//    private val session: Session,
    private val userDao: UserDao
) : BaseViewModel() {

    //Function Update Profile
    fun updateUser(name: String, phone: String) = viewModelScope.launch {
        ApiObserver({ apiService.updateProfile(name, phone) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess())
                    userDao.insert(data.copy(idRoom = 1))
//                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

    //Function Update Photo Profile
    fun updateUserPhoto(name: String, phone: String, photo: File) = viewModelScope.launch {
        val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("image", photo.name, fileBody)
        ApiObserver({ apiService.updateProfilePhoto(name, phone, filePart) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess("Profile Picture Updated"))
                    userDao.insert(data.copy(idRoom = 1))
//                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())
                }

            })
    }

}
package com.bennohan.shopline.ui.login

import androidx.lifecycle.viewModelScope
import com.bennohan.shopline.api.ApiService
import com.bennohan.shopline.base.BaseViewModel
import com.bennohan.shopline.data.Cons
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.data.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
) : BaseViewModel() {

    //Login Function
    fun login(
        phone: String,
        password: String,
    ) = viewModelScope.launch {
        _apiResponse.send(ApiResponse().responseLoading())
        ApiObserver({ apiService.login(phone, password) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val token = response.getString("token")
                    session.setValue(Cons.TOKEN.API_TOKEN,token)
                    _apiResponse.send(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

}
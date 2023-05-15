package com.bennohan.shopline.ui.profile

import androidx.lifecycle.viewModelScope
import com.bennohan.shopline.api.ApiService
import com.bennohan.shopline.base.BaseViewModel
import com.bennohan.shopline.data.Session
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
) : BaseViewModel() {

    // logout
    fun logout() = viewModelScope.launch {
        ApiObserver({ apiService.logout() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    session.clearAll()
                    _apiResponse.send(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())
                }
            })
    }
}
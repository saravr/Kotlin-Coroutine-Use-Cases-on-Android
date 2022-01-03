package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.callbacks

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SequentialNetworkRequestsCallbacksViewModel(
    private val mockApi: CallbackMockApi = mockApi()
) : BaseViewModel<UiState>() {
    private var getAndroidVersionsCall: Call<List<AndroidVersion>>? = null
    private var getAndroidVersionFeatures: Call<VersionFeatures>? = null

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading
        getAndroidVersionsCall = mockApi.getRecentAndroidVersions()
        getAndroidVersionsCall?.enqueue(object: Callback<List<AndroidVersion>> {
            override fun onResponse(
                call: Call<List<AndroidVersion>>,
                response: Response<List<AndroidVersion>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.last()?.let {
                        getAndroidVersionFeatures = mockApi.getAndroidVersionFeatures(it.apiLevel)
                        getAndroidVersionFeatures?.enqueue(object : Callback<VersionFeatures> {
                            override fun onResponse(
                                call: Call<VersionFeatures>,
                                response: Response<VersionFeatures>
                            ) {
                                if (response.isSuccessful) {
                                    response.body()?.let {
                                        uiState.value = UiState.Success(it)
                                    }
                                } else {
                                    uiState.value = UiState.Error("Get versions call failed: bad response")
                                }
                            }

                            override fun onFailure(call: Call<VersionFeatures>, t: Throwable) {
                                uiState.value = UiState.Error("Version feature call failed: ${t.message}")
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<List<AndroidVersion>>, t: Throwable) {
                uiState.value = UiState.Error("Get versions call failed: ${t.message}")
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        getAndroidVersionFeatures?.cancel()
        getAndroidVersionsCall?.cancel()
    }
}
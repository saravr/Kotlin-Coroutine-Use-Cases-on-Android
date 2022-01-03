package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import java.lang.Exception

class Perform2SequentialNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val recentVersions = mockApi.getRecentAndroidVersions()
                val versionFeatures =
                    mockApi.getAndroidVersionFeatures(recentVersions.last().apiLevel)
                uiState.value = UiState.Success(versionFeatures)
            } catch (exception: Exception) {
                uiState.value = UiState.Error(exception.message!!)
            }
        }
    }
}
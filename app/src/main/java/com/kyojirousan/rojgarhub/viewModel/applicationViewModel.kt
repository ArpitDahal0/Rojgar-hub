package com.kyojirousan.rojgarhub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyojirousan.rojgarhub.model.ApplicationModel
import com.kyojirousan.rojgarhub.repository.ApplicationRepository

class ApplicationViewModel(private val repository: ApplicationRepository) : ViewModel() {
    private val _applicationStatus = MutableLiveData<Resource<String>>()
    val applicationStatus: LiveData<Resource<String>> = _applicationStatus

    private val _applicationsList = MutableLiveData<List<ApplicationModel>>()
    val applicationsList: LiveData<List<ApplicationModel>> = _applicationsList

    fun submitApplication(application: ApplicationModel) {
        _applicationStatus.value = Resource.Loading()
        repository.submitApplication(application) { success, message ->
            if (success) {
                _applicationStatus.value = Resource.Success(message)
            } else {
                _applicationStatus.value = Resource.Error(message)
            }
        }
    }

    fun getApplicationsByUserId(userId: String) {
        repository.getApplicationsByUserId(userId) { applications, success, message ->
            if (success) {
                _applicationsList.value = applications
            } else {
                _applicationsList.value = emptyList()
            }
        }
    }

    fun getApplicationsForEmployer(employerId: String) {
        repository.getApplicationsForEmployer(employerId) { applications, success, message ->
            if (success) {
                _applicationsList.value = applications
            } else {
                _applicationsList.value = emptyList()
            }
        }
    }

    fun updateApplicationStatus(applicationId: String, status: String) {
        repository.updateApplicationStatus(applicationId, status) { success, message ->
            // Optionally handle update status
        }
    }

    sealed class Resource<T>(
        val data: T? = null,
        val message: String? = null
    ) {
        class Success<T>(data: T) : Resource<T>(data)
        class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
        class Loading<T> : Resource<T>()
    }
}

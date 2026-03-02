package com.kyojirousan.rojgarhub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyojirousan.rojgarhub.model.JobModel
import com.kyojirousan.rojgarhub.repository.JobRepository

class JobViewModel(private val repository: JobRepository) : ViewModel() {
    private val _jobs = MutableLiveData<List<JobModel>>()
    val jobs: LiveData<List<JobModel>> = _jobs

    private val _addJobStatus = MutableLiveData<Boolean>()
    val addJobStatus: LiveData<Boolean> = _addJobStatus

    fun addJob(jobModel: JobModel, callback: (Boolean, String?) -> Unit) {
        repository.addJob(jobModel) { success, message ->
            _addJobStatus.value = success
            callback(success, message)
        }
    }

    fun getAllJobs() {
        repository.getAllJobs { jobsList, success, message ->
            if (success) _jobs.value = jobsList
        }
    }

    fun getJobsByEmployer(employerId: String) {
        repository.getJobsByEmployer(employerId) { jobsList, success, message ->
            if (success) _jobs.value = jobsList
        }
    }

    fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit) {
        repository.getJobById(jobId, callback)
    }

    fun deleteJob(jobId: String) {
        repository.deleteJob(jobId) { success, message ->
            if (success) getAllJobs()
        }
    }
}

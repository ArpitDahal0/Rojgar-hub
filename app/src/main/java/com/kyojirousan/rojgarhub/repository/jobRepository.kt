package com.kyojirousan.rojgarhub.repository

import com.kyojirousan.rojgarhub.model.JobModel

interface JobRepository {
    fun addJob(jobModel: JobModel, callback: (Boolean, String) -> Unit)
    fun updateJob(jobId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit)
    fun getAllJobs(callback: (List<JobModel>, Boolean, String) -> Unit)
    fun getJobsByEmployer(employerId: String, callback: (List<JobModel>, Boolean, String) -> Unit)
    fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit)
    fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit)
}

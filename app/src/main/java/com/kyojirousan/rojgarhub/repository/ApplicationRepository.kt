package com.kyojirousan.rojgarhub.repository

import com.kyojirousan.rojgarhub.model.ApplicationModel

interface ApplicationRepository {
    fun submitApplication(application: ApplicationModel, callback: (Boolean, String) -> Unit)
    fun getApplicationsByUserId(userId: String, callback: (List<ApplicationModel>, Boolean, String) -> Unit)
    fun getApplicationsForEmployer(employerId: String, callback: (List<ApplicationModel>, Boolean, String) -> Unit)
    fun updateApplicationStatus(applicationId: String, status: String, callback: (Boolean, String) -> Unit)
}

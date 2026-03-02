package com.kyojirousan.rojgarhub.repository

import com.kyojirousan.rojgarhub.model.ApplicationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplicationRepositoryImpl : ApplicationRepository {
    private val database = FirebaseDatabase.getInstance()
    private val applicationsRef = database.reference.child("applications")

    override fun submitApplication(application: ApplicationModel, callback: (Boolean, String) -> Unit) {
        val applicationId = if (application.applicationId.isNullOrEmpty())
            applicationsRef.push().key ?: ""
        else
            application.applicationId

        val updatedApplication = application.copy(applicationId = applicationId)

        applicationsRef.child(applicationId).setValue(updatedApplication)
            .addOnSuccessListener {
                callback(true, "Application submitted successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to submit application")
            }
    }

    override fun updateApplicationStatus(
        applicationId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        applicationsRef.child(applicationId).child("status").setValue(status)
            .addOnSuccessListener {
                callback(true, "Application status updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update application status")
            }
    }

    override fun getApplicationsByUserId(
        userId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    ) {
        applicationsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val applicationsList = mutableListOf<ApplicationModel>()
                    for (appSnapshot in snapshot.children) {
                        appSnapshot.getValue(ApplicationModel::class.java)?.let {
                            applicationsList.add(it)
                        }
                    }
                    callback(applicationsList, true, "Applications fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }

    override fun getApplicationsForEmployer(
        employerId: String,
        callback: (List<ApplicationModel>, Boolean, String) -> Unit
    ) {
        applicationsRef.orderByChild("employerId").equalTo(employerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val applicationsList = mutableListOf<ApplicationModel>()
                    for (appSnapshot in snapshot.children) {
                        appSnapshot.getValue(ApplicationModel::class.java)?.let {
                            applicationsList.add(it)
                        }
                    }
                    callback(applicationsList, true, "Applications fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }
}

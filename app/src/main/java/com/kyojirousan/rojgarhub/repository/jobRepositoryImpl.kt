package com.kyojirousan.rojgarhub.repository

import com.kyojirousan.rojgarhub.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobRepositoryImpl : JobRepository {
    private val database = FirebaseDatabase.getInstance()
    private val jobsRef = database.reference.child("jobs")

    override fun addJob(jobModel: JobModel, callback: (Boolean, String) -> Unit) {
        val jobId = jobsRef.push().key ?: run {
            callback(false, "Couldn't get push key")
            return
        }
        jobModel.jobId = jobId
        jobsRef.child(jobId).setValue(jobModel)
            .addOnSuccessListener { callback(true, "Job added successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to add job") }
    }

    override fun getAllJobs(callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobs = snapshot.children.mapNotNull { it.getValue(JobModel::class.java) }
                callback(jobs, true, "Jobs fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }

    override fun getJobsByEmployer(employerId: String, callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsRef.orderByChild("employerId").equalTo(employerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = snapshot.children.mapNotNull { it.getValue(JobModel::class.java) }
                    callback(jobs, true, "Jobs fetched successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }

    override fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit) {
        jobsRef.child(jobId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val job = snapshot.getValue(JobModel::class.java)
                if (job != null) {
                    callback(job, true, "Job fetched successfully")
                } else {
                    callback(null, false, "Job not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit) {
        jobsRef.child(jobId).removeValue()
            .addOnSuccessListener { callback(true, "Job deleted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to delete job") }
    }
}

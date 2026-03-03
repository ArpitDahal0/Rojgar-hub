package com.kyojirousan.rojgarhub.repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kyojirousan.rojgarhub.model.JobModel
class JobRepositoryImpl : JobRepository {
    private val jobsRef = FirebaseDatabase.getInstance().reference.child("jobs")
    override fun addJob(jobModel: JobModel, callback: (Boolean, String) -> Unit) {
        val jobId = jobsRef.push().key ?: return callback(false, "Failed to generate ID")
        val finalJob = jobModel.copy(jobId = jobId)
        jobsRef.child(jobId).setValue(finalJob)
            .addOnSuccessListener { callback(true, "Job posted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to post job") }
    }
    override fun updateJob(jobId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        jobsRef.child(jobId).updateChildren(updates)
            .addOnSuccessListener { callback(true, "Job updated successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to update job") }
    }
    override fun getAllJobs(callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobs = snapshot.children.mapNotNull { it.getValue(JobModel::class.java) }
                callback(jobs, true, "Success")
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }
    override fun getJobsByEmployer(employerId: String, callback: (List<JobModel>, Boolean, String) -> Unit) {
        jobsRef.orderByChild("employerId").equalTo(employerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = snapshot.children.mapNotNull { it.getValue(JobModel::class.java) }
                    callback(jobs, true, "Success")
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), false, error.message)
                }
            })
    }
    override fun getJobById(jobId: String, callback: (JobModel?, Boolean, String) -> Unit) {
        jobsRef.child(jobId).get()
            .addOnSuccessListener { callback(it.getValue(JobModel::class.java), true, "Success") }
            .addOnFailureListener { callback(null, false, it.message ?: "Error") }
    }
    override fun deleteJob(jobId: String, callback: (Boolean, String) -> Unit) {
        jobsRef.child(jobId).removeValue()
            .addOnSuccessListener { callback(true, "Job deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to delete") }
    }
}

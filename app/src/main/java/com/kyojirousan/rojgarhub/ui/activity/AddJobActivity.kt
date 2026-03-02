package com.kyojirousan.rojgarhub.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kyojirousan.rojgarhub.databinding.ActivityAddJobBinding
import com.kyojirousan.rojgarhub.model.JobModel
import com.kyojirousan.rojgarhub.repository.JobRepositoryImpl
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.utils.LoadingUtils
import com.kyojirousan.rojgarhub.viewModel.JobViewModel
import com.kyojirousan.rojgarhub.viewModel.UserViewModel

class AddJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJobBinding
    private lateinit var jobViewModel: JobViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModels()
        setupViews()
    }

    private fun setupViewModels() {
        jobViewModel = JobViewModel(JobRepositoryImpl())
        userViewModel = UserViewModel(UserRepositoryImpl())
        loadingUtils = LoadingUtils(this)
    }

    private fun setupViews() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Post New Job"

        // Setup submit button
        binding.btnSubmitJob.setOnClickListener {
            submitJob()
        }
    }

    private fun submitJob() {
        val title = binding.etJobTitle.text.toString()
        val description = binding.etJobDescription.text.toString()
        val location = binding.etJobLocation.text.toString()
        val salary = binding.etJobSalary.text.toString()
        val requirements = binding.etJobRequirements.text.toString()

        if (validateInputs(title, description, location)) {
            loadingUtils.show()

            val currentUser = userViewModel.getCurrentUser()
            if (currentUser?.uid != null) {
                val jobModel = JobModel(
                    employerId = currentUser.uid,
                    title = title,
                    description = description,
                    location = location,
                    salary = salary,
                    requirements = requirements
                )

                jobViewModel.addJob(jobModel) { success, message ->
                    loadingUtils.dismiss()

                    Toast.makeText(this, message ?: "Job posted successfully", Toast.LENGTH_SHORT).show()

                    if (success) {
                        binding.root.postDelayed({
                            val resultIntent = Intent()
                            resultIntent.putExtra("stayOnJobs", true)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }, 500)
                    }
                }
            } else {
                loadingUtils.dismiss()
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(title: String, description: String, location: String): Boolean {
        if (title.isBlank()) {
            binding.etJobTitle.error = "Title is required"
            return false
        }

        if (description.isBlank()) {
            binding.etJobDescription.error = "Description is required"
            return false
        }

        if (location.isBlank()) {
            binding.etJobLocation.error = "Location is required"
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

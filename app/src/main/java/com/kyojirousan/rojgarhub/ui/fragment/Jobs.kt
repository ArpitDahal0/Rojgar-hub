package com.kyojirousan.rojgarhub.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyojirousan.rojgarhub.R
import com.kyojirousan.rojgarhub.adapter.JobAdapter
import com.kyojirousan.rojgarhub.databinding.FragmentJobsBinding
import com.kyojirousan.rojgarhub.model.JobModel
import com.kyojirousan.rojgarhub.model.UserModel
import com.kyojirousan.rojgarhub.repository.JobRepositoryImpl
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.ui.activity.AddJobActivity
import com.kyojirousan.rojgarhub.ui.activity.JobApplicationActivity
import com.kyojirousan.rojgarhub.ui.activity.JobDetailsActivity
import com.kyojirousan.rojgarhub.viewModel.JobViewModel
import com.kyojirousan.rojgarhub.viewModel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class Jobs : Fragment() {
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var jobsAdapter: JobAdapter
    private var currentUser: UserModel? = null

    private val addJobLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentUser?.let { loadJobs(it) }

            val stayOnJobs = result.data?.getBooleanExtra("stayOnJobs", false) ?: false
            if (stayOnJobs) {
                requireActivity().findViewById<BottomNavigationView>(R.id.buttomNavigation)?.selectedItemId =
                    R.id.menuJobs
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        setupObservers()
        setupClickListeners()
        observeUserAndLoadJobs()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        jobViewModel = JobViewModel(JobRepositoryImpl())
    }

    private fun setupObservers() {
        jobViewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            if (_binding != null) {
                requireActivity().runOnUiThread {
                    val sortedJobs = jobs.sortedByDescending { it.postedDate }
                    jobsAdapter.updateJobs(sortedJobs)
                    binding.jobsRecyclerView.visibility = View.VISIBLE
                    binding.progressBarJobs?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecyclerView(isEmployer: Boolean) {
        jobsAdapter = JobAdapter(
            jobs = emptyList(),
            onApplyClick = { job -> startApplicationProcess(job) },
            onDeleteClick = { job -> deleteJob(job) },
            isEmployer = isEmployer
        )
        binding.jobsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jobsAdapter
        }
    }

    private fun deleteJob(job: JobModel) {
        jobViewModel.deleteJob(job.jobId)
        Toast.makeText(requireContext(), "Job deletion requested", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListeners() {
        binding.fabAddJob.setOnClickListener {
            addJobLauncher.launch(Intent(requireContext(), AddJobActivity::class.java))
        }
    }

    private fun onJobClicked(job: JobModel) {
        Intent(requireContext(), JobDetailsActivity::class.java).apply {
            putExtra("jobId", job.jobId)
            startActivity(this)
        }
    }

    private fun startApplicationProcess(job: JobModel) {
        val intent = Intent(requireContext(), JobApplicationActivity::class.java).apply {
            putExtra("jobId", job.jobId)
            putExtra("jobTitle", job.title)
        }
        startActivity(intent)
    }

    private fun observeUserAndLoadJobs() {
        val authUser = userViewModel.getCurrentUser()
        authUser?.let { user ->
            userViewModel.getUserFromDatabase(user.uid) { userModel, success, message ->
                if (success && userModel != null && _binding != null) {
                    this.currentUser = userModel
                    setupRecyclerView(userModel.role == "employer")
                    binding.fabAddJob.visibility =
                        if (userModel.role == "employer") View.VISIBLE else View.GONE
                    loadJobs(userModel)
                } else if (!success) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadJobs(user: UserModel) {
        binding.progressBarJobs?.visibility = View.VISIBLE
        if (user.role == "employer") {
            jobViewModel.getJobsByEmployer(user.userId)
        } else {
            jobViewModel.getAllJobs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

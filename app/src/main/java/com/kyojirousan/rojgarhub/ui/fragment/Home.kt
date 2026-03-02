package com.kyojirousan.rojgarhub.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyojirousan.rojgarhub.adapter.ApplicationsAdapter
import com.kyojirousan.rojgarhub.databinding.FragmentHomeBinding
import com.kyojirousan.rojgarhub.model.ApplicationModel
import com.kyojirousan.rojgarhub.model.UserModel
import com.kyojirousan.rojgarhub.repository.ApplicationRepositoryImpl
import com.kyojirousan.rojgarhub.repository.JobRepositoryImpl
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.viewModel.ApplicationViewModel
import com.kyojirousan.rojgarhub.viewModel.JobViewModel
import com.kyojirousan.rojgarhub.viewModel.UserViewModel

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var jobViewModel: JobViewModel
    private lateinit var applicationViewModel: ApplicationViewModel
    private lateinit var applicationsAdapter: ApplicationsAdapter

    private var currentUser: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()
        setupRecyclerView()
        loadData()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        jobViewModel = JobViewModel(JobRepositoryImpl())
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
    }

    private fun setupRecyclerView() {
        applicationsAdapter = ApplicationsAdapter(
            isEmployer = currentUser?.role == "employer",
            onStatusUpdate = { application, newStatus ->
                updateApplicationStatus(application, newStatus)
            }
        )

        binding.rvRecentApplications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applicationsAdapter
        }
    }

    private fun loadData() {
        val authUser = userViewModel.getCurrentUser()
        if (authUser != null) {
            userViewModel.getUserFromDatabase(authUser.uid) { user, success, message ->
                if (success && user is UserModel) {
                    currentUser = user
                    updateUIForUser(user)
                    loadApplications(user)
                }
            }
        } else {
            showLoginMessage()
        }
    }

    private fun updateUIForUser(user: UserModel) {
        binding.tvWelcomeMessage.text = "Welcome, ${user.firstName}!"
        if (user.role == "employer") {
            binding.userTypeSection.visibility = View.VISIBLE
            binding.applicationsSectionTitle.text = "Applications for your jobs"
        } else {
            binding.userTypeSection.visibility = View.VISIBLE
            binding.applicationsSectionTitle.text = "Your Recent Applications"
        }
    }

    private fun showLoginMessage() {
        binding.userTypeSection.visibility = View.GONE
        binding.notLoggedInMessage.visibility = View.VISIBLE
        binding.rvRecentApplications.visibility = View.GONE
        binding.applicationsSectionTitle.visibility = View.GONE
    }

    private fun loadApplications(user: UserModel) {
        if (user.role == "employer") {
            applicationViewModel.getApplicationsForEmployer(user.userId)
        } else {
            applicationViewModel.getApplicationsByUserId(user.userId)
        }

        applicationViewModel.applicationsList.observe(viewLifecycleOwner) { applications ->
            applicationsAdapter.submitList(applications)
        }
    }

    private fun updateApplicationStatus(application: ApplicationModel, newStatus: String) {
        applicationViewModel.updateApplicationStatus(application.applicationId, newStatus)
        Toast.makeText(requireContext(), "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

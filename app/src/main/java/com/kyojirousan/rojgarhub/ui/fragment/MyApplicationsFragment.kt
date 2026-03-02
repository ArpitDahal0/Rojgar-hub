package com.kyojirousan.rojgarhub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyojirousan.rojgarhub.adapter.ApplicationsAdapter
import com.kyojirousan.rojgarhub.databinding.FragmentMyApplicationsBinding
import com.kyojirousan.rojgarhub.repository.ApplicationRepositoryImpl
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.viewModel.ApplicationViewModel
import com.kyojirousan.rojgarhub.viewModel.UserViewModel


class MyApplicationsFragment : Fragment() {
    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var applicationViewModel: ApplicationViewModel
    private lateinit var applicationsAdapter: ApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()
        setupRecyclerView()
        loadApplications()
    }

    private fun setupViewModels() {
        userViewModel = UserViewModel(UserRepositoryImpl())
        applicationViewModel = ApplicationViewModel(ApplicationRepositoryImpl())
    }

    private fun setupRecyclerView() {
        applicationsAdapter = ApplicationsAdapter(
            isEmployer = false,
            onStatusUpdate = { application, newStatus ->
                applicationViewModel.updateApplicationStatus(application.applicationId, newStatus)
            }
        )

        binding.rvApplications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationsAdapter
        }
    }


    private fun loadApplications() {
        binding.progressBar.visibility = View.VISIBLE

        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            applicationViewModel.getApplicationsByUserId(currentUser.uid)

            applicationViewModel.applicationsList.observe(viewLifecycleOwner) { applications ->
                binding.progressBar.visibility = View.GONE

                if (applications.isEmpty()) {
                    binding.rvApplications.visibility = View.GONE
                    binding.tvNoApplications.visibility = View.VISIBLE
                } else {
                    binding.rvApplications.visibility = View.VISIBLE
                    binding.tvNoApplications.visibility = View.GONE
                    applicationsAdapter.submitList(applications)
                }
            }
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvApplications.visibility = View.GONE
            binding.tvNoApplications.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

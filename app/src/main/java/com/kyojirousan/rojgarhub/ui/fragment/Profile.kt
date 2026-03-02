package com.kyojirousan.rojgarhub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kyojirousan.rojgarhub.databinding.FragmentProfileBinding
import com.kyojirousan.rojgarhub.model.UserModel
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.ui.activity.EditProfileActivity
import com.kyojirousan.rojgarhub.ui.activity.LoginActivity
import com.kyojirousan.rojgarhub.viewModel.UserViewModel

class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private var currentUserModel: UserModel? = null

    private val TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = UserViewModel(UserRepositoryImpl())
        setupClickListeners()
        loadUserData()
    }

    private fun setupClickListeners() {
        binding.editProfile.setOnClickListener {
            navigateToEditProfile()
        }

        binding.logout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser != null) {
            userViewModel.getUserFromDatabase(currentUser.uid) { user, success, message ->
                if (success && user != null) {
                    currentUserModel = user
                    updateUI(user)
                } else if (!success) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            navigateToLogin()
        }
    }

    private fun updateUI(user: UserModel) {
        binding.profileName.text = user.firstName
        binding.profileEmail.text = user.email
        binding.profileRole.text = user.role.uppercase()
    }

    private fun navigateToEditProfile() {
        currentUserModel?.let { user ->
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("USER_DATA", user)
            startActivity(intent)
        }
    }

    private fun logout() {
        userViewModel.logout { success, message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (success) {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

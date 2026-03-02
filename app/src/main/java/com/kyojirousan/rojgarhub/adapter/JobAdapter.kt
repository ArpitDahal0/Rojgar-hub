package com.kyojirousan.rojgarhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kyojirousan.rojgarhub.databinding.ItemJobBinding
import com.kyojirousan.rojgarhub.model.JobModel

class JobAdapter(
    private var jobs: List<JobModel>,
    private val onApplyClick: (JobModel) -> Unit,
    private val onDeleteClick: (JobModel) -> Unit,
    private val isEmployer: Boolean
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        holder.binding.apply {
            tvJobTitle.text = job.title
            tvJobLocation.text = job.location
            tvJobSalary.text = job.salary

            btnApply.visibility = if (isEmployer) View.GONE else View.VISIBLE
            btnDelete.visibility = if (isEmployer) View.VISIBLE else View.GONE

            btnApply.setOnClickListener { onApplyClick(job) }
            btnDelete.setOnClickListener { onDeleteClick(job) }
        }
    }

    override fun getItemCount(): Int = jobs.size

    fun updateJobs(newJobs: List<JobModel>) {
        jobs = newJobs
        notifyDataSetChanged()
    }
}

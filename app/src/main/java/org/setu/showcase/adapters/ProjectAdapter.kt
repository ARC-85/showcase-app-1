package org.setu.showcase.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.setu.showcase.databinding.CardProjectBinding
import org.setu.showcase.models.NewProject
import org.setu.showcase.main.MainApp

import org.setu.showcase.models.PortfolioModel

interface ProjectListener {
    fun onProjectClick(project: NewProject)
}

class ProjectAdapter constructor(private var projects: List<NewProject>,
                                  private val listener: ProjectListener) :
    RecyclerView.Adapter<ProjectAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardProjectBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val project = projects[holder.adapterPosition]
        holder.bind(project, listener)
    }

    override fun getItemCount(): Int = projects.size

    class MainHolder(private val binding : CardProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var app: MainApp


        fun bind(project: NewProject, listener: ProjectListener) {

            binding.projectTitle.text = project.projectTitle
            binding.projectDescription.text = project.projectDescription
            Picasso.get().load(project.projectImage).resize(200,200).into(binding.projectImageIcon)
            binding.root.setOnClickListener { listener.onProjectClick(project) }

        }
    }
}
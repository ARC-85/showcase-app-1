package org.setu.showcase.models

interface PortfolioStore {
    fun findAll(): List<PortfolioModel>
    fun create(portfolio: PortfolioModel)
    fun update(portfolio: PortfolioModel)
    fun findAllProjects(): List<NewProject>
    fun createProject(project: NewProject)
    fun updateProject(project: NewProject)
    fun findSpecificProjects(portfolio: PortfolioModel): List<NewProject>
}
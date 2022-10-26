package org.setu.showcase.models

interface PortfolioStore {
    fun findAll(): List<PortfolioModel>
    fun create(portfolio: PortfolioModel)
    fun update(portfolio: PortfolioModel)
    fun delete(portfolio: PortfolioModel)
    fun findAllProjects(): List<NewProject>
    fun createProject(project: NewProject, portfolio: PortfolioModel)
    fun updateProject(project: NewProject, portfolio: PortfolioModel)
    fun findSpecificProjects(portfolio: PortfolioModel): List<NewProject>
    fun deleteProject(project: NewProject, portfolio: PortfolioModel)
    fun findProjects(): List<NewProject>
    fun findProject(id: Long): NewProject?
    fun findPortfolio(portfolio: PortfolioModel): PortfolioModel?
    fun findSpecificPortfolios(portfolioType: String): List<PortfolioModel>
}
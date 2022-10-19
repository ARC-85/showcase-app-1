package org.setu.showcase.models


import timber.log.Timber.i

var lastId = 0L
var lastProjectId = 0L

internal fun getId(): Long {
    return lastId++
}


internal fun getProjectId(): Long {
    return lastProjectId++
}

class PortfolioMemStore : PortfolioStore {

    val portfolios = ArrayList<PortfolioModel>()

    override fun findAll(): List<PortfolioModel> {
        return portfolios
    }

    override fun create(portfolio: PortfolioModel) {
        portfolio.id = getId()
        portfolios.add(portfolio)
        logAll()
    }

    override fun update(portfolio: PortfolioModel) {
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            foundPortfolio.title = portfolio.title
            foundPortfolio.description = portfolio.description
            foundPortfolio.image = portfolio.image
            logAll()
        }
    }

    private fun logAll() {
        portfolios.forEach { i("$it") }
    }

    val projects = ArrayList<NewProject>()
    //var filtered = ArrayList<NewProject>()


    override fun findAllProjects(): List<NewProject> {
        return projects
    }

    override fun findSpecificProjects(portfolio: PortfolioModel): List<NewProject> {
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            return projects.filter { p -> p.portfolioId == foundPortfolio.id }
        }
        return projects
    }

    override fun createProject(project: NewProject) {
        project.projectId = getProjectId()
        projects.add(project)
        logAllProjects()
    }

    override fun updateProject(project: NewProject) {
        var foundProject: NewProject? = projects.find { p -> p.projectId == project.projectId }
        if (foundProject != null) {
            foundProject.projectTitle = project.projectTitle
            foundProject.projectDescription = project.projectDescription
            foundProject.projectImage = project.projectImage
            foundProject.lat = project.lat
            foundProject.lng = project.lng
            foundProject.zoom = project.zoom
            logAllProjects()
        }
    }

    override fun deleteProject(project: NewProject) {
        projects.remove(project)
        logAllProjects()
    }

    private fun logAllProjects() {
        projects.forEach { i("$it") }
    }

}
package org.setu.showcase.models


import android.annotation.SuppressLint
import timber.log.Timber
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
    var totalProjects = mutableListOf<NewProject>()


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
            foundPortfolio.projects = portfolio.projects
            logAll()
        }
    }

    override fun delete(portfolio: PortfolioModel) {
        portfolios.remove(portfolio)
        logAll()
    }

    private fun logAll() {
        portfolios.forEach { i("$it") }
    }

    private fun logProjects() {
        portfolios.forEach { Timber.i("$it")
            var portfolioProjects = it.projects
            if (portfolioProjects != null) {
                projects += portfolioProjects.toMutableList()
            }
        }
    }

    override fun findProjects(): MutableList<NewProject> {
        logProjects()
        return projects
    }

    override fun findProject(id: Long): NewProject? {
        logProjects()
        return projects.find { p -> p.projectId == id }
    }

    override fun findPortfolio(portfolio: PortfolioModel): PortfolioModel? {
        logAll()
        return portfolios.find { p -> p.id == portfolio.id }
    }

    /*override fun findPortfolioProjects(portfolio: PortfolioModel): List<NewProject> {
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            return projects.filter { p -> p.portfolioId == foundPortfolio.id }

        }
        return portfolioProjects
    }*/

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

    override fun findSpecificPortfolios(portfolioType: String): MutableList<PortfolioModel> {
        var list = portfolios.filter { p -> p.type == portfolioType }
        return list.toMutableList()
        logAll()
        return portfolios
    }

    override fun findSpecificTypeProjects(portfolioType: String): MutableList<NewProject> {
        var list = portfolios.filter { p -> p.type == portfolioType }
        println("this is list: $list")
        list.forEach { Timber.i("$it")
            var portfolioTypeProjects = it.projects
            if (portfolioTypeProjects != null) {
                projects += portfolioTypeProjects.toMutableList()
            }
        }
        return projects
    }

    override fun createProject(project: NewProject, portfolio: PortfolioModel) {
        project.projectId = getProjectId()
        projects.add(project)
        logAllProjects()
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {

            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects
                portfolioProjects = portfolioProjects?.plus(project)
                foundPortfolio.projects = portfolioProjects

            } else {
                foundPortfolio.projects = arrayOf(project)
            }
            logAll()
        }

    }

    override fun updateProject(project: NewProject, portfolio: PortfolioModel) {
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
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {

            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects!!.dropLast(1)
                portfolioProjects = portfolioProjects?.plus(project)
                foundPortfolio.projects = ArrayList(portfolioProjects).toTypedArray()

            }
            logAll()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun deleteProject(project: NewProject, portfolio: PortfolioModel) {
        projects.remove(project)
        logAllProjects()
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects!!.dropLast(1)

                foundPortfolio.projects = ArrayList(portfolioProjects).toTypedArray()

            } else
            logAll()
        }
    }

    private fun logAllProjects() {
        projects.forEach { i("$it") }
    }


}



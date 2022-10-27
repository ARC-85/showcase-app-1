package org.setu.showcase.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.setu.showcase.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val PORTFOLIO_JSON_FILE = "portfolios.json"
const val PROJECT_JSON_FILE = "projects.json"
const val SHOWCASE_JSON_FILE = "showcase.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()

val listType: Type = object : TypeToken<ArrayList<PortfolioModel>>() {}.type
val projectListType: Type = object : TypeToken<ArrayList<NewProject>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class PortfolioJSONStore(private val context: Context) : PortfolioStore {

    var portfolios = mutableListOf<PortfolioModel>()


    init {
        if (exists(context, PORTFOLIO_JSON_FILE)) {
            deserialize()
        }
        /*if (exists(context, PORTFOLIO_JSON_FILE)) {
            deserialize()
        }
        if (exists(context, PROJECT_JSON_FILE)) {
            deserializeProjects()
        }*/
    }

    override fun findAll(): MutableList<PortfolioModel> {
        logAll()
        return portfolios
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

    override fun create(portfolio: PortfolioModel) {
        portfolio.id = generateRandomId()
        portfolios.add(portfolio)
        serialize()
    }

    override fun update(portfolio: PortfolioModel) {
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            foundPortfolio.title = portfolio.title
            foundPortfolio.description = portfolio.description
            foundPortfolio.image = portfolio.image
            foundPortfolio.projects = portfolio.projects
            foundPortfolio.type = portfolio.type
            serialize()
        }
    }

    override fun delete(portfolio: PortfolioModel) {
        println("this is the removed portfolio: $portfolio")
        portfolios.remove(portfolio)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(portfolios, listType)
        write(context, PORTFOLIO_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, PORTFOLIO_JSON_FILE)
        portfolios = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        portfolios.forEach { Timber.i("$it") }
    }

    private fun logProjects() {
        portfolios.forEach { Timber.i("$it")
            var portfolioProjects = it.projects
            if (portfolioProjects != null) {
                projects += portfolioProjects.toMutableList()
            }
        }
    }

    var projects = mutableListOf<NewProject>()

    override fun findAllProjects(): MutableList<NewProject> {
        logAllProjects()
        return projects
    }

    override fun findSpecificProjects(portfolio: PortfolioModel): MutableList<NewProject> {
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            var list = projects.filter { p -> p.portfolioId == foundPortfolio.id }
            return list.toMutableList()
        }
        logAllProjects()
        return projects
    }

    override fun findSpecificPortfolios(portfolioType: String): MutableList<PortfolioModel> {
        var list = portfolios.filter { p -> p.type == portfolioType }
        return list.toMutableList()
        println("this is list: $list")
        logAll()
        return portfolios
    }

    override fun createProject(project: NewProject, portfolio: PortfolioModel) {
        project.projectId = generateRandomId()
        projects.add(project)
        serializeProjects()
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {

            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects
                portfolioProjects = portfolioProjects?.plus(project)
                foundPortfolio.projects = portfolioProjects

            } else {
                foundPortfolio.projects = arrayOf(project)
            }
            serialize()
        }
    }


    override fun updateProject(project: NewProject, portfolio: PortfolioModel) {
        var foundProject: NewProject? = projects.find { p -> p.projectId == project.projectId }
        if (foundProject != null) {
            foundProject.projectTitle = project.projectTitle
            foundProject.projectDescription = project.projectDescription
            foundProject.projectImage = project.projectImage
            foundProject.projectImage2 = project.projectImage2
            foundProject.projectImage3 = project.projectImage3
            foundProject.lat = project.lat
            foundProject.lng = project.lng
            foundProject.zoom = project.zoom
            foundProject.projectCompletionDay = project.projectCompletionDay
            foundProject.projectCompletionMonth = project.projectCompletionMonth
            foundProject.projectCompletionYear = project.projectCompletionYear
            serializeProjects()
        }
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {

            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects!!.dropLast(1)
                portfolioProjects = portfolioProjects?.plus(project)
                foundPortfolio.projects = ArrayList(portfolioProjects).toTypedArray()

            }
            serialize()
        }
    }

    override fun deleteProject(project: NewProject, portfolio: PortfolioModel) {
        projects.remove(project)
        serializeProjects()
        var foundPortfolio: PortfolioModel? = portfolios.find { p -> p.id == portfolio.id }
        if (foundPortfolio != null) {
            if (foundPortfolio.projects != null) {
                var portfolioProjects = foundPortfolio.projects!!.dropLast(1)

                foundPortfolio.projects = ArrayList(portfolioProjects).toTypedArray()

            }
            serialize()
        }
    }

    private fun serializeProjects() {
        val projectJsonString = gsonBuilder.toJson(projects, projectListType)
        write(context, PROJECT_JSON_FILE, projectJsonString)
    }

    private fun deserializeProjects() {
        val projectJsonString = read(context, PROJECT_JSON_FILE)
        projects = gsonBuilder.fromJson(projectJsonString, projectListType)
    }

    private fun logAllProjects() {
        projects.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }




}

class ProjectUriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }




}

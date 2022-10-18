package org.setu.showcase.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PortfolioModel(var id: Long = 0,
                          var title: String = "",
                          var description: String = "",
                          var image: Uri = Uri.EMPTY,
var projects: Array<NewProject>? = null) : Parcelable

@Parcelize
data class NewProject(var projectId: Long = 0,
                      var portfolioId: Long = 0,
                        var projectTitle: String = "",
                        var projectDescription: String = "",
                        var projectImage: Uri = Uri.EMPTY) : Parcelable
package com.lunatech.iamin

import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.{JSON, URIUtils}

object IaminAPI {

  def fetchUser(id: String): Future[User] = {
    Ajax.get(
      userEndpoint(id),
      headers = Map("Content-Type" -> "application/json")
    ).map { xhr =>
      JSON.parse(xhr.responseText).asInstanceOf[User]
    }
  }

  def userEndpoint(id: String) = s"http://localhost:8080/users/$id"
}

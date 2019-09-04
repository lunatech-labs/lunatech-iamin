package com.lunatech.iamin

import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.{JSON, URIUtils}

object IaminAPI {

  val usersEndpoint = "http://localhost:8080/users"

  def getUser(id: String): Future[User] = {
    Ajax.get(
      s"$usersEndpoint/$id",
      headers = Map("Content-Type" -> "application/json")
    ).map { xhr =>
      JSON.parse(xhr.responseText).asInstanceOf[User]
    }
  }

  def postUser(name: String): Future[User] = {
    Ajax.post(
      usersEndpoint,
      headers = Map("Content-Type" -> "application/json"),
      data = s"""{\"name\":\"$name\"}"""
    ).map { xhr =>
      JSON.parse(xhr.responseText).asInstanceOf[User]
    }
  }
}

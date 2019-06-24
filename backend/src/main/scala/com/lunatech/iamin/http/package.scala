package com.lunatech.iamin

import com.lunatech.iamin.endpoints.definitions.ProblemResponseJson
import org.http4s.Status

package object http {

  def problemOf(status: Status, detail: String): ProblemResponseJson =
    ProblemResponseJson(status.reason, status.code, detail)

  def notFoundProblem(detail: String): ProblemResponseJson =
    problemOf(Status.NotFound, detail)

  def conflictProblem(detail: String): ProblemResponseJson =
    problemOf(Status.Conflict, detail)
}

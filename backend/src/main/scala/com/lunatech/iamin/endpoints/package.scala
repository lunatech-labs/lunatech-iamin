package com.lunatech.iamin

import com.lunatech.iamin.endpoints.definitions.ProblemResponseJson
import org.http4s.Status

package object endpoints {

  def problemOf(status: Status, detail: String): ProblemResponseJson =
    ProblemResponseJson(status.reason, status.code, detail)
}

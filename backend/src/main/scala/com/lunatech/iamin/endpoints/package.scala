package com.lunatech.iamin

import com.lunatech.iamin.endpoints.definitions.Problem
import org.http4s.Status

package object endpoints {

  def problemOf(status: Status, detail: String): Problem =
    Problem(status.reason, status.code, detail)
}

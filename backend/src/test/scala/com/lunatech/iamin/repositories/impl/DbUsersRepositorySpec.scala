package com.lunatech.iamin.repositories.impl

import java.time.LocalDateTime

import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.model
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

class DbUsersRepositorySpec extends Specification with BeforeAfterAll with IOMatchers {

  // Run in sequence
  sequential

  // TODO: Extract database stuff into dedicated base trait
  private var database: AutoCloseable = _
  private var repo: DbUsersRepository = _

  override def beforeAll(): Unit = {
    val embeddedPostgres = EmbeddedPostgres.start()
    val ds = embeddedPostgres.getPostgresDatabase
    val db = Database.forDataSource(ds, None)

    database = embeddedPostgres

    com.lunatech.iamin.database.Database.migrate(ds.getConnection).unsafeRunSync

    repo = new DbUsersRepository(db)
  }

  override def afterAll(): Unit = {
    database.close()
  }

  var user: model.User = _
  var otherUser: model.User = _

  "db user repository" >> {

    "is initially empty" >> {
      repo.getUsers() should returnValue(Seq.empty[model.User])
    }

    "can create user" >> {
      repo.createUser("Alice") should returnValue { user: model.User =>
        this.user = user
        user.displayName === "Alice"
      }
    }

    "contain created user" >> {
      repo.getUser(user.id) should returnValue { either: Either[_, model.User] => either should be right user }
    }

    "can add another user" >> {
      repo.createUser("Bob") should returnValue { user: model.User =>
        this.otherUser = user
        user.displayName === "Bob"
      }
    }

    "cannot update unknown user" >> {
      repo.updateUser(model.User(42, "Bas", LocalDateTime.now)) should returnValue { either: Either[_, _] =>
        either should be left
      }
    }

    "can update user" >> {
      val updatedUser = user.copy(displayName = "Carol")

      repo.updateUser(updatedUser) should returnValue { either: Either[_, model.User] =>
        this.user = either.right.get

        either should be right updatedUser
      }
    }

    "can retrieve all users" >> {
      repo.getUsers() should returnValue(Seq(user, otherUser))
    }

    "cannot delete unknown user" >> {
      repo.deleteUser(101) should returnValue { either: Either[_, _] =>
        either should be left
      }
    }

    "can delete user" >> {
      val users = for {
        _     <- repo.deleteUser(user.id)
        users <- repo.getUsers()
      } yield users

      users should returnValue(Seq(otherUser))
    }
  }
}

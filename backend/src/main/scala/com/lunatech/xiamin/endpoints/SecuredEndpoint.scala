package com.lunatech.xiamin.endpoints

import java.nio.charset.StandardCharsets

import cats.{Eq, Id, MonadError}
import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import io.circe.Json
import org.http4s._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._
import tsec.authorization._
import tsec.common.SecureRandomId
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class SecuredEndpoint[F[_] : Sync] {
  private val dsl = Http4sDsl[F]
  import dsl._

  def inMemoryBackingStore[F[_], I, V](getId: V => I)(implicit F: Sync[F]) = new BackingStore[F, I, V] {
    private val store = TrieMap.empty[I, V]

    def put(elem: V): F[V] = {
      val map = store.put(getId(elem), elem)
      if (map.isEmpty) {
        F.pure(elem)
      } else {
        F.raiseError(new IllegalArgumentException)
      }
    }

    def get(id: I): OptionT[F,V] = {
      println(s">>> Looking for item with id $id, $this")

      OptionT.fromOption[F](store.get(id))
    }

    def update(v: V): F[V] = {
      store.update(getId(v), v)
      F.pure(v)
    }

    def delete(id: I): F[Unit] =
      store.remove(id) match {
        case Some(_) => F.unit
        case None    => F.raiseError(new IllegalArgumentException)
      }
  }

  sealed case class Role(roleRepr: String)
  object Role extends SimpleAuthEnum[Role, String] {
    val Administrator: Role = Role("Administrator")
    val User: Role = Role("User")

    implicit val eq: Eq[Role] = Eq.fromUniversalEquals[Role]

    def getRepr(t: Role): String = t.roleRepr

    protected val values: AuthGroup[Role] = AuthGroup(Administrator, User)
  }

  case class User(id: Int, role: Role = Role.User)
  object User {
    implicit def authRole[F[_]](implicit F: MonadError[F, Throwable]): AuthorizationInfo[F, Role, User] =
      new AuthorizationInfo[F, Role, User] {
        def fetchInfo(u: User): F[Role] = F.pure(u.role)
      }
  }


  val jwtStore: BackingStore[F, SecureRandomId, AugmentedJWT[HMACSHA256, Int]] = inMemoryBackingStore[F, SecureRandomId, AugmentedJWT[HMACSHA256, Int]](s => SecureRandomId.coerce(s.id))
  val userStore: BackingStore[F, Int, User] = inMemoryBackingStore[F, Int, User](_.id)
  val signingKey: MacSigningKey[HMACSHA256] = HMACSHA256.buildKey[Id]("secret".getBytes(StandardCharsets.UTF_8))

  val jwtStatefulAuth = JWTAuthenticator.backed.inBearerToken(
    expiryDuration = 10.minutes,
    maxIdle = None,
    tokenStore = jwtStore,
    identityStore = userStore,
    signingKey = signingKey
  )

  val jwtStatelessAuth = JWTAuthenticator.unbacked.inBearerToken(
    expiryDuration = 1.hour,
    maxIdle = None,
    identityStore = userStore,
    signingKey = signingKey
  )

  val Auth = SecuredRequestHandler(jwtStatefulAuth)



  val claims0 = JWTClaims(customFields = Seq("userid" -> Json.fromInt(42)))

  val jwt0 = JWTMac.buildToString[cats.effect.IO, HMACSHA256](claims0, signingKey).unsafeRunSync()



//  println(JWTMac.buildToString[cats.effect.IO, HMACSHA256](JWTClaims(subject = Some("4"), jwtId = Some(id0)), key = signingKey).unsafeRunSync())



  val service: HttpRoutes[F] = Auth.liftService(TSecAuthService {
    case req @ GET -> Root / "api" asAuthed user =>
      val r: SecuredRequest[F, User, AugmentedJWT[HMACSHA256, Int]] = req
      println(r.request)
      println(r.authenticator)
      println(r.identity)
      println(user)

      PaymentRequired()
  })
}

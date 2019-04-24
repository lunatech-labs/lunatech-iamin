package com.lunatech.iamin

//import cats.effect._
//import cats.implicits._
//import com.lunatech.iamin.config.Config
//import com.lunatech.iamin.database.Database
//import com.lunatech.iamin.endpoints.users.UsersResource
//import com.lunatech.iamin.endpoints.version.VersionResource
//import com.lunatech.iamin.repository.slick.UserRepositorySlickInterpreter
//import com.lunatech.iamin.utils.BuildInfo
//import fs2.Stream
//import org.http4s.implicits._
//import org.http4s.server.blaze.BlazeServerBuilder
//import org.http4s.server.middleware.Logger
//
//@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
//object Main extends IOApp {
//
//  override def run(args: List[String]): IO[ExitCode] = {
//
//    val server = for {
//      config <- Resource.liftF(Config.load())
//      db     <- Database.create(config.database)
//      _      <- Resource.liftF(Database.migrate(db.source.createConnection))
//      server = new Server(config, db)
//    } yield server
//
//    server.use(_.stream[IO].compile.drain.as(ExitCode.Success))
//  }
//
//  class Server(config: Config, db: com.lunatech.iamin.database.Profile.api.Database) {
//    def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
//
//      val userRepository = new UserRepositorySlickInterpreter[F](db)
//      val httpApp = (
//        new VersionResource[F]().routes(new VersionHandlerImpl[F](BuildInfo)) <+>
//          new UsersResource[F]().routes(new UsersHandlerImpl[F](userRepository))
//        ).orNotFound
//      val finalHttpApp = Logger(logHeaders = true, logBody = true)(httpApp)
//
//      BlazeServerBuilder[F]
//        .bindHttp(config.server.port, config.server.host)
//        .withHttpApp(finalHttpApp)
//        .serve
//        .drain
//    }
//  }
//}

# lunatech-iamin

I am in!

## Database setup

If you're using `Docker` you can use the following command to setup PostgreSQL:

```
docker run -d --name postgres \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=password \
-e POSTGRES_DB=iamin \
-p 5432:5432 postgres
```

If you're not using the command above, you must create the database manually, for example using `psql` tools:

```
createdb iamin
```

Also, if you have a different PostgreSQL setup, you may need to set some env variables so that the application can find the correct database:

```
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=iamin
export DB_USER=postgres
export DB_PASSWORD=password
```

**TODO**: something about configuration overrides


## Starting the application

Requirements:

- Scala 2.12
- Sbt 1.2.8
- PostgreSQL 11 with correct setup, see above

run:

```
sbt runMain com.lunatech.iamin.rest.Main
```


## Creating and managing API endpoints

Location of the api specification is `documentation/api.yaml`, format used is [OpenAPI specification v3.x.x](https://swagger.io/specification).

>Because of the [Guardrail](https://github.com/twilio/guardrail) code generator, some additional attributes are required on resources (namely `operationId` and `x-scala-package`), find the full list extensions [here](https://github.com/twilio/guardrail/blob/master/docs/book.md#guardrail-extensions).

When you made some changes to the specification you can trigger the code generation by compiling the project:

```
sbt compile
```
This will save the endpoint `Handler` stubs in the `src_managed` directory in the form a a Scala `trait` and a companion object.
Put your implementation in `src/scala/com/lunatech/iamin/rest/{endpoint}/impl/{endpoint}Handler`, take a look at what's already there as a reference.

If the endpoint you created is new, you also must add its routes to the server in `com.lunatech.iamin.rest.IaminServer`, again look at what's there for reference.


## Managing database schema and generated code

Find the database migrations in `src/main/resources/migrations`, files are in Liquibase's [xml format](https://www.liquibase.org/documentation/xml_format.html).

When adding a new migration, please observer the naming convention of the files: `YYYYMMdd_short_description.xml`, it is important to have an ascending order since Liquibase applies these migrations in order.

> Never change an existing migration once it has been applied to the database! If you need to change something just add a new migration.

Once you added you migration, run the generateSlickTables command:

```
sbt generateSlickTables
```
This sbt task will:

1. Create a temporary Postgresql database
2. Apply all Liquibase migrations to that database
3. Generate database access objects using Slick codegen
4. Save the generated code to `com.lunatech.iamin.database.tables`
5. Destroy the temporary database

Don't forget to commit the (newly) generated code to the Git repository.


## Stack

- [PostgreSQL](https://www.postgresql.org) database
- [Scala](https://www.scala-lang.org) language
- [OpenAPI](https://swagger.io) service definition and documentation
- [Guardrail](https://github.com/twilio/guardrail) OpenAPI to Http4s code generator
- [Http4s](https://http4s.org) web server
- [Liquibase](https://www.liquibase.org) database migrations
- [Slick](http://slick.lightbend.com) database access
- [Circe](https://circe.github.io/circe) json serialization
- [Specs2](https://etorreborre.github.io/specs2/) testing
- [Wartremover](http://www.wartremover.org) code linting
- frontend stuff TBD
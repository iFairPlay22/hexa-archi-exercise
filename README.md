# hexa-archi-exercise

This repository is a backend project based on `Quarkus` and `MongoDB`, which allows to manipulate todos, thanks to a CRUD API.

## Run the application

In order to run the application, you need to install `java` and `quarkus` in your machine.
Please check the following documentation if it is not already the case:
- https://www.java.com/en/download/help/download_options.html (for java)
- https://quarkus.io/guides/cli-tooling (for quarkus-cli)

Then, you can run the backend in dev using:
```shell script
quarkus dev
```

The application will be available in the port **8080**.

Note that you can test the API using the postman collection located [there](dev%2Fhexa-archi-exercise.postman_collection.json).

## Exercise instructions

Before starting the exercise, you should of course get familiar with [Quarkus](https://quarkus.io/guides/getting-started) and [MongoDB](https://www.mongodb.com/docs/manual/tutorial/getting-started/) basics.

1) As a first task, please edit the API so that we can filter by tag, without considering case matching and accents (ex: 'Santé' should match with 'sante'). Also, we would like to be able to specify multiple tags as a parameter. 
2) Then, add 3 users: 'Ewen' & 'Sebastien', two regular users and 'Nicolas', the admin. A regular user should be able to add/modify/delete/get its own todos, while the administrator has full rights.
3) Finally, refactor the application to use the [hexagonal infrastructure](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)). NB: Propose a system to make sure that there is no regression.

Good luck ! ;) 
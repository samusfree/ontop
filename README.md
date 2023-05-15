# This is my solution from the challenge from [Ontop](https://www.getontop.com/) 💪

### Table of content

- [Project description](#description)

- [Features](#features)

- [Todo](#todo)

- [Technologies & Tools](#-technologies--tools)

- [Run on local](#run-on-local)

- [Test evidences](#test-evidences)

- [Authors](#authors)

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Description

<p align="justify">
A Rest API to serve the functionality of transfers from Ontop Account to customer Account. The API is developed over Spring Boot using Hexagonal Architecture. 

![ui-image](readme-files/challange_ui.png)
</p>

![Hexagonal diagram where is explained the components of the app](readme-files/hexagonal.png)

<p align="justify">
As show in this diagram this app have one input the Rest API Client generating one adapter for.

For the business logic use case are generated and the domain to define the business logic and domain objects.

For integration with the output ports is using repositories to connect to H2 database and service to connect to the
external services.
</p>

![Activity diagram where is show how the layers interact between them](readme-files/hexagonal_activity.png)

## Features

✅ `Feature 1:` Create customer Account.

✅ `Feature 2:` Withdraw

✅ `Feature 3:` Transaction list

## Todo

☑️ `Feature 1:` Create more than one Account for userId

☑️ `Feature 2:` Top up

☑️ `Feature 3:` Improvement of the list api

☑️ `Feature 3:` Add docker support

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## 🛠 Technologies & Tools

- **Language:** Java 17
- **Framework :** Spring Boot 3
- **Architecture :** Hexagonal architecture
- **Web framework :** Webflux
- **Data framework :** Spring Data R2DBC
- **Database :** H2
- **Api Docs :** Spring Doc

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Git](https://img.shields.io/badge/-Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/-GitHub-181717?style=for-the-badge&logo=github)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Run on local

1. Clone the repo
2. Install java 17 (SDK Man or from another install manager)
3. inside the repo on the console run the ./mvnw test for run the unit test of the project (macos / linux) / mvnw.cmd
   test (windows)
4. Inside the repo on the console run ./mvnw spring-boot:run to start the app / mvnw.cmd spring-boot:run (windows). The
   project runs on the default port 8080.
5. Use the postman collection to test [Postman collection](readme-files/Ontop.postman_collection.json) inside this collection there are the 3 generated endpoints one with the localhost URL and another with a cloud vendor url.
6. To test first you must create the Account with the account endpoint (there is a examples in postman) and after that use the witdraw endpoint to generate the operations (there are examples in postman). Additional there is a list endpoint to review the saved data by user.
7. Also can enter to the swagger-ui [Swagger UI](http://localhost:8080/swagger-doc/webjars/swagger-ui/index.html)

## Test evidences

![Swagger UI test](readme-files/swagger-test.png)
![Postman test](readme-files/postman-test.png)
![Jacoco report](readme-files/jacoco-report.png)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Authors

| [<img src="https://avatars.githubusercontent.com/u/6700707?v=4" width=115><br><sub>Samuel Gonzales</sub>](https://github.com/samusfree) |  
|:---------------------------------------------------------------------------------------------------------------------------------------:|

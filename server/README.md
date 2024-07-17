# WCA Statistics Server

## Requirements

- [Java](https://www.java.com/pt-BR/)

- [MySQL](https://dev.mysql.com/doc/refman/8.0/en/linux-installation.html)

## Setup local database

### Using docker

If you are in the `server` folder, you can just run `docker-compose up -d`. This will spin up an empty MySQL database running on port 3306.

### Your own local copy of WCA's database

You can also use an internal database for handling WCA data.

In case you do not have it installed yet, you will need to get MySQL.

- Install [MySQL 8.0](https://dev.mysql.com/doc/refman/8.0/en/linux-installation.html), and set it up with a user with username "root" with an empty password.

```
sudo mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';

create database wca_development;
```

The database `wca_development` will be populated with WCA data. If you want to change password, username or others, make sure to also change on `application-local.properties`.

## Before you run this

You need your copy of the database from WCA. If you already have it (with a user 'root' with no password), you can skip this.

Download [the latest export](https://www.worldcubeassociation.org/wst/wca-developer-database-dump.zip) and execute the sql (as stated in the last step). If you wish, you can execute the file `get_db_export.sh` in the scripts folder.

From the root folder, use

    source scripts/get_db_export.sh

## How to run it

- Clone this repository

`git clone https://github.com/thewca/statistics.git`

### With your IDE

- Open the `server` folder in your favorite IDE (not the root folder)
- The project uses gradle and your IDE should recognize that.
- You can hit the run (or debug) button to run it.

### With CLI

Navigate to the server's folder (Unix systems)

`cd statistics/server`

- Run the server

`./gradlew bootRun`

An address should be logged. Probably http://localhost:8080/swagger-ui.html#/, if you did not change port. Visit it to read the documentation. You can run in another port (let's say 8001) by using `./gradlew bootRun --args='--spring.profiles.active=local --server.port=8001'`.

For Windows, I think you need to use `.\gradlew.bat bootRun`.

## Run with docker

- Build an image

`docker build -t {user}/statistics-server .`

- Run the image

`docker run -d -p 8080:8080 --name statistics-server {user}/statistics-server:latest`

The `-d` part means "detached", so you'll have to stop by killing the process running on port 8080.

## Tests

This backend project uses integration tests so we need to actually connect to a database and fetch data from somewhere.

- Preparing the database for tests (from the repository root)

  `docker-compose -f server/docker-compose-test.yaml up -d`

This will start the database (port 3307) and also a mocked version of the WCA's api (for getting user info) on port 3500.

- Run the tests

In a new terminal, from the repository root, run

    ./server/gradlew clean build -p server --info

- If you need to change migrations, run

  ```
  docker-compose -f server/docker-compose-test.yaml down --volumes
  docker-compose -f server/docker-compose-test.yaml up -d
  ```

## Code format

### Intelij

- `File/Settings`
- `Editor/Code style`
- `Scheme actions/Import schemes/IntelliJ IDEA...`
- Import file `server/codequality-config/checkstyle/intellij-java-google-style.xml`

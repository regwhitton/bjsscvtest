# Reg Whitton's Solution for BJSS' CV test

This project uses the following technologies:

* Java 11
* Spring Boot
* Spring Data JPA (with Hibernate and H2 database)

## Setup and Build

### Java 11

You will need to have the Java 11 JDK (or higher) installed.  Check by typing the following into the command line:

    $ java -version
    openjdk version "11.0.4" 2019-07-16
    OpenJDK Runtime Environment 18.9 (build 11.0.4+11)
    OpenJDK 64-Bit Server VM 18.9 (build 11.0.4+11, mixed mode)
    
    $ javac -version
    javac 11.0.4

If these are not found or show a version lower than 11 then download and install a Java JDK from [Oracle](https://jdk.java.net/).

Once installed, the directory containing java should have been added to your PATH environment variable,
so that the above `java` and `javac` commands will work from the command line. 

### Building and starting the server from the command line

    cd path/to/this/dir
    mvnw package            (./mvnw - if using bash)
    mvnw spring-boot:run    (Use Control-C or task-manager to stop)
    
Browse to <http://localhost:8080/api/swagger-ui/> to see the API details.

To view the database browse to <http://localhost:8080/api/h2-console/> and login using these details:
 
* Driver Class: `org.h2.Driver`
* JDBC URL: `jdbc:h2:mem:testdb`
* User Name: `sa`
* Password: \<leave empty\>

### Building in an IDE

This project uses Lombok to insert getters, setters and other boiler plate code.  As a consequence the raw code will
show compilation errors in your IDE.  To avoid this use the Lombok plugin. Go to <https://projectlombok.org/> and
select the installation instructions for your IDE from the "Install" menu.

## Notes on Implementation

* For demonstration purposes an in-memory database has been used.
* Spring Data enables database transactions by default.

### Assumptions made about the task

* The system allows a single user to manipulate multiple CVs, rather than multiple users manipulate just their own CV.

### Productionisation 

* In production the web services would need to be secured and a registration process provided.

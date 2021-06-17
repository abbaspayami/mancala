# Mancala-Game

* [How to play Kalah (Wikipedia)](https://en.wikipedia.org/wiki/Kalah)

## How to Run
* Execute tests:

   **mvn clean install** 

* starting Application:

    **mvn spring-boot:run**

Alternatively, a jar package can be obtained with: **mvn clean package**

And then run: **java -jar target/Mancala-Bol.com-0.0.1.jar**

Either way, afterward the game can be started at **http://localhost:8080**

## Feature:
Configurable number of stones from this address [\src\main\resources\application.properties]

### Tools & Technologies

* [Java 8]
* [Spring boot]
* [Maven]
* [Thymeleaf]
* [Lombok]
* [Swagger]
* [JUnit 5]
* [Mockito]
* [H2]


### Guides
Follow the link below to find the proper documentation:
* [API Swagger UI](/swagger-ui.html#)

Follow the Link to see DataBase Status
* [DataBase_H2](/h2-console/login.jsp)

### Author
This project has been implemented by **Abbas Payami** for Bol.com Code Challenge
[Contact me](payami2013@gmail.com)


## Notes :
* First, you can call this API for starting the game in postman
      [ curl -X POST \ http://localhost:8080/mancala/start ]

*  For moving and playing you can call this API in postman

      [ curl -X PUT \ 'http://localhost:8080/mancala/move?game=1&pit=4'   ] 

      "?game=1" is the number gameId you have been created and must enter a game id
      "&pit=4" is the number pit index That is going to move. for example, I set 4.
      For starting the game every pit you can select. except that pit number 7, 14.
      PlayerOne is pitting number 1 to 6 and playerTwo is pits number 8 to 13.
      Each player has a larger pit, 7 is related to PlayerOne and 14 is related to PlayerTwo.
      
*  For moving For getting the current status of the game you can call this API 

      [ curl -X GET \ 'http://localhost:8080/mancala/gameStatus?game=1'  ]




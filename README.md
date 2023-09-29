# trafiklab-bus-lines
A SpringBoot command line application that prints Trafiklab's top 10 longest lines and the bus stops of the longest one.
In case of lines with the same number of stops the API considers the order of the lines in the response of Trafiklab's API.

## Building and executing the JUnit test
`mvn clean install`

## Running instructions
`mvn spring-boot:run -Dspring-boot.run.arguments=--application.trafiklab.endpoint.key=REPLACE-WITH-YOUR-KEY`


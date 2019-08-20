Coding-Exercise
=========================================

This application is designed to process a csv file containing a single tap on or tap off per line and output the trip
data to csv

Developer Build
---------------
Package the jar with dependencies

mvn [clean] package

Unit Tests
------------
Unit tests are run automatically on every build

Resource Files
--------------
**Trip pricing data is held in the pricing.csv**

Bellow is an example of a valid pricing file.

StopID1 StopID2 ChargeAmount

Stop1, Stop2, 5.50

**Tap on and tap off data is held in the taps.csv**

Bellow is an example of a valid pricing file.

ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN

1, 22-01-2018 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559

**Trip data is written to the trips.csv**

Bellow is an example of the output file created when running the application.

Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status

2018-01-22T13:00,2018-01-22T13:05,300,Stop1,3.25,Company1,Bus37,5500005555555559


Running the application
------------------------
The application requires the file paths of the pricing.csv and taps.csv to be passed as arguments.

The pricing csv can be found at {project.directory}/src/resources/pricing.csv

**Bellow is an example of how to run the application from the project root directory**

java -jar target\coding-exercise-1.0-SNAPSHOT-jar-with-dependencies.jar "src/main/resources/pricing.csv"  "src/main/resources/taps-example.csv"

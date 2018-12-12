# processing-pipeline

The processing-pipeline is our streaming service between ingestion 
and analyzing database. It is reading from Cloud PubSub and writing
to Cloud SQL.

## Overview

![Integration overview](docs/img/integration.svg)

_* Currently we are not using Cloud Dataflow. Instead we are using
the "Apache Beam Direct Runner" and running the application on our
kubernetes cluster._

## How it works

The pipeline has 3 major steps to read, convert and write data from
PubSub into Cloud SQL. All of those steps are defined in `ProcessingApplication.java`

1) __PubSubIO__: The pipeline is continuously listening to the PubSub topic
`ingestion-prod`. Every new data-set gets passed to the next step of the pipeline.

2) __ConstructDatabaseOutputOperations__: At this step we are preparing all
necessary operations that has to be executed on our SQL database to apply
the new object-oriented object from PubSub. Therefore we are using two
data-transfer-objects: `InputOperationDto` and `OutputOperationDto`.
We are parsing the input from PubSub into the `InputOperationDto` and
converting it then to multiple encapsulated `OutputOperationDto` which
do contain all SQL write/delete operations.

3) __ApplyOutputOperationsToDatabase__: Finally we are applying
all prepared `OutputOperationDto` to the database.

## Detailed communication flow diagram

I created a detailed communication flow diagram with the most important classes
and their methods and properties:

![detailed communication flow diagram](docs/img/detailed-communication-flow-diagram.svg)
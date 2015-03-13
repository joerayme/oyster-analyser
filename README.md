# oyster-analyser

Performs analysis on a set of Oyster data exports.

## Installation

    $ lein uberjar

## Usage

Run the analysis on a set of csv files:

    $ java -jar target/uberjar/oyster-analyser-0.1.0-SNAPSHOT-standalone.jar <file> [<file2> ...]


## Options

Takes a set of CSV files to analyse

## Examples

    $ java -jar target/uberjar/oyster-analyser-0.1.0-SNAPSHOT-standalone.jar *.csv

     Total Duration       788
     Avg. Duration      24.63
     Total Cost       £137.35
     Avg. Cost          £1.78
     Journeys              77

### Planned features

* Most used transport type
* Spend per week
* Min, max, average journey times

## License

Copyright © 2015 Joe Ray

Distributed under the Eclipse Public License either version 1.0 or any later version.

# oyster-analyser

[![Build Status](https://travis-ci.org/joerayme/oyster-analyser.svg?branch=master)](https://travis-ci.org/joerayme/oyster-analyser)

Performs analysis on a set of Oyster data exports.

## Build

    $ lein uberjar

## Usage

Run the analysis on a set of csv files:

    $ java -jar target/uberjar/oyster-analyser-uber.jar <file> [<file2> ...]


## Options

Takes a set of CSV files to analyse

## Examples

    $ java -jar target/uberjar/oyster-analyser-uber.jar *.csv

     From 01 Jan 2015 to 28 Feb 2015

        Total Duration   13.13 hrs
         Avg. Duration  24.63 mins
      Shortest Journey     10 mins
       Longest Journey     58 mins
            Total Cost     £137.35
             Avg. Cost       £1.78
              Journeys          77
     Most popular mode   bus (57%)

### Planned features

* Spend per week

## License

Copyright © 2015 Joe Ray

Distributed under the Eclipse Public License either version 1.0 or any later version.

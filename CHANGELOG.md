# changelog

This file summarizes **notable** changes for each release, but does not describe internal changes unless they are particularly exciting. For complete details please see the corresponding milestones and their associated issues.

## New and Noteworthy for Version 0.2.0-M3

A new feature, and quite a few upgrades. Only non-stable module is http4s since it does not have a stable yet. Many thanks to Gabriele Petronella for the Query Param Decoder Implementation

Features:

- Adds Query Param Decoder for Http4s [#69](https://github.com/ChristopherDavenport/fuuid/pull/69)

Upgrades:

- Scala 2.12.8/2.11.12
- Http4s 0.20.0-M4
- Circe 0.10.1
- Doobie 0.6.0
- Cats-Effect 1.1.0
- Specs2 4.3.5 (test-only)

Plugins:

- Kind Projector
- Tut
- ScalaJS

## New and Noteworthy for Version 0.2.0-M2

Restoration of the doobie project with the new release.

Upgrades:

- circe 0.10.0
- doobie 0.6.0-M3
- http4s 0.19.0-M3

## New and Noteworthy for Version 0.2.0-M1

Release of all core code on cats-effect 1.0, http4s 0.19.0-M2, circe 0.10.0-M2. As doobie
does not have a release at this time, this release does not include a doobie module.

Improvements:

- Refactor fromString to a Throwable rather the the more specific exception.

Upgrades:

- cats-effect 1.0.0
- circe 0.10.0-M2
- http4s 0.19.0-M2

## New and Noteworthy for Version 0.1.1

Bug Fixes:

- Fixes for a possible escape to the total function if error was not one of the expected checked exceptions.

## New and Noteworthy for Version 0.1.0

- Initial Encoding
- Circe Module
- Doobie Module
- Http4s Module

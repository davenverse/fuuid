# changelog

This file summarizes **notable** changes for each release, but does not describe internal changes unless they are particularly exciting. For complete details please see the corresponding milestones and their associated issues.

## New and Notworthy for Version 0.2.0-M2

Restoration of the doobie project with the new release.

Upgrades:

- circe 0.10.0
- doobie 0.6.0-M3
- http4s 0.19.0-M3

## New and Notworthy for Version 0.2.0-M1

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

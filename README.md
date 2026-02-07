# Cauldron - A Minecraft VTT

CauldronVTT, or Cauldron, is a Fabric mod for Minecraft that acts as a way to create and run virtual encounters for TTRPG systems (D&D, Pathfinder, etc.).


There is currently no official launch/build of the project.


## Basic setup

### The Encounter

Any player with operator permissions can create an "Encounter". An Encounter is an encapsulating term for all events, tokens and data that would be associated with a combat or battle.

In order for a battle to run, an encounter must be created first with the following command:

``/cauldron encounter create <ID>``

To assign GM tokens to an encounter you must be actively modifying it, which can be done with the modify subcommand.

``/cauldron encounter modify <ID>``

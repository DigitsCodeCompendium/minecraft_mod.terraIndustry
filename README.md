# Terra Industry

Terra Industry is a NeoForge 1.21.1 mod for chunk-local refineries. A controller owns
all refinery ports in its chunk; a port in an adjacent chunk is deliberately ignored.
The controller and all ports are unbreakable in survival. Controllers rediscover their
ports every second, which handles ports placed after the controller.

## Included starter refinery

The **Terra Industry** creative tab contains an **Iron Refinery** and one basic version of
every port. Place the Iron Refinery, a Basic Material Port, Basic Fuel Port, and Basic Product
Port, and Catalyst Blocks anywhere in the same chunk. Its refinery-owned operating policy
consumes one coal every 120 ticks. On each completed cycle, every nearby Catalyst Block has a
10% chance to convert one nearby stone block into iron ore.

## Interfaces

Right-click a controller to see its active status, connected-port count, and cycle progress.
Right-click a port to open its storage interface. Item ports show the recipe’s required item
or produced item at the top and the real storage slot below it. Fluid-configured ports instead
show a 16,000 mB gauge, the required/produced fluid, and the currently stored fluid type.

## KubeJS refinery registration

The mod does not require KubeJS, but KubeJS can call its public Java bridge during a
startup script. This example delays a refinery until the stated UTC instant, then runs
twice each UTC day:

```js
// kubejs/startup_scripts/terraindustry_refineries.js
const RefineryDefinitions = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.RefineryDefinitions'
)

const RefineryResource = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.RefineryResource'
)
const CatalystTransformationRecipe = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.CatalystTransformationRecipe'
)
const CatalystOutcome = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.CatalystTransformationRecipe$Outcome'
)
const CatalystCrystallizationRecipe = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.CatalystCrystallizationRecipe'
)
const RefineryOperatingRate = Java.loadClass(
  'com.digitscodecompendium.terraindustry.refinery.RefineryOperatingRate'
)

RefineryDefinitions.register(
  'terraindustry:basic_crude',
  '2026-09-01T00:00:00Z',
  ['06:00-10:00', '18:30-23:00'],
  20, // cycle length in ticks
  [new CatalystTransformationRecipe('minecraft:stone', new CatalystOutcome('minecraft:iron_ore', 0.10))],
  RefineryOperatingRate.everyTicks(RefineryResource.item('minecraft:coal', 1), 20),
  null, // optional coolant profile, reserved for coolant mechanics
  [new CatalystCrystallizationRecipe('minecraft:iron_ore', 'minecraft:amethyst_cluster', 0.10)]
)
RefineryDefinitions.setDefault('terraindustry:basic_crude')
```

Use `null` for no delayed start. A schedule of `00:00-00:00` is active all day. Windows
such as `22:00-02:00` cross midnight. `setDefault` selects the recipe used by unconfigured
controllers. A refinery definition contains one or more catalyst-transformation recipes. At
the end of a fueled, scheduled cycle, each Catalyst Block finds one nearby matching input block
for each recipe and rolls the listed output chances. Fuel is an operating property of the
refinery definition, not a transformation recipe input.

Crystallization recipes select a matching block type and a crystal block. On a successful roll, a
crystallization recipe places its crystal on one random exposed face of the selected block. Vanilla
amethyst clusters are supported out of the box.

Fuel and Modifier Ports expose item storage; the Coolant Port exposes liquid storage. Modifier
and coolant effects are intentionally not processed yet, as their separate mechanics still need
their design pass.

## Effects block

The **Effects Block** is available in the Terra Industry creative tab. It uses the required
[Cascade](https://modrinth.com/mod/cascademc) library to continuously emit a layered sonic-pulse ring.
Only players with permission level 2
(operators/admins) can open its configuration screen. The screen accepts a final radius in blocks,
a fade exponent, the pulse travel time in seconds, and a six-digit hex color such as `#35D4FF`.
Settings are stored on each placed block and validated by the server.

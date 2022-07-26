# Item Subs

A Minecraft mod created during [Forgejam 2022](https://blog.minecraftforge.net/announcements/forgejam22/).
The mod adds a "submarine" that can be used for subaquatic item transport, among other things.

## Installation

First install [1.19 Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.19.html).
This mod will work on the latest version (i.e, [post](https://github.com/MinecraftForge/MinecraftForge/pull/8786)-[refactor](https://github.com/MinecraftForge/MinecraftForge/pull/8840)²), provided no other exciting breaking changes snuck in (unlikely, but you never know.)

Next, download the mod.
Presumably Forge will be providing built versions of the mod (otherwise why require the source.)
Regardless, you can find prebuilt binaries on the [releases page](https://github.com/EnderTurret/ItemSubs/releases).

There is also a demo world with the release that you can download which demonstrates various features of the mod.

## Content

### The submarine

This new "block" (it's an entity) has 18 slots you can stash your stuff in.

You place it in a station and when that's given a redstone pulse, the submarine begins to move!
The direction the submarine moves in can be controlled using relays, and its path terminates at the next station block.

Submarines can also be shift-right-clicked to start and stop moving,
which can be useful if you forgot a relay somewhere and the submarine decides to locate the nearest stronghold instead of transporting your items.
Additionally, submarines will stop moving if they detect something blocking their path.

Not only that, but you can place a submarine outside of water (disabled by default), which allows you to use the submarine for parkour or player transportation
(comparable to slime block flying machines, but slightly less prone to putting you in the backrooms.)
This behavior is disabled by default, as it's moving away from the idea of subaquatic item transport.
Regardless, you can enable it if these features intrigue you.

Submarines require fuel to move, similar to furnace minecarts, but slightly more over-engineered.
Under default settings, a submarine will move up to 5 blocks for every item the fuel would smelt (in a standard furnace).
This means that a single coal causes it to move up to 40 blocks, a lava bucket is 500 blocks, etc.
This fuel is not consumed when the submarine isn't moving, so you don't have to worry about submarines idling in stations eating through all your hard-earned Blazing Pyrotheum.
As implied, the number of blocks moved per item smelted is configurable.

Lastly, if you set the tag `decorative: 1b` in the entity or item data (achieved through commands or custom recipes),
it will cause the submarine to become "decorative."
This removes its inventory and fuel slots, allowing it to move without needing fuel, but also removing its ability to transport items.
This is useful if you want to have decorative submarines in your base, or if you're building a weird parkour map.
This feature is unavailable in survival by default because you can still ride submarines, allowing for the world's cheapest slime block flying machine.
You can still make it obtainable through custom recipes.

### Station blocks

Station blocks mark the beginning and ending of submarine paths.
Submarines that enter stations will stop until they're either right-clicked or given a redstone signal.

Hoppers can withdraw/deposit items into docked submarines.
The block face → submarine inventory slot mappings should be obvious.

Station blocks also support comparator output:
* A signal of 0 indicates no submarine is docked
* A signal of 1 indicates a submarine is docked and its inventory is partially full
* A signal of 2 indicates a submarine is docked and its inventory is empty
* A signal of 3 indicates a submarine is docked and its inventory is full

### Relays

Relays cause submarines that move above them to turn towards the direction the relay is facing.
When given a redstone signal, relays will switch directions, like rails.

Similarly to stations, relays support comparator output:
* A signal of 0 indicates no submarine is above them
* A signal of 1 indicates a submarine is above them
* A signal of 2 indicates a submarine is above them and it has just finished turning
	* Sometimes this doesn't work for some reason.

### Speed upgrades

Normally, submarines are rather slow (a measly .5 blocks/sec).
This can be mitigated through the use of speed upgrades.
Each speed upgrade increases its speed by .25 blocks/sec, up to a max of 4.5 blocks/sec (16 upgrades).

As a warning, high speeds are relatively untested and may have problems such as:
* Submarines ignoring relays
* Submarines ignoring stations
* Submarines moving through blocks
I'm pretty sure I've fixed these problems, but they may show up again.

Another note: sometimes submarines with speed upgrades will move seemingly erratically.
This issue is caused by a lack of movement processing on the client.
This seems to be mitigated by opening its inventory, at least from what I've tested.

## Configuration

Currently, there are a few configuration options for adapting the mod to whatever environment you're dropping it in.
You can find the config in the `serverconfig` folder in your world (under the name `itemsubs-server.toml`.)

## Limitations

Unlike minecarts, submarines cannot easily go in reverse.
This is because they'll just hit the last relay and immediately u-turn.
This cannot easily be fixed without modifying fundamental mechanics (which can't be done now, as there's not enough time).
Worst case scenario here you'll just have to create two paths.

The other main limitation is a lack of submarines chunk loading their path.
This is unimplemented because chunk loading is rather complicated and because it's not actually a feature other vanilla item transport has.
If you need this, there are quite a few chunk loader mods (or there would be, if any of them were up to date.)
<!-- It's kind of funny actually. I looked for chunk loader mods and the only up-to-date one was Mekanism. -->
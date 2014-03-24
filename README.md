WhoBannedMe
===========

###Description

WhoBannedMe interfaces with the <a href="http://fishbans.com/">FishBans API</a> to let you know how many global bans a user has.  When a player connects, other users can be notified if the connecting user has any global bans from the various ban systems Fishbans pulls data from.

These services include:
* McBouncer
* Minebans
* McBlockIt 
* Glizer

***

###Permissions
<table>
<tr><td><code>whobannedme.exempt</code></td><td>Allows user to bypass ban lookup on connect</td></tr>
<tr><td><code>whobannedme.notify</code></td><td>Users will be notified when a connecting player has more than the minimum-bans count</td></tr>
<tr><td><code>whobannedme.notify.all</code></td><td>Users will be notified of all connecting players</td><tr>
</table>

***

###Configuration

<pre>#Configuration file for WhoBannedMe.

#Minimum number of bans before triggering message to players with whobannedme.notify
minimum-bans: 3

#Output scan results to console?
console-output: false

#Debug mode.  This will output lots of text to the console.
debug-mode: true
</pre>

***

###Source

Find our source on <a href="https://github.com/FearFree/WhoBannedMe">Github!</a>

###Notes

**Data collection**

RandomGift uses Plugin Metrics that collects anonymous statistic data about the
plugins usage and sends it to [http://mcstats.org/plugin/WhoBannedMe](http://mcstats.org/plugin/whobannedme)

**Planned Additions**

* Ability to run lookups on username in-game.
* Configurable option to list detailed ban information in game

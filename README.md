# Free GPS Tracker  - Android app

https://free-gps-tracker.appspot.com/

Track your GPS for free!


My fork of fab https://github.com/herverenault/Self-Hosted-GPS-Tracker/
See also similar app to http://sourceforge.net/projects/gpsmapper/


## How does it work?

    android GPS -> google app engine web backend -> html and js map


## My modification to Self-Hosted-GPS-Tracker

* POST json
* added timestamp, speed, altitude
* preferences validation
* class names changed
* android studio project structure
* send positions to free-gps-tracker.appspot.com by default


## TODO
* if network is not available save position to local DB and send it later
* refactor buildPositionMsgFromCurrLocation to a command class?
* fixup commands tracker start, tracker stop
* update screen with latest position


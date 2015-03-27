# Free GPS Tracker  - Android app

https://free-gps-tracker.appspot.com/

Track your GPS for free!


My fork of fab https://github.com/herverenault/Self-Hosted-GPS-Tracker/
See also similar app to http://sourceforge.net/projects/gpsmapper/


## How does it work?

    android GPS -> google app engine web backend -> html and js map


## My modification of Self-Hosted-GPS-Tracker

* POST json
* added timestamp
* url validation
* class names changed
* android studio project structure


## TODO
* move url from main screen to settings
* add configurable params field to settings form for device_id, user_token
  and more
* add altitude
* if network is not available save position to local DB and send it later

# Free GPS Tracker  - Android app

https://free-gps-tracker.appspot.com/

Track your GPS for free!


My fork of fab https://github.com/herverenault/Self-Hosted-GPS-Tracker/
See also similar app to http://sourceforge.net/projects/gpsmapper/


## How does it work?

    Free GPS Tracker Android -> Free GPS Tracker website


## My modification to Self-Hosted-GPS-Tracker

* POST json
* added timestamp, speed, altitude
* preferences validation
* class names changed
* send positions to free-gps-tracker.appspot.com by default
* renamed classes and refactor here and there
* added unittests with mockito and robolectric

## TODO
* if network is not available save position to local DB and send it later
* refactor buildPositionMsgFromCurrLocation to a command class?
* fixup commands tracker start, tracker stop
* update screen with latest position


## License

Copyright (C) 2015

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>



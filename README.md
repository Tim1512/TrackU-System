# TrackU-System
Folder TrackUService is the TrackU service code
Floder WifiLocRecoder is the TrackU client side App

Our TrackU server deployed on Tomcat Sevlet originally. In order to facilitate the reader to experience our system, now the client side App wil write the meta-data on the external storage.

After the TrackU APP starts running, it will generate a "LocationWifi.txt" file on external storage.
Copy the "LocationWifi.txt" file to PC, and replace the string FILE_INPUT to the meata-data file name in /TrackUservice/src/iie/wxy/TrackU.jave. Then run the server code, will output a KML file to list the user's daily schedule.

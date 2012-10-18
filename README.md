telemetry-servlet
=================

Telemetry Servlet


What it Does:
---------------------------

Saves received telemetry events and ouputs them to csv files stored somewhere in the file system.


How to Build:
---------------------------

* ant jar will build the servlet jar
* ant war will create the war
* ant deploy-war will deploy the war to the tomcat folder specified in build.properties

If you want to change the location where the csv files are saved, change the csv.file.path property
in build.properties and recreate the webapp.






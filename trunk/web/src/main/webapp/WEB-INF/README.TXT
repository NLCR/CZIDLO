#######################################
#######################################
##    URN:NBN Resolver version 2.3   ##
#######################################
#######################################

This archive contains files for installation of URN:NBN Resolver system.
Resolver is authority over global persistent identifiers URN:NBN for single national space 
and should be therefore run by particular national library. 

Main featurese are:
- resolving the URN:NBN for given nation space (i. e. redirecting to digital libraries or showing metadata of digital document in user friendly form or in xml).
- redirecting according to access origin (HTTP header REFERER)
- assigning URN:NBN identifiers for given subspace manually or by means of REST API
- resposibility for subspaces delagated to organisations yet Resolver is still global authority
- user accounts management and accounts assigned to subspaces
- OAI-PMH provider
- possible to edit metadata manually though web interface 

New features in version 2.3
- new API (v3), old API (v2) still available
- all API v3 responses can be validated by Xml Schema provided
- new namespace for input/outpud data for API v3
- digital document registration is more defensive - in some cases of invalid/long data where the operation would be denied the operation now passes and ignors/shortes that data
- more allowed characters for registrar-scope identifiers (all reserved and unreserved url characters according to RFC 3986)
- some minor changes in URLs for API v3
- some API v3 responses contain more relavant data in order to avoid unnecessary secondary API operations in typical use cases


System is being developed under GNU GPL v3 licence by National Library of the Czech Republic.
http://nkp.cz/
Source codes, built software and documentation is available on google code.
http://code.google.com/p/urnnbn-resolver-v2/
This document describes installation of this system on Linux server. 
Since all the middleware (database, servlet container, web server) is available on multiple platforms,
it should be possible to install the system on different platform. 
This has not been tested though and will not be explained here.



############################
Installation
############################

Prerequisites:
- Apache Tomcat in version 6 and higher. Possibly other servlet container, but that has not been tested.
- PostgreSQL in version 8 or higher.
- It is recommended to also use Apache HTTP Server as frontend for Tomcat. This is typical solution for production deployment.

This archive should contain these files:
- README.TXT - this file
- web.war - web interface module
- api.war - API module  
- OaiPmhProvider.war - OAI-PMH provider module
- initDatabase.sql - sql script for intializing the database
- upgradeDatabase.sql - sql script for upgrading database from previous versions (2.1, 2.2)


1. Provided you have database installed and properly configured, you should first run the initDatabase.sql (e. g. by psql) 
in order to create tables, sequences and indexes.
Script also creates one administrator account (admin:adminPassword). 
Since it is not possible yet to change user password, it is very important that this account is removed 
immediatelly after another administrator account (with publicly unknown password) is created. 


2. Next step is installation of the three web applications. That is done simply by copying web.war, api.war and OaiPmhProvider.war
int $TOMCAT_HOME/webapps. Applications are independent so you can choose from multiple deployment options. For example:
- web.war + api.war - if OAI-PMH functionality is not desired
- api.war only - if only resolving and importing records through API is requiered
- each module in different Tomcat (possibly different machines) - for better scalability


3. All modules must be connected to single database, that has been set and configured in step 1. 
The database connection pool is looked up by JNDI. So this resource must be defined in either:
- $TOMCAT_HOME/conf/context.xml - globally for all applications
- $TOMCAT_HOME/webapps/$APPLICATION_NAME/META-INF/context.xml - for each single application inside of deployed application
- $TOMCAT_HOME/conf/Catalina/localhost/$APPLICATION_NAME.xml - for each single application outside of deployed application
For more information see http://tomcat.apache.org/tomcat-7.0-doc/config/context.html.
Context definition looks this way:

<Context antiJARLocking="true" path="/api">
 <Resource auth="Container"
  driverClassName="org.postgresql.Driver"
  maxActive="100" maxIdle="30" maxWait="200"
  name="jdbc/postgres"
  username="testuser"
  password="testpass"
  type="javax.sql.DataSource"
  url="jdbc:postgresql://localhost:5432/resolver"
  />
</Context>

Bear in mind that there is allways default context.xml in each war 
so it will be copied into $TOMCAT_HOME/webapps/$APPLICATION_NAME/META-INF/context.xml when the application is deployed.


4. Each of three web applications has its own configuration. Some of the bundle keys are shared among them but them.

-------------------
A. api.war:
-------------------

Configuration file is $TOMCAT_HOME/webapps/api/WEB-INF/classes/api.properties. Defined properties are:

#if this is set to true the application will not allow imports and other data updates
resolver.readOnly=false

#if running in development mode. default value is false
resolver.develMode=false

#two-letter language code in URN:NBN schema according to ISO 3166, e. g. "cz"
#ATTENTION: 
this configuration is not used in current version of API module and language code is firmly set to "cz" in code.
resolver.languageCode=cz

#maximal number of prined urn:nbn records for operation GET /v2/registrars/{REGISTRAR_CODE}/urnNbnReservations
#if number of records is higher than this only the number itself is printed
api.getReseravations.maxReservedToPrint=100

#default number of records returned by operation POST /v2/registrars/{REGISTRAR_CODE}/urnNbnReservations
api.reservation.defaultSize=10

#maximal number of records returned by operation POST /v2/registrars/{REGISTRAR_CODE}/urnNbnReservations
api.reservation.maxSize=100

-------------------
A. web.war:
-------------------

Configuration file is $TOMCAT_HOME/webapps/web/WEB-INF/classes/web.properties. Defined properties are:

#if this is set to true the application will not allow imports and other data updates
resolver.readOnly=false

#if running in development mode. default value is false
resolver.develMode=false


-------------------
A. OaiPmhProvider.war:
-------------------

Configuration file is $TOMCAT_HOME/webapps/OaiPmhProvider/WEB-INF/classes/provider.properties. Defined properties are:

#two-letter language code in URN:NBN schema according to ISO 3166, e. g. "cz"
resolver.languageCode=cz

#name of repository  (will be present in response of Identify operation)
provider.repositoryName=URN:NBN Resolver for language code 'cz'

#oai provider base url (will be present in response of Identify operation)
provider.baseUrl=http://localhost:8080/OaiPmhProvider/provider
#provider.baseUrl=http://resolver.nkp.cz/OaiPmhProvider/provider

#url of web interface of oai provider (will be present in response of Identify operation)
provider.webUrl=http://localhost:8080/OaiPmhProvider/web
#provider.webUrl=http://resolver.nkp.cz/OaiPmhProvider/web

#earliest datestamp (will be present in response of Identify operation)
provider.earliestDatestamp=2010-10-06T00:00:00Z

#email of administrator (will be present in response of Identify operation)
provider.adminEmail=rehan@mzk.cz

#maximal number of records returned by operation ListIdentifiers
#if result is bigger, resumption token is returned
provider.ListIdentifiers.maxSize=100

#maximal number of records returned by operation ListRecords
#if result is bigger, resumption token is returned
provider.ListRecords.maxSize=50

#number of minutes after which the ListIdentifier or ListRecord request is dropped
#if not refreshed by following request with resumptionToken
provider.ListRequests.minutesValid=5


################################
Upgrade from version 2.1 and 2.2
################################

New features require that database be upgraded. 
This should be realized by running script upgradDatabase.sql (for instance by psql).
It is recommended to backup your database before that just in case something goes wrong.
After running the upgrade sql script it should suffice to replace applications (api, web and OaiPmhProviders) with their new versions.


############################
Logging
############################

Standard java.util.logging is used as logging framework. 
Loggers are allways named after full names of classes (sometime superclasses) that create logs.
So it is possible to append handlers according to the package hierarchy. E. g.:
- cz.nkp.urnnbn.core.persistence for module persistence (orm)
- cz.nkp.urnnbn.services for service layer
- cz.nkp.urnnbn.rest for API module
- cz.nkp.urnnbn.oaipmhprovider for OAI-PMH provider

After clear tomcat installation all the application logs go to $TOMCAT_HOME/logs/catalina.out,
where you can for example find out, that the database connection is not configured correctly.



##############################
Apache HTTP Server as frontend
##############################

In production deployment it is required that applications are available on port 80 (instead of typical 8080 of Tomcat).
Tomcat itself usually doesn't run under root and cannot therefore occupy ports lower than 1024.

Typical solution to this is to use Apache HTTP Server (or other similar system) as a front-end
and Tomcat (or other servlet container) as back-end. 
Web browsers then communicate with Apache on port 80. Apache sends the packets to Tomcat usually by protocol AJP.
In this scenario Tomat doesn't have to be visible from outer world. 

There are mutliple Apache modules that can be used for Apache->Tomcat communication - mod_proxy_ajp, mod_jk, ...

Apache can also perform other preprocessing of packets.
- HTTPS support
- redirection from HTTP to HTTPS where desired
- this type of redirection: http://myresolverdomain.cz/urn:nbn:cz:aba001-000001 --> http://mydomain.cz/api/v2/resolver/urn:nbn:cz:aba001-000001
- load balancing among more Tomcat instances

Apache is thought to be more secure than Tomcat because of its wider usage.
Also there are some API operations, that require SSL. 
If you wont to use API for automated access (authorized operations like data imports) you need to establish HTTPS somehow.


############################
Usage
############################

Without any redirection and with Tomcat only the applications are available (for domain maydomain.cz) here:
http://mydomain.cz:8080/web - Web interface intended for human access.
http://mydomain.cz:8080/api - REST API for automated imports. Also performs resolving.
http://mydomain.cz:8080/OaiPmhProvider - OAI-PMH provider with simple web interface.

There should allways be available installation of National Library of the Czech Republic at http://resolver.nkp.cz.

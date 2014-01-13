#######################################
#######################################
##         CZIDLO version 4.2        ##
#######################################
#######################################

This archive contains files for installation the CZIDLO (CZech IDentification and LOcalization Tool based on URN:NBN) system.
CZIDLO is authority over global persistent identifiers URN:NBN for single national space 
and should be therefore run only by particular national library. 

Main featurese are:
- resolving the URN:NBN for given nation space (i. e. redirecting to digital libraries or showing metadata of digital document in user friendly form or in xml).
- redirecting according to access origin (HTTP header REFERER)
- assigning URN:NBN identifiers for given subspace manually or by means of REST API
- resposibility for subspaces delagated to organisations yet Resolver is still global authority
- user accounts management and accounts assigned to subspaces
- OAI-PMH provider
- possible to edit metadata manually though web interface, managing processes on server through web interface

New features in version 4.2
- bug fixes
- passwords from now on stored more securely as hash of password concancated with random salt
- fulltext web search though only withing title data
- for more see https://code.google.com/p/czidlo/wiki/Changelog#4.2

System is being developed under GNU GPL v3 licence by National Library of the Czech Republic.
http://nkp.cz/
Source codes, built software and documentation is available on google code.
http://code.google.com/p/czidlo/
This document describes installation of this system on Linux server. 
Since all the middleware (database, servlet container, web server) is available on multiple platforms,
it should be possible to install the system on different platform. 
This processed has not been tested though and will not be explained here.


############################
Installation
############################

Prerequisites:
- PostgreSQL database in version 8 or higher.
- Apache Tomcat in version 6 and higher. Possibly other servlet container, but that has not been tested.
- It is recommended to also use Apache HTTP Server as frontend for Tomcat. This is typical solution for production deployment.

This archive should contain these files:
- README.TXT - this file
- web.war - web interface module
- api.war - API module  
- oaiPmhProvider.war - OAI-PMH provider module
- processDataServer.war - application to access logs and outputs of processes
- initDatabase_4.2.sql - sql script for intializing the database (only the core database, does NOT include database for processes and OAI Adapter xsl transformations)
- updateDatabase-2.0-2.2_to_2.3-3.0.sql - sql script for upgrading core database (from CZIDLO versions 2.0, 2.1 or 2.2 to versions 2.3, 2.4 or 3.0)
- updateDatabase-2.3-3.0_to_4.1.sql - sql script for upgrading core database (from versions 2.3, 2.4 or 3.0 to version 4.1)
- updateDatabase_4.1_to_4.2.sql - sql script for updating core database (from version 4.1 to current version 4.2). 
It is NOT sufficient only to run this script to update database. Complete database upgrade is described below.
- databaseUpgrader-4.2.jar - java program that replaces plaintext passwords with their encrypted form.


1. Provided you have database installed and properly configured, you should first run the initDatabase_4.2.sql (e. g. by psql) 
in order to create tables, sequences and indexes.
Script also creates one administrator account (admin:adminPassword). 
Since it is not possible yet to change user password, it is very important that this account is removed 
immediatelly after another administrator account (with publicly unknown password) is created. 


2. Next step is installation of the four web applications. That is done simply by copying web.war, api.war, oaiPmhProvider.war
and processDataServer.war into $TOMCAT_HOME/webapps. Applications are independent so you can choose from multiple deployment options. For example:
- web.war + processDataServer.war + api.war - if OAI-PMH functionality is not desired
- api.war + processDataServer.war only - if only resolving and importing records through API is required
- each module in different Tomcat (possibly different machines) - for better scalability


3. Each of the modules must be connected to single database, that has been set and configured in step 1. 
The database connection pool is looked-up by JNDI. So this resource must be defined in either:
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
  url="jdbc:postgresql://localhost:5432/czidlo_core"
  />
</Context>

Bear in mind that there allways exists default context.xml in each war 
so it will be copied into $TOMCAT_HOME/webapps/$APPLICATION_NAME/META-INF/context.xml when the application is (re)deployed.

4. All modules installed on the same machine should set property resolver.admin.logFile to the same file so that admin logs of all modules
are accessible through web interface.

5. Web applications web and processDataServer need that database for processes and OAI Adapter xsl transformations is initialized. 
Applications access this database by hibernate (unlike core databese where pure JDBC is used). 
Access to this database is configured in file hibernate.cfg.xml (in $APPLICATION_RULE/WEB-INF/classes). 
Required tables in this database can be initialized this way:
 - set property hibernate.hbm2ddl.auto to create in hibernate.cfg.xml in deployed application web (<property name="hibernate.hbm2ddl.auto">create-drop</property>)
 - make sure application web is reloaded, for example by restarting application server
 - now the tables should be created by hibernate
 - remove property hibernate.hbm2ddl.auto from hibernate.cfg.xml
Without correct connection to this database the application "web" will behave incorrectly.

6. Each of three applications has its own configuration. Some of the bundle keys are shared among them.


############################
Configuration
############################

Configuration of each module can be found in $TOMCAT_HOME/webapps/$APPLICATION_NAME/WEB-INF/classes.
Default version of configuration files are contained within application and explained in comments.
Configuration files are:
------------------------
API: api.properties
WEB: web.properties, quartz.properties, hibernate.cfg.xml
OAI_PMH_PROVIDER: provider.properties
PROCESS_DATA_SERVER: processDataServer.properties, hibernate.cfg.xml


#############################
Upgrade from previous version
#############################
Previous versions of CZIDLO (formerly called URN:NBN Resolver) may or may not differ in core database structure.
There is also new database required since version 3.0. It stores data of external processes and xsl transformations for process OAI Adapter. 
Initialization of this database is described in section installation (item 5) of this document. 
Concrete actions needed to upgrade database(s) to current version 4.2 are described bellow.
You should allways backup your database before upgrading it in order to avoid data loss if somthing goes wrong.

Apart from that, applications need to be replaced with newer versions.
This will probably require fixing configuration files again, since application server will probably replace these files with default ones from war archives.


########################
Upgrade from version 4.1
########################

Core database
=============
1. Use script updateDatabase_4.1_to_4.2.sql. You may need to manually change owner of table ie_title and view ie_title_view.
2. Use databaseUpgrader-4.2.jar to replace plaintext passwords with generated salt and salted password hash. 
Usage:
USAGE: java -jar databaseUpgrader.jar HOST PORT DATABASE LOGIN PASSWORD

Process database
================
No need to upgrade.


########################
Upgrade from version 3.0
########################

Core database
=============
Proceed as described in "Upgrade from version 4.1" section of this document.

Process database
================
No need to upgrade.


################################
Upgrade from versions 2.3 or 2.4
################################

Core database
=============
1. Use script updateDatabase-2.3-2.4_to_3.0-4.1.sql to update to version 4.1 compatible database.
2. Proceed as described in "Upgrade from version 4.1" section of this document.

Process database
================
Database needs to be created and initalized same way as in current version. 
See installation section of this document.


#####################################
Upgrade from versions 2.2, 2.1 or 2.0
#####################################

Core database
=============
1. Use script updateDatabase-2.0-2.2_to_2.3-3.0.sql to update to version 2.3 compatible database.
2. Use script updateDatabase-2.3-2.4_to_3.0-4.1.sql to update to version 4.1 compatible database.
3. Proceed as described in "Upgrade from version 4.1" section of this document.

Process database
================
Database needs to be created and initalized same way as in current version. 
See installation section of this document.


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

There is also special admin logger, that logs selected action. It's logs are written to file, that is configurable in each module configuration.


##############################
Apache HTTP Server as frontend
##############################

In production deployment it is required that applications are available on port 80 (instead of typical 8080 for Tomcat).
Tomcat itself usually doesn't run as root user and cannot therefore occupy ports lower than 1024.

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
http://mydomain.cz:8080/oaiPmhProvider - OAI-PMH provider with simple web interface.

There should allways be available CZIDLO installation of National Library of the Czech Republic at http://resolver.nkp.cz.


README file for CZIDLO v5.0

Copyright (C) 2013-2018 Martin Řehánek


#####################################
#####################################
#         CZIDLO version 5.0        #
#####################################
#####################################

This archive contains files for the CZIDLO (CZech IDentification and LOcalization Tool based on URN:NBN) system installation.

CZIDLO is authority over global persistent identifiers URN:NBN for single national space and should be therefore run only by particular national library.

System is being developed under GNU GPL v3 licence by [National Library of the Czech Republic](http://nkp.cz/).
Source codes, built software and documentation is available on [google code](http://code.google.com/p/czidlo/).

This document describes installation of this system on Linux server.

Since all the middleware (database, servlet container, web server) is available on multiple platforms, it should be possible to install the system on different platform.

This processed has not been tested though and will not be explained here.


###################
## Main features ##
###################

- resolving URN:NBN identifiers for national space (i. e. redirecting from stable URN:NBN-based URL to volatile URL in digital library).
- redirecting according to access origin (HTTP header REFERER)
- assigning URN:NBN identifiers for given subspace manually (web interface) or through REST API
- resposibility for subspaces delegated to organisations yet Resolver is still global authority
- user accounts management and accounts assigned to subspaces
- OAI-PMH provider
- web interface for manual data manipulation, managing processes on server, etc.


####################
### New features ###
####################

Version 4.0:
- manual changing order of registrars/archiver in web interface
- hidden registrars/archivers in web interface
- content of tabs "info" and "rules" can be edited through web interface
- application "web" can now also be deployed in read-only mode
- login page configurable, which is usefull for more complicated system deployments
- digital instances editable through web interface
- more parameters for process Export URN:NBN - so far we could restrict records produced only by registrar
- CZIDLO version in web interface header
- whole multimodule system properly mavenized

Version 4.1:
- fixed bug in web search caused by premature deployment of fulltext features
- web dialog for scheduling Export URN:NBN process slightly enhanced
- fixed incorrect handling of parameter missingISBN for scheduling process Export URN:NBN

Version 4.2:
- bug fixes
- passwords from now on stored more securely as hash of password concancated with random salt
- fulltext web search though only withing title data
- for more see https://code.google.com/p/czidlo/wiki/Changelog#4.2

Version 4.2.1:
- fixed bug https://code.google.com/p/czidlo/issues/detail?id=38

Version 4.2.2:
- urn:nbn deactivation implemented in API v3. With or without note, without predecessor(s)/successor(s).

Version 4.3:
- API v4: all GET operations results also in json (only xml up to now); operation to update digital document and intelectual entity records (only empty fields)
- new process to check availability of digital instances
- more fields indexed for searching, only title information up to now, all the metadata from now on.
- basic Google Analytics integration
- charts for urn:nbn assignment/resolvation statistics

Version 4.4:
- own search solution consisting of database tables, views, functions triggers has been replaced with Apache Solr
- added reverse resolving, i.e. finding URN:NBN by URL, throw API or web interface
- some tuning of web interface
- OAI Adapter reworked, source code and report cleaned up. Parameters changed, enabled merging of digital instances.

Version 4.4.1
- fixed bug in scheduling OAI Adapter from web interface

Version 4.5
- increased max length of registrar-scope id value frome 60 to 80
- updated XSL templates for OAI Adapter

Version 4.6
- rearranged web process management, process scheduling dialogs
- added registrar-scope identifier manipulation through web interface
- spring-security upgrade (3.1.0 -> 4.2.9)
- updated login/user account web UI
- added web option to change password (by logged user or admin)

Version 4.6.1
- fix bug in statistics (urn:nbn assignments)

Version 5.0
- see https://github.com/NLCR/CZIDLO/wiki/P%C5%99ehled-zm%C4%9Bn#50


##################
## Installation ##
##################

### Prerequisites ###

- PostgreSQL database in version 8 or higher.
- Apache Tomcat in version 6 or higher. Possibly other servlet container, but that has not been tested.
- It is recommended to also use Apache HTTP Server as frontend for Tomcat. This is typical solution for production deployment.

This archive should contain following files:
- `README.rm` - this file
- `web.war` - web interface module
- `api.war` - API module
- `oaiPmhProvider.war` - OAI-PMH provider module
- `processDataServer.war` - application to access logs and outputs of processes
- `initDatabase_5.0.sql` - sql script for database initialization (only the core database, does NOT include database for processes and OAI Adapter xsl transformations)
- `updateDatabase-2.0-2.2_to_2.3-3.0.sql` - sql script for upgrading core database (from CZIDLO versions 2.0, 2.1 or 2.2 to versions 2.3, 2.4 or 3.0)
- `updateDatabase-2.3-3.0_to_4.1.sql` - sql script for upgrading core database (from versions 2.3, 2.4 or 3.0 to version 4.1)
- `updateDatabase_4.1_to_4.2.2.sql` - sql script for updating core database (from version 4.1 to version 4.2.2).
- `databaseUpgrader-4.2.2.jar` - java program that replaces plaintext passwords with their encrypted form.
- `updateDatabase_4.2.2_to_4.3.sql` - sql script for updating core database (from version 4.2.2 to version 4.3).
- `updateDatabase_4.3_to_4.4-4.6.sql` - sql script for updating core database (from version 4.3 to versions 4.4, 4.4.1, 4.5 and 4.6).
- `updateDatabase_4.4-4.6_to_4.6.1.sql` - sql script for updating core database (from versions versions 4.4, 4.4.1, 4.5 and 4.6 to version 4.6.1).
- `updateDatabase_4.6.1_to_5.0.sql` - sql script for updating core database (from version 4.6.1 to version 5.0).
- `solr-7.2.1-czidlo.zip` - zip archive containing solr server with CZIDLO configuration
- `cliUtils.jar` - command line application with some utilities

It is NOT sufficient only to run this script to update database. Complete database upgrade is described below.

### Process ###

1. Provided you have database installed and properly configured, you should first run the `initDatabase_5.0.sql` script (e. g. by psql) in order to create tables, sequences and indexes.

   Script also creates one administrator account (admin:admin).
   It is very important that this account is removed immediately after another administrator account (with publicly unknown password) is created. Or at least the password for user 'admin' should be changed.


2. Next step is the installation of the four web applications. That is done simply by copying `web.war`, `api.war`, `oaiPmhProvider.war` and `processDataServer.war` into `$TOMCAT_HOME/webapps`.
   Applications are independent so you can choose from multiple deployment options. For example:

   - `web.war` + `processDataServer.war` + `api.war` - if OAI-PMH functionality is not desired
   - `api.war` + `processDataServer.war` only - if only resolving and importing records through API is required
   - each module in different Tomcat (possibly different machines) - for better scalability


3. All the applications must be connected to shared database, that has been set and configured in step 1.

   The database connection pool is looked-up by JNDI. So this resource must be defined in either:

      - `$TOMCAT_HOME/conf/context.xml` - globally for all applications
      - `$TOMCAT_HOME/webapps/$APPLICATION_NAME/META-INF/context.xml` - for each single application inside of deployed application
      - `$TOMCAT_HOME/conf/Catalina/localhost/$APPLICATION_NAME.xml` - for each single application outside of deployed application

   For more information see http://tomcat.apache.org/tomcat-7.0-doc/config/context.html.

   Context definition looks this way:

   ```xml
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
   ```

   Bear in mind that there always exists default context.xml in each war
   so it will be copied into `$TOMCAT_HOME/webapps/$APPLICATION_NAME/META-INF/context.xml` when the application is (re)deployed.

4. Application run on same server should set property `resolver.admin.logFile` to the same file so that admin logs of all modules
   are accessible through web interface.

5. Web applications `web` and `processDataServer` need that database for processes and OAI Adapter xsl transformations is initialized.

   Applications access this database by hibernate (unlike core database where pure JDBC is used).

   Access to this database is configured in file `hibernate.cfg.xml` (in `$APPLICATION_RULE/WEB-INF/classes`).

   Required tables in this database can be initialized this way:

      - set property `hibernate.hbm2ddl.auto` to create in `hibernate.cfg.xml` in deployed application web (`<property name="hibernate.hbm2ddl.auto">create-drop</property>`)
      - make sure application web is reloaded, for example by restarting application server
      - now the tables should be created by hibernate
      - remove property `hibernate.hbm2ddl.auto` from `hibernate.cfg.xml`

   Without correct connection to this database the application "web" will behave incorrectly.

6. Each of three applications has its own configuration. Some of the bundle keys are shared among them.


###################
## Configuration ##
###################

Configuration of each module can be found in $TOMCAT_HOME/webapps/$APPLICATION_NAME/WEB-INF/classes.

Default version of configuration files are contained within application and explained in comments.

### Configuration files are: ###

- API: `api.properties`
- WEB: `web.properties`, `quartz.properties`, `hibernate.cfg.xml`
- OAI_PMH_PROVIDER: `provider.properties`
- PROCESS_DATA_SERVER: `processDataServer.properties`, `hibernate.cfg.xml`

Note: Keys in configuration properties files have been renamed since version 4.2.2. Also some new properties were introduced.
So don't rely on simply copying configuration from previous version. Migration should be straightforward.
See https://github.com/NLCR/CZIDLO/commit/20543990df5132b156426f61ae5024ba4f2ef0b1.


###################################
## Upgrade from previous versions ##
####################################

Previous versions of CZIDLO (formerly called URN:NBN Resolver) may or may not differ in core database structure.

There is also new database required since version 3.0. It stores data of external processes and xsl transformations for process OAI Adapter.

Initialization of this database is described in section installation (item 5) of this document.

Concrete actions needed to upgrade database(s) to current version are described bellow.

You should always backup your database before upgrading it in order to avoid data loss if something goes wrong.

Apart from that, applications need to be replaced with newer versions.
This will probably require fixing configuration files again, since application server will probably replace these files with default ones from war archives.

##################################
### Upgrade from version 4.6.1 ###
##################################

#### Core database ####

1. Use script `updateDatabase_4.6.1_to_5.0.sql`. Be sure to run this script as user that has all necessary rights (creating, deleting and updating databases, indexes, views, functions, triggers).
Typically something like this: `psql czidlo_core czidlo_user <./updateDatabase_4.6.1_to_5.0.sql` with czidlo_core being name of database and czidlo_user being user that is owner of the database.

#### Process database ####

No upgrade needed.

#### Solr server ####

No upgrade needed.

##################################
### Upgrade from version 4.6   ###
##################################

#### Core database ####

1. Use script `updateDatabase_4.4-4.6_to_4.6.1.sql`. Be sure to run this script as user that has all necessary rights (creating, deleting and updating databases, indexes, views, functions, triggers).
Typically something like this: `psql czidlo_core czidlo_user <./updateDatabase_4.4-4.6_to_4.6.1.sql` with czidlo_core being name of database and czidlo_user being user that is owner of the database.

#### Process database ####

No upgrade needed.

#### Solr server ####

No upgrade needed.


##################################
### Upgrade from version 4.5   ###
##################################

#### Core database ####

Proceed as described in "Upgrade from version 4.6" section of this document.

#### Process database ####

No upgrade needed.

#### Solr server ####

No upgrade needed.


##################################
### Upgrade from version 4.4.1 ###
##################################

#### Core database ####

Proceed as described in "Upgrade from version 4.6" section of this document.

#### Process database ####

No upgrade needed.

#### Solr server ####

No upgrade needed.


##################################
### Upgrade from version 4.4   ###
##################################

#### Core database ####

Proceed as described in "Upgrade from version 4.6" section of this document.

#### Process database ####

No upgrade needed.

#### Solr server ####

No upgrade needed.


##################################
### Upgrade from version 4.3   ###
##################################

#### Core database ####

1. Use script `updateDatabase_4.3_to_4.4-4.5.sql`. Be sure to run this script as user that has all necessary rights (creating, deleting and updating databases, indexes, views, functions, triggers).
Typically something like this: `psql czidlo_core czidlo_user <./updateDatabase_4.3_to_4.4-4.5.sql` with czidlo_core being name of database and czidlo_user being user that is owner of the database.
There is significantly less data processing compared to 4.2.2->4.3 database upgrade. Upgrading database with 1 mil digital documents shouldn't last more than few seconds.
2. Proceed as described in "Upgrade from version 4.6" section of this document.

#### Process database ####

No upgrade needed.

#### Solr server ####

Proceed with Solr installation as described in chapter Solr installation.


##################################
### Upgrade from version 4.2.2 ###
##################################

#### Core database ####

1. Use script `updateDatabase_4.2.2_to_4.3.sql`. Be sure to run this script as user that has all necessary rights (creating, deleting and updating databases, indexes, views, functions, triggers).
Typically something like this: `psql czidlo_core czidlo_user <./updateDatabase_4.2.2_to_4.3.sql` with czidlo_core being name of database and czidlo_user being user that is owner of the database.
Since there is nontrivial amount of data being processed here (precomputed tables for search) this can take a while. Upgrading database with 1 mil digital documents lasted about 20 minutes on average computer.
2. Proceed as described in "Upgrade from version 4.3" section of this document.

#### Process database ####

No upgrade needed.


##################################
### Upgrade from version 4.2.1 ###
##################################

#### Core database ####

Proceed as described in "Upgrade from version 4.2.2" section of this document.

#### Process database ####

No upgrade needed.


################################
### Upgrade from version 4.2 ###
################################

#### Core database ####

Proceed as described in "Upgrade from version 4.2.2" section of this document.

#### Process database ####

No upgrade needed.


################################
### Upgrade from version 4.1 ###
################################

#### Core database ####

1. Use script `updateDatabase_4.1_to_4.2.2.sql`. You may need to manually change owner of table `ie_title` and view `ie_title_view`.
2. Use `databaseUpgrader-4.2.2.jar` to replace plaintext passwords with generated salt and salted password hash.
- USAGE: `java -jar databaseUpgrader-4.2.2.jar HOST PORT DATABASE LOGIN PASSWORD`
3. Proceed as described in "Upgrade from version 4.2.2" section of this document.


#### Process database ###

No upgrade needed.


################################
### Upgrade from version 3.0 ###
################################

#### Core database ####

Proceed as described in "Upgrade from version 4.1" section of this document.

#### Process database ####

No upgrade needed.


########################################
### Upgrade from versions 2.3 or 2.4 ###
########################################

#### Core database ####

1. Use script `updateDatabase-2.3-2.4_to_3.0-4.1.sql` to update to version 4.1 compatible database.
2. Proceed as described in "Upgrade from version 4.1" section of this document.

#### Process database ####

Database needs to be created and initialized same way as in current version.
See installation section of this document.


#############################################
### Upgrade from versions 2.2, 2.1 or 2.0 ###
#############################################

#### Core database ####

1. Use script `updateDatabase-2.0-2.2_to_2.3-3.0.sql` to update to version 2.3 compatible database.
2. Use script `updateDatabase-2.3-2.4_to_3.0-4.1.sql` to update to version 4.1 compatible database.
3. Proceed as described in "Upgrade from version 4.1" section of this document.

#### Process database ####

Database needs to be created and initialized same way as in current version.
See installation section of this document.


########################
##  Solr installation ##
########################

If you are installing CZIDLO version 4.4+ or upgrading from version 4.3 to 4.4, you must install Solr server for web search:
1. unpack file `solr-7.2.1-czidlo.zip` into directory from now on called SOLR_HOME
2. start Solr server with `$SOLR_HOME/bin/solr start`
3. check that Solr server is running correctly with `$SOLR_HOME/bin/solr status`
4. create preconfigured czidlo solr-core with `$SOLR_HOME/bin/solr create -c czidlo -d $SOLR_HOME/server/solr/czidlo -n solrconfig.xml`
5. stop Solr server with `$SOLR_HOME/bin/solr stop`
6. enable security configuration by renaming file `$SOLR_HOME/server/solr/security.json.disabled` to `$SOLR_HOME/server/solr/security.json`
7. change default login and password for solr indexer (czidloIndexer:czidloRolls) in all configuration files
in properties `indexer.solr.login`, `indexer.solr.password`, `process.solrIndexer.login` and `process.solrIndexer.password`.
8. use `cliUtils.jar` to generate credentials for new login and pasword and change configuration file `$SOLR_HOME/server/solr/security.json` accordingly:
for example: `java -jar cliUtils.jar build_solr_basic-auth-plugin_credentials login password`
9. update `$SOLR_HOME/server/solr/security.json` with new credentials
10. start Solr server with `$SOLR_HOME/bin/solr start`

Solr server needn't be visible to the client browser, application `web` works as a proxy. 
When all applications are properly deployed (especially web, api) you should schedule indexation process for all the content.


#############
## Logging ##
#############

Standard java.util.logging is used as logging framework.

Loggers are always named after full names of classes (sometime superclasses) that create logs.

So it is possible to append handlers according to the package hierarchy. E. g.:

- `cz.nkp.urnnbn.core.persistence` for module persistence (orm)
- `cz.nkp.urnnbn.services` for service layer
- `cz.nkp.urnnbn.rest` for API module
- `cz.nkp.urnnbn.oaipmhprovider` for OAI-PMH provider

After clear tomcat installation all the application logs go to `$TOMCAT_HOME/logs/catalina.out`, where you can for example find out, that the database connection is not configured correctly.

There is also special admin logger, that logs selected action. It's logs are written to file, that is configurable in each module configuration.


####################################
## Apache HTTP Server as frontend ##
####################################

In production deployment it is required that applications are available on port `80` (instead of typical `8080` for Tomcat).

Tomcat itself usually doesn't run as root user and cannot therefore occupy ports lower than `1024`.

Typical solution to this is to use Apache HTTP Server (or other similar system) as a front-end and Tomcat (or other servlet container) as back-end.

Web browsers then communicate with Apache on port `80`. Apache sends the packets to Tomcat usually by protocol AJP.

In this scenario Tomcat doesn't have to be visible from outer world.

There are multiple Apache modules that can be used for Apache->Tomcat communication - `mod_proxy_ajp`, `mod_jk`, ...

Apache can also perform other preprocessing of packets.

- HTTPS support
- redirection from HTTP to HTTPS where desired
- this type of redirection: http://myresolverdomain.cz/urn:nbn:cz:aba001-000001 --> http://mydomain.cz/api/v6/resolver/urn:nbn:cz:aba001-000001
- load balancing among more Tomcat instances

Apache is thought to be more secure than Tomcat because of its wider usage.
Also there are some API operations, that require SSL.

If you wont to use API for automated access (authorized operations like data imports) you need to establish HTTPS somehow.


###########
## Usage ##
###########

Without any redirection and with Tomcat only the applications are available (for domain maydomain.cz) here:

- http://mydomain.cz:8080/web - Web interface intended for human access.
- http://mydomain.cz:8080/api - REST API for automated imports. Also performs resolving.
- http://mydomain.cz:8080/oaiPmhProvider - OAI-PMH provider with simple web interface.

There should always be available CZIDLO installation of National Library of the Czech Republic at http://resolver.nkp.cz.

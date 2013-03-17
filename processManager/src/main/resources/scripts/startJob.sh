#!/bin/bash
#################################
#starts program and returns pid #
#################################
#
###############
#paramethers: #
###############
# 1 - spousteny jar
# napr. /home/martin/NetBeansProjects/process/target/process-2.3-jar-with-dependencies.jar
#
# 2 - config file for external process
# napr. /home/martin/NetBeansProjects/process/src/main/resources/process.properties
#
#
# 3 - process uuid
# napr. 111
#
# 4 - process type
# napr. TEST
#
# 5-... - dalsi - argumenty procesu

#ulozi do promenne string pro spusteni JAR archivu s parametry 
#(jar i parametry jsou parametry skriptu)
RUN="java -jar $@"

#echo $RUN
#$@ &

#spusti proces v pozadi
$RUN &

#vrati id procesu spusteneho v pozadi
echo $!

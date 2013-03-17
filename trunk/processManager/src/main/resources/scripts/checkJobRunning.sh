#!/bin/bash
#skript konstroluje, jestli proces(job) identifikovaný PIDem (=jobId) běží
#jediny argument je PID

PID=$1
if [ -e /proc/$PID ]; then
  echo RUNNING
else
  echo NOT_RUNNING
fi

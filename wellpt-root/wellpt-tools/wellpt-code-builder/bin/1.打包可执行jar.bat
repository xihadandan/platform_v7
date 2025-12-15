@echo off
title package
cd ..
call mvn clean assembly:assembly -Dmaven.test.skip=true

pause
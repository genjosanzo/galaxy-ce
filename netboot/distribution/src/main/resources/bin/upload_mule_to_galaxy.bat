@echo off

setlocal

set MULE_BOOTSTRAP_HOME=%~dp0..
set GALAXY_LIB=%MULE_BOOTSTRAP_HOME%\lib\galaxy

set cp=.;%MULE_BOOTSTRAP_HOME%\conf;%MULE_BOOTSTRAP_HOME%\lib\opt\groovy-all-1.5.4.jar;%MULE_BOOTSTRAP_HOME%\lib\boot\commons-cli-1.0.jar
java -Dgalaxy.lib=%GALAXY_LIB% -Dmule.home=%MULE_HOME% -cp "%cp%" org.codehaus.groovy.tools.GroovyStarter --main PopulateGalaxy --conf %MULE_BOOTSTRAP_HOME%\bin\galaxy_launcher.conf %*
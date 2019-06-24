@echo off

@rem Assumes the following directory structure.
@rem   javasee/     => The top dir
@rem     lib/       => .jar file
@rem     bin/       => executable files

set TOPDIR=%~dp0..
java -jar "%TOPDIR%\lib\JavaSee-all.jar" %*

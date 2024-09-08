@echo off

rem
rem  Set maximum memory for JVM heap
rem
set "JAVA_OPTS= -Xmx2048m"

"java.exe" %JAVA_OPTS% -cp "p2rank_2.4.1/bin/p2rank.jar;p2rank_2.4.1/bin/lib/*" cz.siret.prank.program.Main %*
@echo off
setlocal
cd /d "%~dp0"

set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.1"
set "CATALINA_HOME=C:\Users\hp\.gemini\antigravity\tools\apache-tomcat-10.1.25"

echo =========================================================================
echo   Starting PulseFit Online Fitness Tracking Application...
echo   Server Port: 8080
echo   URL:         http://localhost:8080/
echo   Stop Server: Press Ctrl+C in this terminal
echo =========================================================================

call "%CATALINA_HOME%\bin\catalina.bat" run

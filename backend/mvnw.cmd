@REM Maven Wrapper for Windows
@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

"%JAVA_HOME%\bin\java.exe" ^
  -jar "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %*

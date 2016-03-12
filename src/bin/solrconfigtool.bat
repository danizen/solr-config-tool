@REM # 
@REM # Invokes solr-config-tool Java program
@REM #
@ECHO OFF

SET TOOLDIR=%~dp0
SET TOOLJAR=solr-config-tool-${project.version}.jar

IF DEFINED JAVA_HOME (
	SET "JAVA=%JAVA_HOME%\bin\java"
) ELSE (
	SET "JAVA=java"
)

%JAVA% -jar %TOOLDIR%lib\%TOOLJAR% %*



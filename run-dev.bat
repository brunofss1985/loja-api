@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Iniciando aplicacao com Maven Wrapper corrigido...
mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
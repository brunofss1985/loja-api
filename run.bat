@echo off

rem 1. Obtém o nome da branch atual do Git
for /f "delims=" %%i in ('git rev-parse --abbrev-ref HEAD') do set BRANCH=%%i

echo Branch atual e: %BRANCH%

rem 2. Mapeia a branch para um perfil do Spring
if "%BRANCH%"=="main" (
    set SPRING_PROFILE=prod
) else if "%BRANCH%"=="master" (
    set SPRING_PROFILE=prod
) else (
    set SPRING_PROFILE=dev
)

echo Iniciando a aplicacao com o perfil: %SPRING_PROFILE%

rem 3. Encontra o arquivo JAR da sua aplicação
for /f "delims=" %%i in ('dir /b target\*.jar') do set JAR_FILE=%%i

if "%JAR_FILE%"=="" (
    echo Erro: Arquivo JAR nao encontrado em 'target\'. Certifique-se de que a aplicacao foi compilada (mvn package).
    exit /b 1
)

rem 4. Executa a aplicação Spring Boot com o perfil ativo
java -jar target\%JAR_FILE% --spring.profiles.active=%SPRING_PROFILE%
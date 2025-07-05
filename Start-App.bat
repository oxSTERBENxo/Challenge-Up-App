@echo off
cd /d %~dp0

REM Set variables for paths
set SRC=BackEnd\src\main\java
set LIBS=BackEnd\libs\gson-2.13.1.jar

REM Compile all java files
javac -cp "%LIBS%" -d %SRC%\classes %SRC%\org\example\*.java %SRC%\org\example\*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Run Java server with classpath including gson and compiled classes folder
start /B java -cp "%SRC%\classes;%LIBS%" org.example.Server
start "" "http://localhost:5005"

REM Run Blazor app
dotnet run --project ChallengeUpFrontend\ChallengeUpFrontend.csproj


REM Kill the server process as before
for /f "tokens=2 delims=," %%a in ('wmic process where "CommandLine like '%%org.example.Server%%'" get ProcessId /format:csv ^| findstr /i "java"') do taskkill /PID %%a /F

pause

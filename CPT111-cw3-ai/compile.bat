@echo off
cd /d %~dp0
echo Compiling Java files...

if defined JAVAFX_HOME (
    echo Detected JAVAFX_HOME: %JAVAFX_HOME%
    set "JAVAFX_LIB=%JAVAFX_HOME%\lib"
    javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls -encoding UTF-8 -d . src\*.java
) else (
    javac -encoding UTF-8 -d . src\*.java
)

if %errorlevel% == 0 (
    echo Compilation successful!
) else (
    echo Compilation failed. Please check error messages.
    pause
    exit /b 1
)
pause


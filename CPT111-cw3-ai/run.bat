@echo off
cd /d %~dp0
echo Running Movie Recommendation & Tracker GUI...

if defined JAVAFX_HOME (
    set "JAVAFX_LIB=%JAVAFX_HOME%\lib"
    java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls MovieApp
) else (
    java MovieApp
)

if %errorlevel% neq 0 (
    echo.
    echo Failed to launch the GUI. Please ensure JavaFX is installed.
    echo If you are using JDK 11 or above, set the JAVAFX_HOME environment variable to the JavaFX SDK folder.
    echo Alternatively, you can still run the console version with: java Main
)
pause


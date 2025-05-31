@echo off
setlocal

REM Set the path to vcvarsall.bat directly
set "VCVARSALL=F:\Visual Studio\VC\Auxiliary\Build\vcvarsall.bat"

if not exist "%VCVARSALL%" (
    echo Could not find vcvarsall.bat at "%VCVARSALL%".
    exit /b 1
)

REM Call vcvarsall.bat to set up the MSVC environment with the x86_amd64 argument
call "%VCVARSALL%" x86_amd64
if errorlevel 1 (
    echo Failed to call vcvarsall.bat
    exit /b %errorlevel%
)

REM Run Maven build explicitly in the same shell
cmd /c mvn clean install
exit /b %errorlevel%


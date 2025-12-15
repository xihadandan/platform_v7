

@echo off

echo ----------------install dependency for all wellapp module----------------
echo begin to install for module, started with 'wellapp-'

set web_path=%cd%
set root_path=%web_path%\..
pause

for /f "delims=*" %%i in ('dir/b/ad "%root_path%\wellapp*"') do call :ss %%i
goto :eof

:ss
set moudle_name=%1
set module_path=%root_path%\%moudle_name%

echo ------install %moudle_name% module dependency---------
cd %module_path%
del package-lock.json
@REM del node_modules

call npm i

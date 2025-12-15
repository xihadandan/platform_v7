
@echo off
echo ----------------install dependency for all wellapp module----------------
echo begin to install for module, started with 'wellapp_'
set web_path=%cd%
set root_path=%web_path%\..
for /f "delims=*" %%i in ('dir/b/ad %root_path%\wellapp*') do call :ss %%i
goto :eof
:ss
set moudle_name=%1
if %moudle_name% == wellapp-web goto :eof

set bb=%root_path%\%moudle_name%

echo ------install  %moudle_name% module dependency---------
call npm_install %bb%
cd %web_path%
call npm_link %bb%
goto :eof


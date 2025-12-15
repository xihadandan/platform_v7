set web_path=%cd%
set root_path=%web_path%\..

for /f "delims=*" %%i in ('dir/b/ad "%root_path%\wellapp*"') do call :link_package_item %%i
goto :eof

:link_package_item
set moudle_name=%1
set module_path=%root_path%\%moudle_name%
echo ------ link package: %moudle_name% ---------
cd %module_path%
call npm run npm-link
goto :eof

@echo off
set module_path=%1
if "%module_path%"=="" set module_path=%cd%
cd %module_path%
del  package-lock.json
call %module_path%\..\wellapp-web\del_module_dep %module_path%

npm i



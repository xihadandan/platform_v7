@echo off
set module_path=%1

if "%module_path%"=="" goto :link_all

npm link %module_path%
exit

:link_all
  npm link ../wellapp-framework ../wellapp-page-assembly ../wellapp-admin ../wellapp-for-prod ../wellapp-dyform ../wellapp-workflow ../wellapp-mobile ../wellapp-theme





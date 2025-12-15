@echo off

set current_path=%1
echo -----------begin to delete wellapp module dependency for module %current_path%
for /f "delims=*" %%i in ('dir/b/ad %current_path%\node_modules\wellapp*') do call :ff %%i
goto :eof
:ff
set aa=%current_path%\node_modules\%1
echo delete folder : %aa%
rd /S /Q %aa%
echo end to delete folder
goto :eof

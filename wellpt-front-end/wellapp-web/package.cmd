@echo off
set /p env=please input environment(unittest or prod or local):
echo %env% > config/env
npm install --production & tar -zcvf ../release.tgz --exclude=./.svn .
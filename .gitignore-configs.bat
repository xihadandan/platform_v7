@echo off
REM 忽略本地配置文件，保留服务端
git update-index --assume-unchanged wellpt-root/wellpt-web/pom.xml 
pause
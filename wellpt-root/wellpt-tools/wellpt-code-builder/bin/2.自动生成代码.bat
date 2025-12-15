@echo off
chcp 65001
title 创建项目结构

for /f "tokens=1,2 delims==" %%i in (config.properties) do (
if "%%i"=="dbUrl" set dbUrl=%%j
if "%%i"=="dbUser" set dbUser=%%j
if "%%i"=="dbPassword" set dbPassword=%%j
if "%%i"=="tables" set tables=%%j
if "%%i"=="javaPackage" set javaPackage=%%j
if "%%i"=="outputDir" set outputDir=%%j
if "%%i"=="author" set author=%%j
)
if defined dbUrl (
echo 数据库url = %dbUrl%) else ( 
echo dbUrl不能为空 
goto :end
)
if defined dbUser (
echo 数据库用户名 = %dbUser%
) else (
echo dbUser不能为空
goto :end)
if defined dbPassword (
echo 数据库密码 = %dbPassword%
) else (
echo dbPassword不能为空
goto :end)
if defined tables (
echo 数据库表 = %tables%
) else (
echo tables不能为空
goto :end)
if defined javaPackage (
echo 代码包目录 = %javaPackage%
) else (
echo javaPackage不能为空
goto :end)


echo 作者 = %author%

echo 开始自动生成代码

java -Dfile.encoding=utf-8 -jar ..\target\wellpt-code-builder-RELEASE.jar


echo 结束自动生成代码

:end
pause
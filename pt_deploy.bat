rem wellpt-root

cd wellpt-root
call mvn clean
call mvn deploy
cd ..


rem wellpt-ptprd

cd wellpt-ptprd
call mvn clean
call mvn deploy
cd ..


rem wellpt-resources

cd wellpt-root/wellpt-modules/wellpt-resources
call mvn clean
call mvn deploy
cd ../../../


rem wellpt-security-implement

cd wellpt-root/wellpt-modules/wellpt-security-implement
call mvn clean
call mvn deploy
cd ../../../


rem wellpt-security-oauth2

cd wellpt-root/wellpt-modules/wellpt-security-oauth2
call mvn clean
call mvn deploy
cd ../../../


rem wellpt-session

cd wellpt-root/wellpt-modules/wellpt-session
call mvn clean
call mvn deploy
cd ../../../


rem wellpt-web

cd wellpt-root/wellpt-web
call mvn clean
call mvn deploy -P res-deploy
cd ../../


pause

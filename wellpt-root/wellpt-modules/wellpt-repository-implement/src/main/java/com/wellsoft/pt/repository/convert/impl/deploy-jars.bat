echo off
REM:部署jar
mvn deploy:deploy-file -DgroupId=suwell.ofd.custom -DartifactId=agent-wrapper -Dversion=1.2.17.1103 -Dpackaging=jar -Dfile=.\lib\agent-wrapper-1.2.17.1103.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"
mvn deploy:deploy-file -DgroupId=suwell.ofd.custom -DartifactId=packet-wrapper -Dversion=1.6.18.0105 -Dpackaging=jar -Dfile=.\lib\packet-wrapper-1.6.18.0105.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"
mvn deploy:deploy-file -DgroupId=suwell.ofd.custom.agent -DartifactId=http-agent -Dversion=1.1.17.628 -Dpackaging=jar -Dfile=.\lib\http-agent-1.1.17.628.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"
REM:部署javadoc
REM:mvn deploy:deploy-file -DgroupId=suwell.ofd.custom.agent -DartifactId=http-agent -Dversion=1.1.17.628 -Dpackaging=jar -Dfile=.\lib\http-agent-1.1.17.628-javadoc.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"
REM:mvn deploy:deploy-file -DgroupId=suwell.ofd.custom -DartifactId=agent-wrapper -Dversion=1.2.17.1103 -Dpackaging=jar -Dfile=.\lib\agent-wrapper-1.2.17.1103-javadoc.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"
REM:mvn deploy:deploy-file -DgroupId=suwell.ofd.custom -DartifactId=packet-wrapper -Dversion=1.6.18.0105 -Dpackaging=jar -Dfile=.\lib\packet-wrapper-1.6.18.0105-javadoc.jar -Durl=http://nexus.well-soft.com:81/nexus/content/repositories/thirdparty -DrepositoryId="Well-Soft-Releases"



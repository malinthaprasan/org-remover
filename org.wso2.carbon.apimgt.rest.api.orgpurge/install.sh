#!/bin/bash

TARGET=/home/malintha/wso2apim/cur/choreo-apim/orgRemTest/wso2am-4.0.0-SNAPSHOT/repository/deployment/server/webapps
mci
rm -r $TARGET/org-purge.war
rm -r $TARGET/org-purge
mv target/org-purge.war $TARGET

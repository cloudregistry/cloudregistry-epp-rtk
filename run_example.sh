#!/bin/bash

USAGE="usage: $0 epp_host_name epp_host_port epp_client_id epp_password domain_name contact_id"

EPP_HOST=$1
EPP_PORT=$2
CLIENT_ID=$3
PASSWORD=$4
DOMAIN=$5
CONTACT_ID=$6

if [ "x$RTK_HOME" = "" ]; then
	echo You must set RTK_HOME to point to the RTK installation directory
	exit 1
fi

if [ "x$CLOUDR_RTK_HOME" = "x" ]; then
	CLOUDR_RTK_HOME=`dirname $BASH_SOURCE`
fi

if [ "x$CONTACT_ID" = "x" ]; then
	echo $USAGE
	exit 1
fi

java -Dssl.props.location=$RTK_HOME/java/ssl \
     -Drtk.props.file=$RTK_HOME/java/etc/rtk.properties \
     -cp $RTK_HOME/java/lib/xerces.jar:$RTK_HOME/java/lib/epp-rtk-java.jar:$RTK_HOME/java/lib/regexp.jar:$RTK_HOME/java/lib/log4j.jar:$RTK_HOME/java/lib/bcprov-jdk14-115.jar:$CLOUDR_RTK_HOME/lib/cloudregistry-epp-rtk-0.1.jar \
     net.cloudregistry.rtk.epprtk.LaunchPhaseTest \
        $EPP_HOST $EPP_PORT $CLIENT_ID $PASSWORD $DOMAIN $CONTACT_ID

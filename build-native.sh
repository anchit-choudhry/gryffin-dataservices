#!/bin/bash
./mvnw clean install -U -Pnative,nativeTest native:compile -Dspring.profiles.active=local,mongo,native -Dskip-native-build=false

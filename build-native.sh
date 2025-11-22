#!/bin/bash
./mvnw clean -Pnative -Dspring.profiles.active=local,mongo,native -Dskip-native-build=false \
  native:compile package -U

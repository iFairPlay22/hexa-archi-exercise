#! /bin/bash

if [ "${MONGO_UPDATE}" = "true" ]; then

  mongo_ip=$(getent hosts "${MONGO_SERVICE}" | awk '{ print $1 }' | tail -n1)
  mongo_path="mongodb://${mongo_ip}:${MONGO_PORT}"

  echo "Restoring mongo in ${mongo_path}"
  mongorestore "${mongo_path}" "/mongo-dump" --nsInclude="*" --drop

fi

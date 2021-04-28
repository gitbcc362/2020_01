FROM mongo:3.6.0

WORKDIR /usr/src/configs

COPY replicaSet.js .
COPY setup.sh .

RUN ["chmod", "+x", "setup.sh"]

CMD ["./setup.sh"]
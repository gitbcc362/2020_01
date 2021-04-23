'use strict';

const {start} = require('./src/server');

const brokerName = process.argv[2];
const host = process.argv[3];
const port = process.argv[4];
const mainHost = process.argv[5];
const mainPort = process.argv[6];

start(host, port, {mainHost, mainPort, brokerName});

'use strict';

const net = require('net');

const debug = require('debug')('server:start');

const { connect } = require('../client');
const { SOURCE, MessageBuffer } = require('../utils');

const { onError, onClose, messageHandler } = require('./actions');

let isPrimary = true;
let clientSocket = null;
let server = null;
let name = '';
let serverAddress = '';
let serverPort = -1;
const mainBrokerAddress = '';
const mainBrokerPort = -1;
const buffer = new MessageBuffer('|');

async function start(host, port, { mainHost, mainPort, brokerName }) {
  debug('Starting server...');
  server = net.createServer();
  name = brokerName;
  serverAddress = host;
  serverPort = port;

  isPrimary = !(mainHost && mainPort);

  server.on('connection', onConnection);
  server.on('error', onError);
  server.on('close', onClose);

  server = await new Promise((resolve) => {
    server.listen({ port }, () => {
      debug(`Server listening on ${port}`);

      resolve(server);
    });
  });

  if (!isPrimary) {
    clientSocket = await informMainBroker(name, mainPort, mainHost);
  }

  return server;
}

function informMainBroker(brokerName, mainPort, mainHost) {
  return connect(brokerName, {
    port: mainPort,
    host: mainHost,
    source: SOURCE.BROKER,
    reconnect: true,
    selfServerHost: serverAddress,
    selfServerPort: serverPort,
  });
}

function onConnection(socket) {
  socket.on('data', (data) => {
    buffer.push(data);
    try {
      while (!buffer.isFinished()) {
        const { source, messageType, content } = buffer.handleData();

        messageHandler(source, messageType, content, socket, server);
      }
    } catch (error) {
      debug('erro na leitura dos dados', error.message);
    }
  });
}

module.exports = {
  start,
  server,
};

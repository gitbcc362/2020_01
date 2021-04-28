'use strict';

const net = require('net');

const debug = require('debug')('client:connection');

const { encode, decode, messageBuilder, MESSAGETYPE, SOURCE, handler, RESPONSES, MessageBuffer } = require('../utils');

const { onError } = require('./actions');

let clientSocket = null;

const maxRetry = 5;
let numberOfTries = 0;
let sktPort = 0;
let sktHost = '';
let clientName = '';
let delay = 2 ** numberOfTries * 1000;
let reconnecting = false;
let connected = false;
let shouldReconnect = true;
let sourc = null;
let mainServerAdress = '';
let mainServerPort = -1;
const optionalServers = [];
const buffer = new MessageBuffer('|');

async function connect(name, { port, host, source, reconnect = true, selfServerPort = -1, selfServerHost = '' }) {
  clientSocket = new net.Socket();
  clientName = name;
  sktPort = port;
  sktHost = host;
  sourc = source;
  shouldReconnect = reconnect;
  mainServerAdress = selfServerHost;
  mainServerPort = selfServerPort;
  Object.assign(clientSocket, { clientName });
  debug(`Connecting to ${host} at ${port}...`);

  clientSocket.on('data', onData);
  clientSocket.on('connect', onConnection);
  clientSocket.on('error', onError);
  clientSocket.on('close', onClose);
  clientSocket.on('ready', onReady);

  await createConnection({ port, host });

    // Gustavo
    console.log('>>>>>>>>>', optionalServers);
    // Gustavo

  return clientSocket;
}

function onReady() {
  informServer(sourc, mainServerPort, mainServerAdress);
}

function informServer(source, port = -1, host = '') {
  let infoMessage = messageBuilder[MESSAGETYPE.INFOCLIENT](clientName, source);

  if (SOURCE.BROKER === source) {
    infoMessage = messageBuilder[MESSAGETYPE.INFOBROKER](clientName, source, port, host);
  }

  clientSocket.write(encode(infoMessage));
}

function sendAsync(message) {
  clientSocket.write(encode(message));
}

function sendSyncMessage(message) {
  return new Promise((resolve, reject) => {
    setTimeout(reject, 10000, new Error({ message: 'ECONNREFUSED: Could not connect to server' }));
    clientSocket.once('dataReady', (data) => {
      debug('dataReady');
      console.log(decode(data));
      resolve(decode(data));
    });

    clientSocket.write(encode(message));
  });
}

function awaitResource() {
  return new Promise((resolve, reject) => {
    setTimeout(reject, 10000, { available: false });
    clientSocket.once('dataReady', (data) => {
      console.log(decode(data));

      resolve(decode(data));
    });
  });
}

function onClose() {
  if (!clientSocket.destroyed) {
    clientSocket.destroy();
  }

  if (shouldReconnect) {
    connected = false;
    reconnecting = true;
    debug('Connection lost, trying to reconect...');
    if (numberOfTries >= maxRetry) {
      reconnecting = false;
      throw new Error('Could not connect to server');
    }

    delay = 2 ** numberOfTries * 1000;
    numberOfTries += 1;

    let newBroker = null;

    if (optionalServers.length > 0) {
      newBroker = optionalServers.shift();
    }

    debug(`${optionalServers.length}`);

    if (newBroker) {
      sktPort = newBroker.port;
      sktHost = newBroker.host;
      debug(`New broker found at ${sktHost} and ${sktPort}`);
    }

    setTimeout(connect, delay, clientName, { port: sktPort, host: sktHost });
  }
}

function onConnection() {
  numberOfTries = 0;
  delay = 2 ** numberOfTries * 1000;
  reconnecting = false;
  connected = true;
}

function onData(data) {
  buffer.push(data);

  while (!buffer.isFinished()) {
    const { source, messageType, content } = buffer.handleData();

    if (!validadeData(source, messageType, content)) {
      console.log('dados invalidos');

      return;
    }

    const result = handler[messageType]({ content, source, socket: clientSocket, optionalServers });

    const builder = messageBuilder[RESPONSES[messageType]];

    if (builder) {
      const message = builder({ ...result });

      clientSocket.write(encode(message));
    }
  }
}

function validadeData(source, messageType, content) {
  return source && messageType && content;
}

function createConnection({ host, port }) {
  return new Promise((resolve) => {
    clientSocket.connect(port, host, () => {
      debug('Client connected!');

      resolve(clientSocket);
    });
  });
}

function isReconnecting() {
  return reconnecting;
}

function isConnected() {
  return connected;
}

function getCurrentDelay() {
  return delay;
}

module.exports = {
  connect,
  sendSyncMessage,
  sendAsync,
  awaitResource,
  isReconnecting,
  isConnected,
  getCurrentDelay,
  reconnecting,
  clientSocket,
};

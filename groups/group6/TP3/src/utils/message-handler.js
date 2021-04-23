'use strict';

const util = require('util');

const debug = require('debug')('server:message-handler');

const resource = require('../resource');

const { listaDeSocketsDeClientes, addLog, endLog, listaDeSocketsDeBrokers } = require('../server/db');

const { MESSAGETYPE, messageBuilder } = require('./message-builder');
const { encode } = require('./message-decoder');

function aquireHandler({ content }) {
  debug('aquireHandler');

  const { resourcePosition: idResource, clientId: idClient } = content;

  debug(`idResource ${idResource}, idClient ${idClient}`);

  addLog(`aquire de ${idClient}`);
  if (!resource.lockResource(idResource)) {
    resource.insertResourceQueue(idResource, idClient);

    addLog(`aquire de ${idClient} falhou`);
    endLog();

    return { available: false };
  }

  if (resource.checkOnQueue(idResource, idClient)) {
    resource.removeResourceQueue(idResource);
  }

  const recurso = resource.acquireResource(idResource);

  addLog(`aquire de ${idClient} conseguiu o recurso ${recurso}`);
  endLog();

  debug(`recurso ${recurso}`);

  return {
    resource: recurso,
    available: true,
  };
}

function ackHandler() {
  debug('ackHandler');

  debug('ACK OK');
}

function releaseHandler({ content }) {
  debug('release handler');

  const { resourcePosition: idRecurso, resource: recurso } = content;

  addLog(`release do recurso ${recurso}`);

  debug(`release ${idRecurso}`);

  const idClient = resource.removeResourceQueue(idRecurso);

  resource.print();

  addLog(`proximo da fila ${idClient}`);

  resource.updateResource(idRecurso, recurso);

  if (listaDeSocketsDeBrokers.size > 0) {
    const update = messageBuilder[MESSAGETYPE.UPDATERESOURCE]({
      resourcePosition: idRecurso,
      resource: recurso,
    });

    console.log(util.inspect(update));

    for (const [nome, obj] of listaDeSocketsDeBrokers.entries()) {
      debug(`atualizando broker ${nome}`);

      const { socket } = obj;

      socket.write(encode(update));
    }
  }

  if (!idClient) {
    resource.releaseResource(idRecurso);
    addLog(`nao tem proximo, libera recurso ${idRecurso}`);

    return {
      statusCode: 'ok',
    };
  }

  addLog(`enviando recurso pro ${idClient}`);

  const clientSocket = listaDeSocketsDeClientes.get(idClient);

  const response = messageBuilder[MESSAGETYPE.AQUIRERESPONSE]({ available: true, resource: recurso });

  clientSocket.write(encode(response));

  return {
    statusCode: 'ok',
  };
}

function infoClientHandler({ content, socket }) {
  debug('infoClientHandler');
  const { socketName } = content;

  debug(`nome do socket ${socketName}`);

  listaDeSocketsDeClientes.set(socketName, socket);

  return listaDeSocketsDeBrokers.toList();
}

function infoBrokerHandler({ content, socket }) {
  debug('infoBrokerHandler');
  const { socketName, port, host } = content;

  debug(`nome do socket ${socketName}`);

  listaDeSocketsDeBrokers.set(socketName, { port, host, socket });

  console.log(`lista de brokers ${util.inspect(listaDeSocketsDeBrokers.toList())}`);

  const listaDeBrokers = listaDeSocketsDeBrokers.toList();

  for (const [clientName, clientSocket] of listaDeSocketsDeClientes.entries()) {
    debug(`atualizando cliente ${clientName}`);

    const message = messageBuilder[MESSAGETYPE.UPDATEBROKER](listaDeBrokers);

    clientSocket.write(encode(message));
  }

  return listaDeSocketsDeBrokers.toList();
}

function updateBrokerHandler({ content, optionalServers }) {
  debug('updateBrokerHandler');
  console.log(`${util.inspect(content)}`);

  const { array } = content;

  for (const [, value] of Object.entries(array)) {
    optionalServers.push(value);
    console.log(value);
  }
}

function aquireResponseHandler({ content, socket }) {
  debug('aquireResponseHandler');

  socket.emit('dataReady', encode(content));
}

function updateResourceHandler({ content }) {
  debug('updateResourceHandler');

  const { resourcePosition: idRecurso, resource: recurso } = content;

  resource.updateResource(idRecurso, recurso);
}

const handler = {
  [MESSAGETYPE.AQUIRE]: aquireHandler,
  [MESSAGETYPE.ACK]: ackHandler,
  [MESSAGETYPE.RELEASE]: releaseHandler,
  [MESSAGETYPE.INFOCLIENT]: infoClientHandler,
  [MESSAGETYPE.INFOBROKER]: infoBrokerHandler,
  [MESSAGETYPE.UPDATEBROKER]: updateBrokerHandler,
  [MESSAGETYPE.AQUIRERESPONSE]: aquireResponseHandler,
  [MESSAGETYPE.UPDATERESOURCE]: updateResourceHandler,
};

module.exports = {
  handler,
};

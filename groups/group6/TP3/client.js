'use strict';

const debug = require('debug')('main:client');

const {connect, sendSyncMessage, awaitResource, isReconnecting, isConnected, getCurrentDelay, sendAsync} = require('./src/client');
const { resourcesLength } = require('./src/resource');
const { messageBuilder, MESSAGETYPE, SOURCE } = require('./src/utils');
const sleep = require('./src/utils/sleep');

const clientName = process.argv[2];
const host = process.argv[3];
const port = process.argv[4];

async function main() {
  await connect(clientName,{port,host, source: SOURCE.CLIENT});

  console.log('conectado');

  let shouldRun = true;

  while (shouldRun) {
  console.log('rodando');

    const resourcePosition = randomResource();

    const aquireMessage = messageBuilder[MESSAGETYPE.AQUIRE](resourcePosition, clientName);

    try {
      const recurso = await adquireRecurso(aquireMessage);

      // usa recurso
      await sleep(2000);

      const message = messageBuilder[MESSAGETYPE.RELEASE](resourcePosition, recurso);

      console.log('com recurso');

      sendAsync(message);
    } catch (error) {
      console.log(error);
        console.log(error);

        console.log('deu erro, reconectando', isReconnecting(), isConnected());

        while (isReconnecting() && !isConnected()) {
          const dlay = getCurrentDelay();

          console.log('reconectando', dlay);
          await sleep(dlay);
        }

        if (!isConnected()) {
          shouldRun = false;
        }
    }
  }
}

async function adquireRecurso(aquireMessage) {
  try {
    let run = false;
    let recurso = {};

    console.log('buscando recurso');

    do {
      const { resource, available} = await sendSyncMessage(aquireMessage);

      recurso = resource;

      if (!available) {
        debug('content not available, awaiting resource');
        const {resource, available} = await awaitResource();

        run = !available;
        recurso = resource;
      }
    } while (run);

    return recurso;
  } catch (error) {
    console.log('erro na espera de recurso', error);
  }
}

function randomResource(MIN = 0, MAX = resourcesLength) {
  return Math.floor(Math.random() * (MAX - MIN) + MIN);
}

main();

//cliente
// recurso = numero aleatorio de 0 a 10
// monto msg de pedido de recurso = {
//  posicaoDoRecurso: recurso,
//}
// peço recurso ate me liberar
// envio req
//  libero
// uso, altero faco qlqer coisa
// mando de volta atualizado
// msg de att do recurso = {
// posicaoDoRecurso: recurso
// recurso: recursoAtualizado
//}

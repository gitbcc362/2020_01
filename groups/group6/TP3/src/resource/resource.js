'use strict';

const debug = require('debug')('server:resource');

const resources = [
  { available: true, content: 'a', queue: [] },
  { available: true, content: 'b', queue: [] },
  { available: true, content: 'c', queue: [] },
  { available: true, content: 'd', queue: [] },
  { available: true, content: 'e', queue: [] },
  { available: true, content: 'f', queue: [] },
  { available: true, content: 'g', queue: [] },
  { available: true, content: 'h', queue: [] },
  { available: true, content: 'i', queue: [] },
  { available: true, content: 'j', queue: [] },
];

const resourcesLength = resources.length;

function print() {
  for (const resource of resources) {
    console.log(resource.content);
    for (const id of resource.queue) {
      console.log(id);
    }
  }
}

function checkavailable(idResource) {
  return resources[idResource].available;
}

function updateResource(idResource, content) {
  resources[idResource].content = content;
}

function insertResourceQueue(idResource, idClient) {
  if (!resources[idResource].queue.some((value) => value === idClient)) {
    debug(`inseri cliente ${idClient} na fila`);
    resources[idResource].queue.push(idClient);
  }
}

function checkOnQueue(idRecurso, idClient) {
  return resources[idRecurso].queue.some((value) => value === idClient);
}

function removeResourceQueue(idResource) {
  if (resources[idResource]) {
    debug('resource queue');

    return resources[idResource].queue.shift();
  }
}

function acquireResource(idResource) {
  return resources[idResource].content;
}

function lockResource(idResource) {
  if (resources[idResource].available) {
    debug(`resource ${resources[idResource].content} locked`);
    resources[idResource].available = false;

    return true;
  }

  return false;
}

function releaseResource(idResource) {
  debug(`resource ${resources[idResource].content} unlocked`);
  resources[idResource].available = true;
}

module.exports = {
  checkavailable,
  insertResourceQueue,
  updateResource,
  checkOnQueue,
  removeResourceQueue,
  acquireResource,
  lockResource,
  releaseResource,
  resourcesLength,
  print,
};

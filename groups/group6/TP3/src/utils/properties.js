'use strict';

const debug = require('debug')('properties:optional-servers');

let optionalServers = [];

function incluirBroker(port, host) {
  optionalServers.push({ port, host });
}
function substituiBroker(arrayBroker) {
  debug('optional servers updated');
  optionalServers = [];
  for (const [, value] of Object.entries(arrayBroker)) {
    optionalServers.push(value);
    console.log(value);
  }
  debug(`${optionalServers.length}`);
}
function getNext() {
  return optionalServers.shift();
}

module.exports = {
  optionalServers,
  incluirBroker,
  substituiBroker,
  getNext,
};

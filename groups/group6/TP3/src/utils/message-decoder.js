'use strict';

const debug = require('debug')('utils:message-decoder');

exports.encode = function encode(object) {
  const jsonString = JSON.stringify(object);

  debug(`ENCODE jsonString ${jsonString}`);

  return Buffer.from(jsonString.concat('|'));
};

exports.decode = function decode(buffer) {
  const bufferString = buffer.toString().replace('|', '');

  debug(`DECODE buffer String ${bufferString}`);

  return JSON.parse(bufferString);
};

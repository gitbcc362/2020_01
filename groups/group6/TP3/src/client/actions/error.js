'use strict';

const debug = require('debug')('client:socket:onError');

exports.onError = function onError(error) {
  debug('System error: ', error);
};

'use strict';

const debug = require('debug')('server:socket:onError');

exports.onError = function onError(error) {
  debug('System error: ', error);
};

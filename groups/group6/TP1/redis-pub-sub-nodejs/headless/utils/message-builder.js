'use strict';

exports.messageBuilder = function messageBuilder(message, index) {
  return {
    linha: message,
    queueIndex: index
  };
};

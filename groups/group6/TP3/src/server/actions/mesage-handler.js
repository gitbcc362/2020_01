'use strict';

const debug = require('debug')('server:socket:client-message-handler');

const { encode, RESPONSES, messageBuilder, handler } = require('../../utils');

exports.messageHandler = function messageHandler(source, messageType, content, socket, server) {
  debug('handling message');
  const result = handler[messageType]({ content, socket, source, server });

  const builder = messageBuilder[RESPONSES[messageType]];

  if (builder) {
    const response = builder({ ...result });

    const responseBuffer = encode(response);

    debug('sending response');
    sendResponse(socket, responseBuffer);
  }
};

function sendResponse(socket, buffer) {
  socket.write(buffer);
}

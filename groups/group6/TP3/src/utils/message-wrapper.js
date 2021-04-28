'use strict';

exports.messageWrapper = function messageWrapper(messageType, source, content) {
  return {
    source,
    messageType,
    content,
  };
};

'use strict';

const { messageWrapper } = require('./message-wrapper');

const SOURCE = {
  CLIENT: 'client',
  BROKER: 'broker',
};

const MESSAGETYPE = {
  AQUIRE: 'aquire',
  RELEASE: 'release',
  AQUIRERESPONSE: 'aquireResponse',
  ACK: 'ack',
  INFOCLIENT: 'infoclient',
  INFOBROKER: 'infobroker',
  UPDATEBROKER: 'updatebroker',
  UPDATERESOURCE: 'updateresource',
};

const RESPONSES = {
  [MESSAGETYPE.AQUIRE]: MESSAGETYPE.AQUIRERESPONSE,
  [MESSAGETYPE.RELEASE]: MESSAGETYPE.ACK,
  [MESSAGETYPE.INFOCLIENT]: MESSAGETYPE.UPDATEBROKER,
  [MESSAGETYPE.INFOBROKER]: MESSAGETYPE.UPDATEBROKER,
  [MESSAGETYPE.UPDATEBROKER]: MESSAGETYPE.ACK,
  [MESSAGETYPE.AQUIRERESPONSE]: MESSAGETYPE.ACK,
  [MESSAGETYPE.UPDATERESOURCE]: MESSAGETYPE.ACK,
};

const messageBuilder = {
  [MESSAGETYPE.AQUIRE]: (resourcePosition, clientId) => {
    const content = {
      resourcePosition,
      clientId,
    };

    return messageWrapper(MESSAGETYPE.AQUIRE, SOURCE.CLIENT, content);
  },
  [MESSAGETYPE.AQUIRERESPONSE]: (resource, available) => {
    const content = {
      available,
      ...resource,
    };

    return messageWrapper(MESSAGETYPE.AQUIRERESPONSE, SOURCE.BROKER, content);
  },
  [MESSAGETYPE.RELEASE]: (resourcePosition, resource) => {
    const content = {
      resourcePosition,
      resource,
    };

    return messageWrapper(MESSAGETYPE.RELEASE, SOURCE.CLIENT, content);
  },
  [MESSAGETYPE.ACK]: (statusCode) => {
    const content = {
      statusCode,
    };

    return messageWrapper(MESSAGETYPE.ACK, SOURCE.BROKER, content);
  },
  [MESSAGETYPE.INFOCLIENT]: (socketName, source) => {
    const content = {
      socketName,
    };

    return messageWrapper(MESSAGETYPE.INFOCLIENT, source, content);
  },
  [MESSAGETYPE.INFOBROKER]: (socketName, source, port, host) => {
    const content = {
      socketName,
      port,
      host,
    };

    return messageWrapper(MESSAGETYPE.INFOBROKER, source, content);
  },
  [MESSAGETYPE.UPDATEBROKER]: (arrayBroker) => {
    const content = {
      array: arrayBroker,
    };

    return messageWrapper(MESSAGETYPE.UPDATEBROKER, SOURCE.BROKER, content);
  },
  [MESSAGETYPE.UPDATERESOURCE]: (singleResource) => {
    const content = {
      ...singleResource,
    };

    return messageWrapper(MESSAGETYPE.UPDATERESOURCE, SOURCE.BROKER, content);
  },
};

module.exports = {
  messageBuilder,
  MESSAGETYPE,
  SOURCE,
  RESPONSES,
};

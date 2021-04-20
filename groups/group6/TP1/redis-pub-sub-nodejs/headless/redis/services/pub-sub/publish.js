'use strict';

const { redisClientPub } = require('../../connection');

exports.publish = (channel, message) => {
      redisClientPub.publish(channel, JSON.stringify(message));
};

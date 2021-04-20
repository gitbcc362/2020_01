'use strict';

const { redisClientSub, subscriptionMap } = require('../../connection');

exports.subscribe = (channel, action) => {
  subscriptionMap.set(channel, action);
  redisClientSub.subscribe(channel);
};

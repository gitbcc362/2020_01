'use strict';

const redis = require('redis');

const redisConfig = require('../config/redis');
const redisClientSub = redis.createClient(redisConfig);
const redisClientPub = redis.createClient(redisConfig);
const redisClientCache = redis.createClient(redisConfig);

const subscriptionMap = new Map();

redisClientSub.on('message', async (channel, message) => {
  const action = subscriptionMap.get(channel);

  if (!action) {
return;
}

const data = JSON.parse(message);

  await action(channel, data);
});

module.exports = {
  redisClientPub,
  redisClientSub,
  redisClientCache,
  subscriptionMap
};

'use strict';

const { redisClientCache } = require('../../connection');

exports.getFromCache = function getFromCache(key) {
  return new Promise((resolve, reject) => {
    const result = redisClientCache.get(key);

    if (!result) {
      reject(new Error('Nao conseguiu inserir na cache'));
    }

    resolve(JSON.parse(result));
  });
};

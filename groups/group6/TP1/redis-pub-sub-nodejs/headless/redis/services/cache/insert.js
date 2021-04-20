'use strict';

const { redisClientCache } = require('../../connection');

exports.insertOnCache = function insertOnCache(key, value) {
  return new Promise((resolve, reject) => {
    const result = redisClientCache.set(key,JSON.stringify(value));

    if (!result) {
      reject(new Error('Nao conseguiu inserir na cache'));
    }

    resolve(JSON.parse(result));
  });
};

'use strict';

const sleep = require('./sleep');

module.exports = async function exponentialBackoff(fn, args, debug, maxRetry = 5, numberOfTries = 0) {
  try {
    fn({ ...args });
  } catch (error) {
    if (numberOfTries >= maxRetry) {
      debug('Connection failed max number of tries');
      throw error;
    }

    const delay = 2 ** numberOfTries * 100;

    debug(`Connection failed ${numberOfTries + 1}! trying again in ${delay}`);

    await sleep(delay);
    exponentialBackoff(fn, maxRetry, debug, numberOfTries + 1);
  }
};

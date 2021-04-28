'use strict';

module.exports = function sleep(tempo) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(tempo);
    }, tempo);
  });
};

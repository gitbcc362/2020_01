'use strict';

module.exports = async (time) => (
  new Promise((resolve) => {
    setTimeout(() => {
      resolve(time);
    }, time);
  })
);

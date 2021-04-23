'use strict';

const {connect} = require('./src/client');
const { encode, sleep } = require('./src/utils');

const numberOfTries = 0;
const delay = 2000;
const maxRetry = 5;
const reconnecting = false

(async ()=>{

})();

function isReconnecting() {
  // return new Proxy(reconnecting, {
  //   apply: (target) =>
  //     new Promise((resolve, reject) => {
  //       while (target) {
  //         if (numberOfTries > maxRetry) {
  //           reject(new Error('Failed to reconnect too many times'));
  //         }

  //         sleep(delay);
  //       }

  //       resolve(!target);
  //     }),
  // });
}

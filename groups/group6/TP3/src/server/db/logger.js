'use strict';

const log = [];
let partial = {};
let eventCount = 0;

function addLog(string) {
  Object.assign(partial, {
    [eventCount]: string,
  });
  eventCount += 1;
}

function endLog() {
  log.push(partial);
  eventCount = 0;
  partial = {};
}

module.exports = {
  addLog,
  endLog,
  log,
};

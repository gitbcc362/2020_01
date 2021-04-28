'use strict';

const { subscribe } = require('../redis/services/pub-sub');

const { getPreviousStep } = require('./resolves-channel');

const indexSize = 3;

exports.getRoundRobinIndex = function getRoundRobinIndex(index) {
  return index % indexSize + 1;
};

function roundRobinSubscribe(currentStep, action) {
  for (let index = 1; index <= indexSize; index+=1) {
    subscribe(getPreviousStep(`${currentStep}_${index}`), action);
  }
}

exports.roundRobinSize = indexSize;
exports.roundRobinSubscribe = roundRobinSubscribe;

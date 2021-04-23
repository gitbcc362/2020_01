"use strict";

const {
  roundRobinSubscribe,
  REDUCE,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/utils");

const mapaReduce = {};

const somaMapas = (mapa1, mapa2) => {
  return Object.entries(mapa1).reduce((acc, [key, value]) => {
    if (acc[key]) {
      acc[key] += value;
    } else {
      acc[key] = value;
    }

    return acc;
  }, mapa2);
};

const reducer = async (channel, message) => {
  const { linha } = message;

  console.log(somaMapas(linha, mapaReduce));
};

roundRobinSubscribe(REDUCE, reducer);

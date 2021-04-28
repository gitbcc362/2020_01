"use strict";

const {
  publish,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/redis/services/pub-sub");
const {
  messageBuilder,
  getNextStep,
  roundRobinSubscribe,
  TERMCLEAN,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/utils");

const cleanTerms = (linha) => linha.normalize("NFD");

const cleanPunctuation = (linha) => {
  const regex = RegExp(/[^\w\s]/gi);

  return linha.replace(regex, "");
};

const termCleanStep = (channel, message) => {
  const { linha, queueIndex } = message;

  const linhaSemAcentos = cleanTerms(linha);
  const linhaLimpa = cleanPunctuation(linhaSemAcentos);
  const msg = messageBuilder(linhaLimpa, queueIndex);

  console.log(msg.linha);

  publish(getNextStep(channel), msg);
};

roundRobinSubscribe(TERMCLEAN, termCleanStep);

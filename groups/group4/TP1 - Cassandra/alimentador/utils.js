const { toysTitles } = require("./toys-titles");

const getRandomStock = () => Math.floor(Math.random() * 10 + 1);
const getRandomToyTitle = () => {
    const id = Math.floor(Math.random() * toysTitles.length);
    if (!toysTitles[id]) return toysTitles[0];
    return toysTitles[id];
};

module.exports = { getRandomStock, getRandomToyTitle };

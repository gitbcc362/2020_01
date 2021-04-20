const uuid = require("uuid-random");
const chalk = require("chalk");
const log = console.log;

const {
    getRandomStock,
    getRandomToyTitle
} = require("./utils");

const getActions = (option, client) => {
    const actions = {
        "select-stock": selectStock,
        "insert-stock": insertStock,
        "delete-stock": deleteStock
    };
    actions[option](client);
}

const selectStock = (client) => {
    const query = "SELECT * FROM brinks.brinks_stock";
    client.execute(query, []).then(result => {
        log(chalk.blue("SELECT:") + "number of entries:" + result.rows.length);
    });
}

const insertStock = (client) => {
    const title = getRandomToyTitle();
    const stock = getRandomStock();
    const selectQuery = `SELECT * FROM brinks.brinks_stock WHERE title = '${title}' ALLOW FILTERING`;
    client.execute(selectQuery, []).then(result => {
        const query = (result.rows.length > 0) ? `UPDATE brinks.brinks_stock SET stock = ${stock + Number(result.rows[0].stock)} WHERE id = ${result.rows[0].id}` :
            `INSERT INTO brinks.brinks_stock (id, title, stock) VALUES (${uuid()}, '${title}', ${stock})`;

        const log = chalk.green("INSERT:") + query;

        execute(query, log, client);
    });
}

const deleteStock = (client) => {
    const title = getRandomToyTitle();
    const stock = getRandomStock();
    const selectQuery = `SELECT * FROM brinks.brinks_stock WHERE title = '${title}' ALLOW FILTERING`;
    client.execute(selectQuery, []).then(result => {
        if (result.rows.length > 0) {
            const query = (Number(result.rows[0].stock) > stock) ? `UPDATE brinks.brinks_stock SET stock = ${ Number(result.rows[0].stock) - stock } WHERE id = ${result.rows[0].id}` : `DELETE FROM brinks.brinks_stock WHERE id = ${result.rows[0].id}`;
            const log = chalk.magenta("DELETE: ") + query;
            execute(query, log, client);
        } else {
            log(chalk.red("ERRO:") + "Produto não encontrado em estoque.");
        }
    });
}

const execute = (query, log, client) => {
    client.execute(query, []).then(_ => console.log(log));
}

module.exports.getActions = getActions;
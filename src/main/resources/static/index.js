let eventSource;

const priceTable = document.getElementById("price-table").getElementsByTagName('tbody')[0]
const exchanges = ["KUCOIN", "BYBIT", "MEXC", "BINANCE", "GATE", "HUOBI"]

window.onload =
    () => {
        subscribe()
    }

function createRow(ticker) {
    let row = document.createElement("tr")
    let tickerCell = document.createElement("td")
    tickerCell.classList.add("col-ticker")
    row.appendChild(tickerCell)
    for (const ex of exchanges) {
        let cell = document.createElement("td")
        cell.classList.add("col-" + ex)
        row.appendChild(cell)
    }
    row.cells[0].textContent = ticker

    row.setAttribute("id", "row-" + ticker)
    return row
}

function setRowData(row, ticker, priceToExchange) {
    Object.entries(priceToExchange).forEach((entry) => {
        const [exchange, price] = entry
        row.getElementsByClassName("col-" + exchange).item(0).textContent = price.noExponents()
    })
}

function subscribe() {
    eventSource = new EventSource("http://localhost:8080/api/prices")
    eventSource.addEventListener("prices", event => {
        const data = JSON.parse(event.data)
        let ticker = data.ticker
        let priceToExchange = data.priceToExchange
        let tickerRow = document.getElementById("row-" + ticker)
        if (tickerRow == null && !_.isEmpty(priceToExchange)) {
            tickerRow = createRow(ticker, priceToExchange)
            priceTable.appendChild(tickerRow)
        }
        setRowData(tickerRow, ticker, priceToExchange)
    })
}


function createSpreadTable(spreadObj, tickerRowId) {
    let table = document.createElement("table")
    table.setAttribute("id", "spread-details-" + tickerRowId)
    table.classList.add("spread-details")
    let row1 = document.createElement("tr")
    let row2 = document.createElement("tr")
    let spreadCell = document.createElement("td")
    spreadCell.setAttribute("rowspan", "2")
    let buyCell = document.createElement("td")
    let sellCell = document.createElement("td")
    table.appendChild(row1)
    table.appendChild(row2)
    row1.appendChild(spreadCell)
    row1.appendChild(buyCell)
    row2.appendChild(sellCell)
    return table
}

Number.prototype.noExponents = function () {
    var data = String(this).split(/[eE]/);
    if (data.length === 1) return data[0];

    var z = '', sign = this < 0 ? '-' : '',
        str = data[0].replace('.', ''),
        mag = Number(data[1]) + 1;

    if (mag < 0) {
        z = sign + '0.';
        while (mag++) z += '0';
        return z + str.replace(/^/, '');
    }
    mag -= str.length;
    while (mag--) z += '0';
    return str + z;
}






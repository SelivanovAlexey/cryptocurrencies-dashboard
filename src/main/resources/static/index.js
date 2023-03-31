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

    let spreadTable = createSpreadTable(ticker)
    row.appendChild(spreadTable)
    row.cells[0].textContent = ticker
    row.setAttribute("id", "row-" + ticker)
    return row
}

function setRowData(row, ticker, priceToExchange, spreads) {
    Object.entries(priceToExchange).forEach((entry) => {
        const [exchange, price] = entry
        row.getElementsByClassName("col-" + exchange).item(0).textContent = price.noExponents()
    })
    spreads.forEach((elem, index) => {
        let cell = row.getElementsByClassName("spread-row").item(0).cells[index]
        cell.getElementsByClassName("p-diff").item(0).textContent = elem.diff
        cell.getElementsByClassName("p-base").item(0).textContent = elem.base
        cell.getElementsByClassName("p-target").item(0).textContent = elem.target
        if (elem.diff >= 1 && elem.diff < 5)  cell.style.backgroundColor = "#F6D892"
        if (elem.diff >= 5 && elem.diff < 15)  cell.style.backgroundColor = "#8DB590"
    })
}

function subscribe() {
    eventSource = new EventSource("http://localhost:8080/api/prices")
    eventSource.addEventListener("prices", event => {
        const data = JSON.parse(event.data)
        let ticker = data.ticker
        let priceToExchange = data.priceToExchange
        let spreads = data.spreads
        let tickerRow = document.getElementById("row-" + ticker)
        if (tickerRow == null && !_.isEmpty(priceToExchange)) {
            tickerRow = createRow(ticker, priceToExchange)
            priceTable.appendChild(tickerRow)
        }
        setRowData(tickerRow, ticker, priceToExchange, spreads)
    })
}


function createSpreadTable(tickerRowId) {
    let td = document.createElement("td")
    let table = document.createElement("table")
    table.setAttribute("id", "spread-details-" + tickerRowId)
    table.classList.add("spread-details")
    let row1 = document.createElement("tr")
    row1.classList.add("spread-row")
    let spreadCell1 = document.createElement("td").appendChild(createSpreadElement())
    let spreadCell2 = document.createElement("td").appendChild(createSpreadElement())
    let spreadCell3 = document.createElement("td").appendChild(createSpreadElement())
    table.appendChild(row1)
    row1.appendChild(spreadCell1)
    row1.appendChild(spreadCell2)
    row1.appendChild(spreadCell3)
    td.appendChild(table)
    td.classList.add("td-spread-details")
    return td
}

function createSpreadElement(){
    let td = document.createElement("td")
    let diff = document.createElement("p")
    let buyEx = document.createElement("p")
    let sellEx = document.createElement("p")
    td.appendChild(diff)
    td.appendChild(buyEx)
    td.appendChild(sellEx)
    td.classList.add("col-spread")
    diff.classList.add("p-diff")
    buyEx.classList.add("p-base")
    sellEx.classList.add("p-target")
    return td
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






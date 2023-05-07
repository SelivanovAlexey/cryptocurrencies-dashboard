let eventSource;
//TODO: add sticky header
const priceTable = document.getElementById("price-table").getElementsByTagName('tbody')[0]
const infoTable = document.getElementById("info-table").getElementsByTagName('tbody')[0]
const exchanges = ["KUCOIN", "BYBIT", "MEXC", "BINANCE", "GATE", "HUOBI"]

let selectedSpreadTarget

document.querySelectorAll(".col-spread").forEach((node) => {
    node.onmouseover = getTickerInfoMouseOverEvent
    node.onMouseout = getTickerInfoMouseOutEvent
})

window.onload =
    () => {
        subscribe()
    }

function createRow(ticker) {
    let row = document.createElement("tr")
    let tickerCell = document.createElement("td")
    tickerCell.classList.add("col-ticker")
    row.classList.add("ticker-row")
    row.appendChild(tickerCell)

    let spreadTable = createSpreadTable(ticker)
    row.appendChild(spreadTable)
    row.cells[0].textContent = ticker
    row.setAttribute("id", "row-" + ticker)
    return row
}

function setCellColor(cell, elem) {
    if (elem.diff >= 1 && elem.diff < 5)
        cell.classList.add("spread-cell-ok")
    else if (elem.diff >= 5)
        cell.classList.add("spread-cell-perfect")
    else cell.classList.add("spread-cell-default")
}

function setRowData(row, ticker, spreads) {
    spreads.forEach((elem, index) => {
        let cell = row.getElementsByClassName("spread-row").item(0).cells[index]
        //TODO: real diff view, not 1000+
        cell.getElementsByClassName("p-diff").item(0).textContent = (elem.diff > 1000 ? "1000+" : elem.diff)
        cell.getElementsByClassName("p-base").item(0).textContent = elem.base
        cell.getElementsByClassName("p-target").item(0).textContent = elem.target
        cell.onmouseenter = getTickerInfoMouseOverEvent
        cell.onmouseout = getTickerInfoMouseOutEvent
        //TODO: check why sometimes color does not set
        setCellColor(cell, elem)
    })
}

function subscribe() {
    //TODO: remove hardcoded host+port
    eventSource = new EventSource("http://localhost:8080/api/prices")
    eventSource.addEventListener("prices", event => {
        data = JSON.parse(event.data)
        let ticker = data.ticker
        let spreads = data.spreads
        let tickerRow = document.getElementById("row-" + ticker)
        if (tickerRow == null) {
            tickerRow = createRow(ticker)
            priceTable.appendChild(tickerRow)
        }
        setRowData(tickerRow, ticker, spreads)

        let priceToExchange = data.priceToExchange
        if (priceToExchange != null) {
            updateSpreadInfoTable(data, selectedSpreadTarget)
        }
    })
}

//TODO: low prio. Do creation on filling
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

function createSpreadElement() {
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

function getTickerInfoMouseOverEvent(event) {
    const ticker = event.target.closest(".ticker-row").firstChild.textContent
    document.getElementById("info-ticker").textContent = ticker
    selectedSpreadTarget = event.target.closest(".col-spread")
    const baseEx = event.target.getElementsByClassName("p-base")[0].textContent
    const targetEx = event.target.getElementsByClassName("p-target")[0].textContent

    fillSpreadInfoTable(ticker, baseEx, targetEx)
    requestPrices(ticker)
}

function getTickerInfoMouseOutEvent(event) {
}

function fillSpreadInfoTable(ticker, baseEx, targetEx) {
    axios.get("http://localhost:8080/api/getTickerInfo", {params: {ticker: ticker}})
        .then((response) => {
            infoTable.rows[0].cells[1].textContent = baseEx
            infoTable.rows[1].cells[1].textContent = targetEx
            infoTable.rows[0].cells[2].textContent = response.data.priceToExchange[baseEx]
            infoTable.rows[1].cells[2].textContent = response.data.priceToExchange[targetEx]
        })

        axios.get("http://localhost:8080/api/withdrawAvailability", {params: {exchange: baseEx, ticker: ticker}})
        .then((wResponse) => {
            console.log('data1', wResponse)
        })
        axios.get("http://localhost:8080/api/depositAvailability", {params: {exchange: baseEx, ticker: ticker}})
        .then((dResponse) => {
            console.log('data2', dResponse)
        })


    const spread = selectedSpreadTarget.getElementsByClassName("p-diff")[0].textContent
    document.getElementById("info-spread").textContent = spread
}

function updateSpreadInfoTable(data, selectedSpreadTarget) {
    const baseEx = selectedSpreadTarget.getElementsByClassName("p-base")[0].textContent
    const targetEx = selectedSpreadTarget.getElementsByClassName("p-target")[0].textContent
    infoTable.rows[0].cells[1].textContent = baseEx
    infoTable.rows[1].cells[1].textContent = targetEx
    //TODO: price with no exponents
    let price;
    if ((price = data.priceToExchange[baseEx]) != null) infoTable.rows[0].cells[2].textContent = price
    if ((price = data.priceToExchange[targetEx]) != null) infoTable.rows[1].cells[2].textContent = price

    const spread = selectedSpreadTarget.getElementsByClassName("p-diff")[0].textContent
    document.getElementById("info-spread").textContent = spread
}

function requestPrices(ticker) {
    axios.get("http://localhost:8080/api/enablePrices", {params: {ticker: ticker}})
}







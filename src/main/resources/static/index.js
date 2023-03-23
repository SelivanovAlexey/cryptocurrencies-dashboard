let eventSource;

const priceTable = document.getElementById("price-table").getElementsByTagName('tbody')[0]
const exchangesMap = {
    "kucoin": 1,
    "bybit": 2,
    "mexc": 3,
    "binance": 4,
    "gate": 5,
    "huobi": 6,
}

function getKeyByValue(object, value) {
    return Object.keys(object).find(key => object[key] === value);
}

window.onload =
    () => {
        // subscribe("kucoin")
        // subscribe("bybit")
    }

function createRow(...cellsValues) {
    let row = document.createElement("tr")
    for (let cellValue of cellsValues) {
        let cell = document.createElement("td")
        cell.textContent = cellValue
        row.appendChild(cell)
    }
    row.setAttribute("id", cellsValues[0])
    return row
}

function subscribe(exchange) {
    eventSource = new EventSource("http://localhost:8080/api/" + exchange + "/prices")
    eventSource.addEventListener("price." + exchange, event => {
        const data = JSON.parse(event.data)
        Object.entries(data).forEach((entry) => {
            const [key, value] = entry
            let tickerRow = document.getElementById(key)
            if (tickerRow == null) {
                tickerRow = createRow(key, "","","","","","")
                priceTable.appendChild(tickerRow)
            }
            tickerRow.cells[exchangesMap[exchange]].textContent = value
        })

        // Array.from(priceTable.rows).forEach(row => {
        //     let rowId = row.getAttribute("id")
        //     if (data[rowId] == null)
        //         priceTable.removeChild(document.getElementById(rowId))
        // })

        // calculateMaxSpread(exchange)
    })
}


// function calculateMaxSpread(exchange) {
//     Array.from(priceTable.rows).forEach(tickerRow => {
//         let maxS = new Spread()
//         maxS.spread = 0
//         for (let i = 1; i < tickerRow.cells.length; i++) {
//             for (let j = i + 1; j < tickerRow.cells.length; j++) {
//                 if (tickerRow.cells[i].textContent !== "" && tickerRow.cells[j].textContent !== "") {
//                     maxS.spread = 0
//                     let first = parseFloat(tickerRow.cells[i].textContent)
//                     let second = parseFloat(tickerRow.cells[j].textContent)
//                     let sellPrice = Math.max(first, second)
//                     let buyPrice = Math.min(first, second)
//                     let newMaxS = (sellPrice - buyPrice) / buyPrice * 100
//                     if (newMaxS > maxS.spread) {
//                         maxS.sellPrice = sellPrice
//                         maxS.buyPrice = buyPrice
//                         maxS.sellExchange = sellPrice === first ? i : j
//                         maxS.buyExchange = sellPrice === first ? j : i
//                         maxS.spread = newMaxS.toFixed(3)
//                     }
//                 }
//             }
//         }
//
//         let spreadTickerRow = document.getElementById(tickerRow.id)
//         if (spreadTickerRow.childNodes[7] != null) {
//             if (maxS.spread != "0") {
//                 let detailsTable = document.getElementById("spread-details-" + tickerRow.id)
//                 fillSpread(detailsTable, maxS)
//             }
//         } else {
//             let spreadCell = createSpreadTable(maxS, tickerRow.id)
//             spreadTickerRow.appendChild(spreadCell)
//         }
//
//     })
// }
//
// function fillSpread(detailsTable, maxS) {
//     detailsTable.rows[0].cells[0].textContent = maxS.spread
//     detailsTable.rows[0].cells[1].textContent = "Buy: " + getKeyByValue(exchangesMap, maxS.buyExchange)
//     detailsTable.rows[1].cells[0].textContent = "Sell: " + getKeyByValue(exchangesMap, maxS.sellExchange)
// }
//
// function createSpreadTable(spreadObj, tickerRowId) {
//     let table = document.createElement("table")
//     table.setAttribute("id", "spread-details-" + tickerRowId)
//     table.classList.add("spread-details")
//     let row1 = document.createElement("tr")
//     let row2 = document.createElement("tr")
//     let spreadCell = document.createElement("td")
//     spreadCell.setAttribute("rowspan", "2")
//     let buyCell = document.createElement("td")
//     let sellCell = document.createElement("td")
//     table.appendChild(row1)
//     table.appendChild(row2)
//     row1.appendChild(spreadCell)
//     row1.appendChild(buyCell)
//     row2.appendChild(sellCell)
//     return table
// }
//
//
// class Spread {
//     constructor(sellPrice, buyPrice, sellExchange, buyExchange, spread) {
//         this.sellPrice = sellPrice
//         this.buyPrice = buyPrice
//         this.sellExchange = sellExchange
//         this.buyExchange = buyExchange
//         this.spread = spread
//     }
// }





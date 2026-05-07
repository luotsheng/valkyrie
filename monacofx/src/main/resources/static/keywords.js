window.SqlKeywords = new Set();

window.addSqlKeywords = function (kws) {
    kws.forEach(v => window.SqlKeywords.add(v))
}
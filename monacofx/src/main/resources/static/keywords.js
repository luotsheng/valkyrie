window.SqlKeywords = [];

window.addSqlKeywords = function (kws) {
    kws.forEach(v => {
        const parts = v.split(':');
        const keyword = parts[0];
        const kind = parts[1] || 'Keyword';

        // 去重检查
        if (SqlKeywords.some(item => item.label === keyword)) {
            return;
        }

        SqlKeywords.push({
            label: keyword,
            kind: kind,
            detail: getDetail(kind),
            documentation: ''
        });
    });
}

function getDetail(kind) {
    const details = {
        'Keyword': '',
        'Function': '函数',
        'Operator': '操作符',
        'Class': '表',
        'Field': '字段',
        'Module': '模块',
        'Folder': '文件夹'
    };
    return details[kind] || '';
}
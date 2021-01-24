$(function () {

    optionlinestep3 = {
        title: {
            text: '丢包率',
            textStyle:{
                color:'black'
            }
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: [ 'Step End']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            data: ['15：00','15：15','15：30','15：45','16：00','16：15','16：30','16：45','17：00','17：15','17：30','17：45','18：00'],
            axisLabel:{
                color: 'rgb(6,222,255)'
            }
        },
        yAxis: {
            type: 'value',
            name: '丢包率',
            nameTextStyle:{
                color:'rgb(6,222,255)'
            },
            axisLabel:{
                color: 'rgb(6,222,255)'
            }
        },
        series: [
            {
                name: '丢包率',
                type: 'line',
                step: 'end',
                data: [0.017, 0.0090, 0.0230, 0.0240, 0.0320, 0.0132, 0.0201, 0.0134, 0.0190, 0.0238, 0.0132, 0.0101, 0.0134]
            }
        ]
    };
    var myChartLinestep3 = echarts.init(document.getElementById('right_3'));
    myChartLinestep3.setOption(optionlinestep3);
0})
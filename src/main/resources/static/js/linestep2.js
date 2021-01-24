
$(function () {

    optionlinestep2 = {
        title: {
            text: '网络延时',
            textStyle:{
                color:'black'
            }
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: [ 'Step Middle']
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
            nameTextStyle:{
                color:'rgb(6,222,255)'
            },
            axisLabel:{
                color: 'rgb(6,222,255)'
            }
        },
        series: [

            {
                name: '网络延时',
                type: 'line',
                itemStyle:{
                    normal:{
                        color: 'yellow',
                    }
                },
                step: 'middle',
                data: [170, 90, 230, 240, 320, 132, 201, 134, 190, 238, 132, 101, 134]
            }

        ]
    };
    var myChartLinestep2 = echarts.init(document.getElementById('right_2'));
    myChartLinestep2.setOption(optionlinestep2);
})
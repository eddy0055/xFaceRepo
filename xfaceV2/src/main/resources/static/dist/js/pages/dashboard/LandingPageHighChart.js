$(document).ready(function(){
	var panelBarChart = "chartPassengerRegister";
	createBarChart();
	function createBarChart(){
//		Highcharts.setOptions ({
//	        colors:[
//	            '#5a9bd4',
//	            '#faa75b',
//	            '#7ac36a',
//	            '#9e67ab',
//	            '#f15a60',
//	            '#ce7058',
//	            '#d77fb4'
//	        ]
//	    });

	    var chart = new Highcharts.Chart({
		    chart: {
		        renderTo:panelBarChart,
		        type:'column'		        
		    },
		    title:{
		        text:'Chart Title'
		    },		   
		    credits:{enabled:false},
		    legend:{
		    },
		    tooltip:{
		        shared:true        
		    },
		    plotOptions: {
		        series: {
		            shadow:false,
		            borderWidth:0,
		            pointPadding:0,
		            point: {
	                    events: {
	                        click: function () {
	                        	handleBarChartClick(this);
	                        }
	                    }
	                }
		        }
		    },
		    xAxis:{
		        categories:['A','B','C','D','E'],
		        lineColor:'#999',
		        lineWidth:1,
		        tickColor:'#666',
		        tickLength:3,
		        title:{
		            text:'X Axis Title',
		            style:{
		                color:'#333'
		            }
		        }
		    },
		    yAxis:{
		        lineColor:'#999',
		        lineWidth:1,
		        tickColor:'#666',
		        tickWidth:1,
		        tickLength:3,
		        gridLineColor:'#ddd',
		        title:{
		            text:'Y Axis Title',
		            rotation:90,
		            margin:50,
		            style:{
		                color:'#333'
		            }
		        }
		    },    
		    series: [{
		        name:'Group 1',
		        data: [7,12,16,32,64],
		        cursor: 'pointer',	            
		    },{
		        name:'Group 2',
		        data: [16,32,64,7,12],
		        cursor: 'pointer'	            
		    },{
		        name:'Group 3',
		        data: [32,64,7,12,16],
		        cursor: 'pointer'	            
		    }]		    
	    });
	}
	//////////// create chart
	function handleBarChartClick(objectClick){
		alert('Category1: ' + objectClick.category +', Group:'+objectClick.series.name+ ', value: ' + objectClick.y);
	}
});	

$(document).ready(function(){
	console.log("start web socket connection");
	var ws;
	var count = 0;
	
	// get the slider instance
	var sliderInstance = $('.bxslider').bxSlider({
		mode: 'vertical', 
	    slideMargin:5,  
	    pager: false, 
	    controls: false,
	    minSlides: 4, 
	    maxSlides:4, 
	    moveSlides:1,
	    infiniteLoop:false,
	    responsive: false,
	    adaptiveHeight: false
	})
	console.log(sliderInstance);
	$('span.thumb').on('hover', function() {
		sliderInstance.goToSlide($(this).attr('data-index'));
	});	

	connect();
	
	function setConnected(connected) {
		console.log('Connected: ' + connected);
		
		getCameraInfo();
	}

	function connect() {
		console.log("in connection");
		ws = new WebSocket('ws://localhost:8090/VCMAlarm');
		ws.onmessage = function(data) {
			onReceived(data.data);
		}
		setConnected(true);
		console.log("out connection");
	}

	function disconnect() {
		if (ws != null) {
			ws.close();
		}
		setConnected(false);
		console.log("Websocket is in disconnected state");
	}

	function sendData() {
		var data = JSON.stringify({
			'user' : $("#user").val()
		})
		ws.send(data);
	}

	function onReceived(data) {
		console.log("on receive data");
		console.log(data);
		
		obj = JSON.parse(data);
	    $('.bxslider').last().prepend(
			'<li><a onclick="onClicked(\'' + obj.alarmCode + '\')">' +
				'<div class="row" style="background-color: ' + obj.personCategory.categoryColorCode + '">' +
		    		'<div class="col-xs-2-5">' +
		    			'<img style="border: 1px solid #ddd;" width="96px" height="96px" src="' + obj.personPhoto + '" />' +
		    		'</div>' +
		    		'<div class="col-md-8">' +
		    			'<label class="control-label"><font-size="18px">  Name: ' + obj.fullName + ' </font></label><br />' +
		    			'<label class="control-label"><font-size="18px">  Group: ' + obj.personCategory.categoryName + '</font></label><br />' +
		    			'<label class="control-label"><font-size="18px">  Time: ' + obj.alarmDate + '</font></label>' +
		    		'</div>' +
		    	'</div></a>' +	    		
		    '</li>'
		);
	    		    
	    // get the current slide
	    var currentSlide = sliderInstance.getCurrentSlide();
	    var count = sliderInstance.getSlideCount(); 
	    
	    // reload the instance
	    sliderInstance.reloadSlider({
			mode: 'vertical', 
		    slideMargin: 5, 
		    pager: false, 
		    controls: count >= 4,
		    minSlides: 4, 
		    maxSlides:4, 
		    moveSlides:1,
		    infiniteLoop:false,
		    adaptiveHeight: false,	   
		    responsive: true,
	        startSlide: currentSlide
	    });	
	    
		$("#db_photo").attr("src", obj.personPhoto);
		$("#live_photo").attr("src", obj.livePhoto);	    
		$("#similarity").text(obj.percentMatch + "%");
	}

	$(function() {	
		$("form").on('submit', function(e) {
			e.preventDefault();
		});
		$("#disconnect").click(function() {
			disconnect();
		});
		$("#send").click(function() {
			sendData();
		});
	});
	
	/**
	 * Decode base64 image
	 *.e.g. data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAAAPAQMAAABeJUoFAAAABlBMVEX///8AAABVwtN+AAAAW0lEQVQImY2OMQrAMAwDjemgJ3jI0CFDntDBGKN3hby9bWi2UqrtkJAk8k/m5g4vGBCprKRxtzQR3mrMlm2CKpjIK0ZnKYiOjuUooS9ALpjV2AjiGY3Dw+Pj2gmnNxItbJdtpAAAAABJRU5ErkJggg==
	 */
	function decodeBase64Image(dataString) {
	  var matches = dataString.match(/^data:([A-Za-z-+\/]+);base64,(.+)$/),
	    response = {};

	  if (matches.length !== 3) {
	    return new Error('Invalid input string');
	  }

	  response.type = matches[1];
	  response.data = new Buffer(matches[2], 'base64');

	  return response;
	}	
	
    function getCameraInfo() {
        $.ajax({
            type: "GET",
            url: "/rest/getCameraInfo",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(result) {
            	console.log(result);
            	init(result[0], result[2], result[3], result[1], result[4]);
            },
            error: function (result) {
            	
            }
        });      	
    }	
	
	function init(ip, user, pwd, port, camId) {
		try {					
			var result;
			
			result = setInit();
			
			if (result == 0)
			{
				//Show live video
				result = setLogin(user, pwd, ip, port);
						
				if (result == 0)
				{
					ocx.IVS_OCX_SetWndLayout(11);
					ocx.IVS_OCX_ShowTitlebar(0);
					
					//Play video
					getLivePlay(camId);
				}
				else
				{
					alert("Error : " + result);
				}
			}
			else
			{
				alert("Error: " + result);
			}					
		} catch (e) {
			console.log(e);
		}	    	   
	}
	
	$(window).bind('beforeunload', function(){
		stopLivePlay();
		closeSession();
	});
	
});


/**
 * http://usejsdoc.org/
 */
var audio_context;
var recorder;
var intervalKey;
var url = 'ws://' + document.domain + ':9001' 
var ws = new WebSocket(url);
ws.onopen = function () {
	__log("Openened connection to websocket");
};

//Receive message from server
ws.onmessage = function(e) {
    var jsonResponse = jQuery.parseJSON(e.data );
    __log(jsonResponse);
    if (jsonResponse.hypotheses.length > 0) {
       var bestMatch = jsonResponse.hypotheses[0].utterance;
        //$("#outputText").text(bestMatch);
    }
}

function __log(e, data) {
    console.log( "\n" + e + " " + (data || '') );
  }

function startUserMedia(stream) {
    var input = audio_context.createMediaStreamSource(stream);
    console.log('Media stream created.');

    // Uncomment if you want the audio to feedback directly
    //input.connect(audio_context.destination);
    //__log('Input connected to audio context destination.');
    
    recorder = new Recorder(input);
    __log('Recorder initialised.');
  }
window.onload = function init() {
    try {
      // webkit shim
      window.AudioContext = window.AudioContext || window.webkitAudioContext;
      navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia;
      window.URL = window.URL || window.webkitURL;
      
      audio_context = new AudioContext;
      __log('Audio context set up.');
      __log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
    } catch (e) {
      alert('No web audio support in this browser!');
    }
    
    navigator.getUserMedia({audio: true}, startUserMedia, function(e) {
      __log('No live audio input: ' + e);
    });
  };
  
var reco = new Vue({
	el : '#record',
	data : {
		returnResult : ''
	},
	methods : {
		audiorecord : function() {
			recorder && recorder.record();
			
            ws.send("start");
            intervalKey = setInterval(function() {
            	recorder.exportWAV(function(blob) {
                	recorder.clear();
                    ws.send(blob);
                });
            }, 1000);
		},
		exportFile:function(){
			recorder && recorder.stop();
			clearInterval(intervalKey);
			ws.send("stop");
			recorder.clear();
		}
	}
});
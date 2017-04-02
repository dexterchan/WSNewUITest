/**
 * http://usejsdoc.org/
 */

var session = {
	audio : true,
	video : false
};
var recordRTC = null;

var ws = new WebSocket('ws://localhost:5000/websocket');

ws.onopen = function(evt) {
  console.log('Connected to websocket.');

  // First message: send the sample rate
  ws.send("sample rate:" + sampleRate);

  navigator.getUserMedia({audio: true, video: false}, initializeRecorder, function(e) {
   console.log('No live audio input: ' + e);
  });
}

function initializeRecorder(stream) {
	var audioContext = window.AudioContext;
	var context = new audioContext();
	var audioInput = context.createMediaStreamSource(stream);
	var bufferSize = 2048;
	// create a javascript node
	var recorder = context.createJavaScriptNode(bufferSize, 1, 1);
	// specify the processing function
	recorder.onaudioprocess = recorderProcess;
	// connect stream to our recorder
	audioInput.connect(recorder);
	// connect our recorder to the previous destination
	recorder.connect(context.destination);
}


function recorderProcess(e) {
	  var left = e.inputBuffer.getChannelData(0);
	}

function recorderProcess(e) {
	  if (recording){
	    var left = e.inputBuffer.getChannelData(0);
	    ws.send(convertFloat32ToInt16(left));
	  }
}

function convertFloat32ToInt16(buffer) {
    var l = buffer.length;
    var buf = new Int16Array(l)

    while (l--) {
      buf[l] = buffer[l]*0xFFFF;    //convert to 16 bit
    }
    return buf.buffer
 }
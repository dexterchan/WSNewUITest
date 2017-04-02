/**
 * http://usejsdoc.org/
 */

var session = {
	audio : true,
	video : false
};
var recordRTC = null;
navigator.getUserMedia(session, initializeRecorder, onError);

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
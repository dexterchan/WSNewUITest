package com.google.speech;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

//Imports the Google Cloud client library
import com.google.cloud.speech.spi.v1beta1.SpeechClient;
import com.google.cloud.speech.v1beta1.RecognitionAudio;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1beta1.SyncRecognizeResponse;
import com.google.protobuf.ByteString;

public class GoogleSpeechReco {

	static public class RecoResult{
		public RecoResult(){}
		public String result;
		public float confidence;
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Result:"+result+" with confidence:"+confidence+"\n";
		}
		
	}
	public static List<RecoResult> syncRecognizeFile(String fileName) throws Exception, IOException {
		SpeechClient speech = SpeechClient.create();
		List<RecoResult> resultLst = new LinkedList<RecoResult>();

		try {
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			// Configure request with local raw PCM audio
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRate(44100).build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
			System.out.println("send file");
			// Use blocking call to get audio transcript
			SyncRecognizeResponse response = speech.syncRecognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			for (SpeechRecognitionResult result : results) {
				List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
				for (SpeechRecognitionAlternative alternative : alternatives) {
					//System.out.printf("Transcription: %s%n", alternative.getTranscript());
					RecoResult r = new GoogleSpeechReco.RecoResult();
					r.confidence=alternative.getConfidence();
					r.result = alternative.getTranscript();
					
					resultLst.add(r);
				}
			}
		} finally {
			speech.close();
		}

		return resultLst;
	}
}

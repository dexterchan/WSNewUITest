package NewUITest.WSNewUITest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.cloud.language.spi.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.AnnotateTextRequest;
import com.google.cloud.language.v1.AnnotateTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.Token;
//Imports the Google Cloud client library
import com.google.cloud.speech.spi.v1beta1.SpeechClient;
import com.google.cloud.speech.v1beta1.RecognitionAudio;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1beta1.SyncRecognizeResponse;
import com.google.protobuf.ByteString;

public class GoogleSpeechQuickStart {
	public static void main(String... args) throws Exception {
		// Instantiates a client
		SpeechClient speech = SpeechClient.create();

		// The path to the audio file to transcribe
		String fileName = "/WSNewUITest/out.wav";

		syncRecognizeFile(fileName);
	}

	public static List<Entity> analyzeEntitiesText(String text) throws IOException {
		
		LanguageServiceClient languageApi=LanguageServiceClient.create();
	    // Note: This does not work on App Engine standard.
	    Document doc = Document.newBuilder()
	        .setContent(text).setType(Type.PLAIN_TEXT).build();
	    AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
	        .setDocument(doc)
	        .setEncodingType(EncodingType.UTF16).build();
	    AnalyzeEntitiesResponse response = languageApi.analyzeEntities(request);
	    return response.getEntitiesList();
	  }
	
	public static List<Token> analyzeSyntaxText(String text) throws IOException {
		LanguageServiceClient languageApi=LanguageServiceClient.create();
	    // Note: This does not work on App Engine standard.
	    Document doc = Document.newBuilder()
	        .setContent(text).setType(Type.PLAIN_TEXT).build();
	    AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
	        .setDocument(doc)
	        .setEncodingType(EncodingType.UTF16).build();
	    AnalyzeSyntaxResponse response = languageApi.analyzeSyntax(request);
	    return response.getTokensList();
	  }

	public static void syncNLUString(String text) throws Exception, IOException {
		LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
		// The text to analyze
		Document document = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
		AnnotateTextResponse response=null;
		// Detects the sentiment of the text
		//try (LanguageServiceClient languageServiceClient = LanguageServiceClient.create()) 
		{

			AnnotateTextRequest.Features features = AnnotateTextRequest.Features.newBuilder().build();
			EncodingType encodingType = EncodingType.NONE;
			 response = languageServiceClient.annotateText(document, features, encodingType);
			
		}
		response.getEntitiesList().forEach( e -> {
			System.out.println(e.getName());
			System.out.println(e.getType());
			System.out.println(e.getMetadata());
			System.out.println(e.getSalience());
		});
	}

	public static void syncRecognizeFile(String fileName) throws Exception, IOException {
		SpeechClient speech = SpeechClient.create();

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
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
			}
		}
		speech.close();
	}
}

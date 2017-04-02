package NewUITest.WSNewUITest;

import java.util.List;

import com.google.speech.GoogleSpeechReco;

import junit.framework.TestCase;

public class GoogleSpeechTest extends TestCase {

	public void testSyncRecognizeFile() throws Exception{
		List<String> r=GoogleSpeechReco.syncRecognizeFile("/Users/dexter/Documents/workspace/WSNewUITest/out.wav");
		
		r.forEach(item -> System.out.println(item));
		
		assertTrue( true );
	}

}

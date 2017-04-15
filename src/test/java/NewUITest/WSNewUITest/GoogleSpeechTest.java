package NewUITest.WSNewUITest;

import java.io.PrintStream;
import java.util.List;

import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.Token;
import com.google.speech.GoogleSpeechReco;

import junit.framework.TestCase;

public class GoogleSpeechTest extends TestCase {

	public void testSyncRecognizeFile() throws Exception{
		List<GoogleSpeechReco.RecoResult> r=GoogleSpeechReco.syncRecognizeFile("out.wav");
		
		r.forEach(item -> System.out.println(item));
		
		
		List<Entity>  le = GoogleSpeechQuickStart.analyzeEntitiesText("Pay credit card");
		
		le.forEach( e -> {
			System.out.println(e.getName());
			System.out.println(e.getType());
			System.out.println(e.getMetadata());
			System.out.println(e.getSalience());
		});
		assertTrue( true );
	}
	
	public void testSytax() throws Exception{
		List<Token>  le = GoogleSpeechQuickStart.analyzeSyntaxText("Buy four thousands bonds");
		
		le.forEach( token-> {
			PrintStream out = System.out;
		      out.println("TextSpan");
		      out.printf("\tText: %s\n", token.getText().getContent());
		      out.printf("\tBeginOffset: %d\n", token.getText().getBeginOffset());
		      out.printf("Lemma: %s\n", token.getLemma());
		      out.printf("PartOfSpeechTag: %s\n", token.getPartOfSpeech().getTag());
		      out.printf("\tAspect: %s\n",token.getPartOfSpeech().getAspect());
		      out.printf("\tCase: %s\n", token.getPartOfSpeech().getCase());
		      out.printf("\tForm: %s\n", token.getPartOfSpeech().getForm());
		      out.printf("\tGender: %s\n",token.getPartOfSpeech().getGender());
		      out.printf("\tMood: %s\n", token.getPartOfSpeech().getMood());
		      out.printf("\tNumber: %s\n", token.getPartOfSpeech().getNumber());
		      out.printf("\tPerson: %s\n", token.getPartOfSpeech().getPerson());
		      out.printf("\tProper: %s\n", token.getPartOfSpeech().getProper());
		      out.printf("\tReciprocity: %s\n", token.getPartOfSpeech().getReciprocity());
		      out.printf("\tTense: %s\n", token.getPartOfSpeech().getTense());
		      out.printf("\tVoice: %s\n", token.getPartOfSpeech().getVoice());
		      out.println("DependencyEdge");
		      out.printf("\tHeadTokenIndex: %d\n", token.getDependencyEdge().getHeadTokenIndex());
		      out.printf("\tLabel: %s\n", token.getDependencyEdge().getLabel());
		    }
		);
	}

}

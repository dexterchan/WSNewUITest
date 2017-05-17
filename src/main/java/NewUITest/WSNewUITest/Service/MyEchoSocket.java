package NewUITest.WSNewUITest.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import com.google.speech.GoogleSpeechReco;
public class MyEchoSocket implements org.eclipse.jetty.websocket.api.WebSocketListener {
	private Session outbound;
	
	String currentCommand="";
	
	String voiceFolder = "voiceData";
	String voiceFile = "newout.raw";
	
	public MyEchoSocket(){
		//Create a folder if not found
		checkandCreateFolder();
	}

	private void checkandCreateFolder(){
		try{
			File directory = new File(voiceFolder);
		    if (! directory.exists()){
		        directory.mkdir();
		        // If you require it to make the entire directory path including parents,
		        // use directory.mkdirs(); here instead.
		    }
			
		}catch (Exception e){e.printStackTrace();}
	}
	
	@Override
	public void onWebSocketBinary(byte[] data, int offset, int len) {
		/* only interested in text messages */
		if (outbound == null) {
			return;
		}

		try {

			if (currentCommand.equals("start")) {
				
				try {
					// The temporary file that contains our captured audio stream
					File f = new File(this.voiceFile);
 
					// if the file already exists we append it.
					if (f.exists()) {
						System.out.println("Adding received block to existing file.");
 
						// two clips are used to concat the data
						 AudioInputStream clip1 = AudioSystem.getAudioInputStream(f);
						 AudioInputStream clip2 = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
 
						 // use a sequenceinput to cat them together
						 AudioInputStream appendedFiles = 
		                            new AudioInputStream(
		                                new SequenceInputStream(clip1, clip2),     
		                                clip1.getFormat(), 
		                                clip1.getFrameLength() + clip2.getFrameLength());
 
						 // write out the output to a temporary file
		                    AudioSystem.write(appendedFiles, 
		                            AudioFileFormat.Type.WAVE,
		                            new File(this.voiceFile+"2"));
 
		                    // rename the files and delete the old one
		                    File f1 = new File(this.voiceFile);
		                    File f2 = new File(this.voiceFile+"2");
		                    f1.delete();
		                    f2.renameTo(new File(this.voiceFile));
					} else {
						System.out.println("Starting new recording.");
						FileOutputStream fOut = new FileOutputStream(this.voiceFile,true);
						fOut.write(data);
						fOut.close();
					}			
				} catch (Exception e) {	e.printStackTrace();}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		this.outbound = null;
	}

	@Override
	public void onWebSocketConnect(Session arg0) {
		// TODO Auto-generated method stub
		outbound = arg0;
	}

	@Override
	public void onWebSocketError(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
    public void onWebSocketText(String data)
    {
		if (data.startsWith("start")) {
			// before we start we cleanup anything left over
			cleanup();
			currentCommand = "start";
			UUID uniqueKey = UUID.randomUUID();
			this.voiceFile = this.voiceFolder+File.separator+uniqueKey.toString()+".raw";
		} else if (data.startsWith("stop")) {
			currentCommand = "stop";
		} else if (data.startsWith("clear")) {
			// just remove the current recording
			cleanup();
		} else if (data.startsWith("analyze")){
			//Convert to flac and send to webservice
			RemoteEndpoint remote = this.outbound.getRemote();
			
			try
			{
				List<GoogleSpeechReco.RecoResult> r=GoogleSpeechReco.syncRecognizeFile(voiceFile);
				
				if(r.size()>0){
					GoogleSpeechReco.RecoResult result = r.get(0);
					remote.sendString(result.result);
				}else{
					remote.sendString("Unknown");
				}
			}
			catch (IOException e)
			{
			    e.printStackTrace(System.err);
			}catch(Exception e){
				e.printStackTrace();
			}
        } else if (data.startsWith("@recognize@: ")) {
        	
        	String input = data.substring(13);
        	System.out.println(input);
        	
        	String url = "http://localhost:5000/pib/service/requests";
        	try {
				URL urlobj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) urlobj.openConnection();
				
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				
				String urlParameters = "{\"description\":\"" + input + "\"}";
				
				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
				
				int responseCode = conn.getResponseCode();
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				
				
				RemoteEndpoint remote = this.outbound.getRemote();
				remote.sendString("@action@: " + response);
				
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }
	
	public void cleanup(){
		try{

		File f = new File(voiceFile);
		f.delete();
		}catch(Exception e){}
		
	}
}

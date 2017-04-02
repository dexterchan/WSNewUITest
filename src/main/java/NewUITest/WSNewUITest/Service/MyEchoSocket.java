package NewUITest.WSNewUITest.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class MyEchoSocket implements org.eclipse.jetty.websocket.api.WebSocketListener {
	private Session outbound;
	
	String currentCommand="";

	@Override
	public void onWebSocketBinary(byte[] data, int offset, int len) {
		/* only interested in text messages */
		if (outbound == null) {
			return;
		}

		try {

			if (currentCommand.equals("start")) {
				/*try {
					// The temporary file that contains our captured audio stream
					File f = new File("out.wav");
					FileOutputStream fOut = new FileOutputStream("out.wav",true);
					fOut.write(data);
					fOut.close();
					// if the file already exists we append it.
						
				} catch (Exception e) {	e.printStackTrace();}*/
				
				try {
					// The temporary file that contains our captured audio stream
					File f = new File("out.wav");
 
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
		                            new File("out2.wav"));
 
		                    // rename the files and delete the old one
		                    File f1 = new File("out.wav");
		                    File f2 = new File("out2.wav");
		                    f1.delete();
		                    f2.renameTo(new File("out.wav"));
					} else {
						System.out.println("Starting new recording.");
						FileOutputStream fOut = new FileOutputStream("out.wav",true);
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
			    remote.sendString("Hello World");
			}
			catch (IOException e)
			{
			    e.printStackTrace(System.err);
			}
        }
    }
	
	public void cleanup(){
		try{

		File f = new File("out.wav");
		f.delete();
		}catch(Exception e){}
		
	}
}
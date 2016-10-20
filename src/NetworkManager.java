import java.io.*;
import java.net.Socket;

public class NetworkManager{

	Socket socket;
	InputStream is;
   InputStreamReader isr;
   BufferedReader br;
	OutputStream os;
	PrintStream out;
	boolean initCorrect = false;
	int packetNum = 0;
	final static String ADDRESS = "127.0.0.1";
	final static int PORT = 22222;

	public NetworkManager (){

		try{
			this.socket = new Socket(ADDRESS, PORT);
			this.is = socket.getInputStream();
            this.isr = new InputStreamReader(is, "UTF-8");
            this.br = new BufferedReader(isr);
			this.os = socket.getOutputStream();
			this.out = new PrintStream(os);
			initCorrect = checkInit();
			if( initCorrect ){
				System.out.println( "Connected!" );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

	//send object to server
	public void sendObjects(String s) {
		try {
			if (!initCorrect) {
				System.out.println("Not initialized correctly");
			} else {
				out.println(s);
				out.flush();
				os.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//if nothing, return null
	public String recieveObjects() {
		try {
			if (initCorrect) {
				if (br.ready()) {
					return br.readLine();
				} else {
					return null;
				}
			} else {
				System.out.println("Not initialized correctly");
				return "Not initialized correctly";
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private boolean checkInit() {
		boolean flag = true;
		if (socket == null) {
			System.out.println("Socket is null");
			flag = false;
		}
		if (is == null) {
			System.out.println("InputStream is null");
			flag = false;
		}
		if (isr == null) {
			System.out.println("InputStreamReader is null");
			flag = false;
		}
		if (br == null) {
			System.out.println("BufferedReader is null");
			flag = false;
		}
		if (os == null) {
			System.out.println("OutputStream is null");
			flag = false;
		}
		if (out == null) {
			System.out.println("PrintStream is null");
			flag = false;
		}
		return flag;
	}
}

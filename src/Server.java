
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public final class Server {

	// Each player connects as a client
	public static class Client{
		//position and velocity
		int x;
		int y;
		float velx;
		float vely;
		boolean active = false;
		PrintStream cout;

		// updates the position and velocity
		public void setPos(int x, int y, float velx, float vely) {
			this.x = x;
			this.y = y;
			this.velx = velx;
			this.vely =vely;

		}


	}
    public static void main(String[] args) throws Exception {
    	ArrayList<Client> clients = new ArrayList<Client>(); //list of clients connected

        try (ServerSocket serverSocket = new ServerSocket(22222)) {
            while (true) {
                Socket socket = serverSocket.accept();

                // defines a runnable client
				Runnable client = () -> {
					int cid = 0; // client's ID
					boolean stop = false;
					try {
						// Sets up the network objects.
						String address = socket.getInetAddress().getHostAddress();
						socket.setTcpNoDelay(true);
						System.out.printf("Client connected: %s%n", address);
						OutputStream os = socket.getOutputStream();
						PrintStream out = new PrintStream(os);
						InputStream is = socket.getInputStream();
						InputStreamReader isr = new InputStreamReader(is, "UTF-8");
						BufferedReader br = new BufferedReader(isr);

						String[] read; // used to read input
						String output = ""; //used to build output
						cid = clients.size(); //client ID #
						clients.add(cid, new Client()); //adds the new client to the clients list
						clients.get(cid).cout = out; //updates value of relative PrintStream
						clients.get(cid).active = true;
						char[] buf = new char[1000];
						int j = 0;
						String raw ="";
						Client cl = clients.get(cid);

						int timeOut = 0;

						while (true) {
							if (br.ready()) {
								timeOut = 0;
								raw = br.readLine();
								System.out.println("This is the raw: " + raw);
								read = raw.split("_"); // packets are delimited by '_'
								for (int k = 1; k < read.length; k++) {
									System.out.println(read[k]);
								switch (read[k].charAt(0)) {
								case 'i': // send a client it's ID
									output += "i";
									output += Integer.toString(cid);
									break;
								case 'p': //Updates positions and velocity
									String tempx = "";
									String tempy = "";
									String tempVelx = "";
									String tempVely = "";
									int mode = 0;

									for (int i = 1; i < read[k].length(); i++) {

										if (read[k].charAt(i) != ',' && mode == 0) {
											tempx += read[k].charAt(i);
										} else if (read[k].charAt(i) == ',') {
											mode++;
										} else if (read[k].charAt(i) != ',' && mode == 1) {
											tempy += read[k].charAt(i);
										} else if (read[k].charAt(i) != ',' && mode == 2) {
											tempVelx += read[k].charAt(i);
										} else if (read[k].charAt(i) != ',' && mode == 3) {
											tempVely += read[k].charAt(i);
										}
									}
									clients.get(cid).setPos(Integer.parseInt(tempx),Integer.parseInt(tempy), Float.parseFloat(tempVelx), Float.parseFloat(tempVely));
									System.out.println("Client: " + cid);
									System.out.println("x position: " + cl.x);
									System.out.println("y position: " + cl.y);
									System.out.println("x vel: " + cl.velx);
									System.out.println("y vel: " + cl.vely);
									out.flush();
									os.flush();
									break;
								case 'a': // player attacked
									for (int i = 0; i < clients.size(); i++) {
										clients.get(i).cout.println("a" + Integer.toString(cid));
									}
									break;
								case 'c': // player hit someone
									for (int i = 0; i < clients.size(); i++) {
										clients.get(i).cout.println("c" + Integer.toString(cid));
									}
									break;
								case 'd': //player died
									for (int i = 0; i < clients.size(); i++) {
										clients.get(i).cout.println("d" + Integer.toString(cid));
									}
									break;
								}
							}

							// Handles innactivity by killing a player after 10 seconds
							} else {
								Thread.sleep(100);
								timeOut++;
							}
							if (timeOut > 100) {
								for (int i = 0; i < clients.size(); i++) {
										clients.get(i).cout.println("d" + Integer.toString(cid));
									}
									Thread.sleep(500);

								}


							for (int i = 0; i < clients.size(); i++) {
								if (i != cid && clients.get(i).active) {
									Client c = clients.get(i);

									output += "_p" + Integer.toString(i) +  "," + Integer.toString(c.x) + "," +  Integer.toString(c.y) + "," + Float.toString(c.velx) + "," + Float.toString(c.vely);
								}

							}

							// sends the output
							if (!output.equals("")){
								out.println(output);
								output = "";
								Thread.sleep(40);
							}
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
				Thread clientThread = new Thread(client);
				clientThread.start();
			}
        }
    }
}

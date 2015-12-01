import java.io.*;//for input and output
import java.net.*;// for sockets library
import java.lang.*;
import java.util.*;
import java.text.*;

public class Functionsv2 {
	//Functions implemented - function attributes are changed by changing factors in constants
		/* createServerSocket - creates a socket in server with SERVER PORT NUMBER
		 * createServerSocket - creates a socket in client with CLIENT PORT NUMBER
		 * createPacket - creates a packet of size PACKET_SIZE - no field is set in this function
		 * createPacket - (destinationInetAddreess, int destinationHostPortNumber,String packetData) - takes the inputs and sets the fields of the packet
		 * pause - pauses the execution of thread for human reading and debugging
		 * delay - time delay between sending each packet
		 * makeTextFile - input as Datav2.FILENAME , DATA
		 * displayPacket - displays the string in the packet
		 * display(string s1) - displays the string s1 - with a pause of PAUSE_DURATION
		 * getFileSize - gets the size of the DATAv2.FILNENAME
		 * readPacketFromFile(index) - takes the querried index and returns a string of length PACKET_SIZE
		 * getAddressOfMinServer(s1,s2,s3,s4)- returns server address with minimum s vlaue
		 * 
		 */
	public static DatagramSocket createServerSocket() throws SocketException
	{
		DatagramSocket skt=new DatagramSocket(Datav2.PORT_NUMBER_SERVER);
		return skt;
	}
	
	public static DatagramSocket createClientSocket() throws SocketException
	{
		DatagramSocket skt=new DatagramSocket(Datav2.PORT_NUMBER_CLIENT);
		return skt;
	}
	
	public static DatagramPacket createPacket()
	{
		byte [] buffer =new byte[Datav2.PACKET_SIZE];
		DatagramPacket pkt=new DatagramPacket(buffer,buffer.length);
		return pkt;
	}
	
	public static DatagramPacket createPacket(String destinationInetAddreess, int destinationHostPortNumber,String packetData) throws UnknownHostException
	{
		byte [] buffer =new byte[Datav2.PACKET_SIZE];
		DatagramPacket pkt=new DatagramPacket(buffer,buffer.length);
		pkt.setAddress(InetAddress.getByName(destinationInetAddreess));
		pkt.setPort(destinationHostPortNumber);
		pkt.setData(packetData.getBytes());
		return pkt;
	}
	
	public static void updatePacket(DatagramPacket pkt,String destinationInetAddreess, int destinationHostPortNumber,String packetData) throws UnknownHostException
	{
		pkt.setAddress(InetAddress.getByName(destinationInetAddreess));
		pkt.setPort(destinationHostPortNumber);
		pkt.setData(packetData.getBytes());
	}	
	
	public static void pause() throws InterruptedException 
	{
		Thread.sleep(Datav2.PAUSE_DURATION);
	}
	
	public static void delay() throws InterruptedException 
	{
		Thread.sleep(Datav2.DELAY_DURATION);
	}
	
	public static void makeTextFile(String fwrite) throws IOException
	{
		BufferedWriter writer=new BufferedWriter(new FileWriter(fwrite));
		int i,j,k;
		System.out.println(Datav2.NUM_UNIQUE_CHARACTERS);
		System.out.println(Datav2.PACKET_SIZE);
		k=65;
		for(i=0;i<Datav2.NUM_UNIQUE_CHARACTERS;i++)
		{
			if(i%2==0){k=65;}else{k=66;}
			for(j=0;j<Datav2.PACKET_SIZE;j++)
			{
				writer.write((char)k);
			}
		}
		writer.close();
	}

	public static void displayPacket(DatagramPacket pkt) 
	{
		System.out.println(new String(pkt.getData()).trim());
		/*
		try {
			Functionsv2.pause();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public static String getPacketString(DatagramPacket pkt) 
	{
		String s1,s2;
		s1= new String(pkt.getData()).trim();
		//s2=s1.substring(s1.length()-14, s1.length()-13);
		//System.out.println(s1.length());
		//System.out.println(s2);
		//System.out.println(s2.length());
		return s1;
	}

	public static double getSk(double delaysFinal[],int k)
	{
		double a=0;int i;
		for(i=0;i<k;i++)
		{
			a=a+delaysFinal[i];
		}
		a=a/k;
		a=a/Datav2.DELAY_DURATION;
		return a;
	}
	
	public static void display(String s1)
	{
		if(Datav2.VERBOSE==1)
		{System.out.println(s1);}
		try {
			Functionsv2.pause();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getFileSize()
	{
		int filesize= (int) new File(Datav2.FILENAME).length();
		return filesize;
	}
	
	public static String readPacketFromFile(char[] textData,int query)
	{
		/*
		 * suppose NUM_UNIQUE_CHARS=4 PACKET_SIZE=2.Then the fil will be AABBCCDD
		 * if index==2, then output should be BB
		 */
		char[] temp=Arrays.copyOfRange(textData, (query-1)*Datav2.PACKET_SIZE,query*Datav2.PACKET_SIZE);
		String s1=new String(temp);
		return s1;
	}
	
	public static String getAddressOfMinServer(double s1,double s2,double s3,double s4)
	{
		String ans = Datav2.SERVER1_ADDRESS;
		if(s1<s2&&s1<s3&&s1<s4){ans=Datav2.SERVER1_ADDRESS;}
		else if(s2<s1&&s2<s3&&s2<s4){ans=Datav2.SERVER2_ADDRESS;}
		else if(s3<s1&&s3<s2&&s3<s4){ans=Datav2.SERVER3_ADDRESS;}
		else if(s4<s1&&s4<s2&&s4<s3){ans=Datav2.SERVER4_ADDRESS;}
		return ans;
	}
}


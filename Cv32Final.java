import java.io.*;//for input and output
import java.net.*;// for sockets library
import java.nio.ByteBuffer;
import java.lang.*;
import java.util.*;
import java.lang.instrument.Instrumentation;

public class Cv32Final {
	public static volatile String commandOuter;
	public static volatile int offset;
	
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		commandOuter="play";
		offset=0;
		Thread inputThread = new Thread(new Runnable() {
	        @Override
	        public void run() 
	        {
	        	Scanner s1=new Scanner(System.in);
	    		String offsetString;
	    		String command;
	    		while(true)
	    		{
	    			command=s1.next();
	    			commandOuter=command;
	    			if(command.contains("play")){}
	    			if(command.contains("pause")){}
	    			if(command.contains("rewind"))
	    			{
	    				offsetString=s1.next();
	    				offset=Integer.parseInt(offsetString);
	    				//System.out.println(offset);
	    				//System.out.println(command);
	    			}
	    			if(command.contains("forward"))
	    			{
	    				offsetString=s1.next();
	    				offset=Integer.parseInt(offsetString);
	    				//System.out.println(offset);
	    				//System.out.println(command);
	    			}
	    			//System.out.println(command);
	    		}
	        }
	    });
	    inputThread.start();
		
		int i;
		DatagramSocket skt=Functionsv2.createClientSocket();
		skt.setSoTimeout(Datav2.SOCKET_TIMEOUT);
		DatagramPacket request=Functionsv2.createPacket();
		
		DatagramPacket reply=Functionsv2.createPacket();
		String msg="0";byte[] b=msg.getBytes();
		request.setData(b);
		//now contacting the serverss for the first time
		InetAddress host=InetAddress.getByName(Datav2.SERVER1_ADDRESS);
		double s1avg,s2avg,s3avg,s4avg;
		
		for (i=1;i<=4;i++)
		{
			if(i==1)host=InetAddress.getByName(Datav2.SERVER1_ADDRESS);
			else if(i==2)host=InetAddress.getByName(Datav2.SERVER2_ADDRESS);
			else if(i==3)host=InetAddress.getByName(Datav2.SERVER3_ADDRESS);
			else if(i==4)host=InetAddress.getByName(Datav2.SERVER4_ADDRESS);
			request.setAddress(host);request.setPort(Datav2.PORT_NUMBER_SERVER);
			
			reply.setAddress(host);reply.setPort(Datav2.PORT_NUMBER_SERVER);
			Functionsv2.display("request sent by client");
			skt.send(request);
			skt.receive(reply);
			Functionsv2.display("reply received by client");
		    String s1=new String(reply.getData()).trim();
		    Functionsv2.display("filesize received from server"+i+"="+s1);
		}
		
		host=InetAddress.getByName(Datav2.SERVER1_ADDRESS);
		//packet being used - request
		String requestString=new String("1");
		String requestedServerAddress=new String();
		
		double[] delays=new double[Datav2.NUM_UNIQUE_CHARACTERS];
		double delayTemp1,delayTemp2,delayTemp3,delayTemp4;
		delayTemp1=0;delayTemp2=0;delayTemp3=0;delayTemp4=0;
		double delayTemp[]=new double[4];
		double delayTempOld[]=new double[4];
		double[] delaysFinal=new double[Datav2.NUM_UNIQUE_CHARACTERS];
		
		//initialising s1avg--2,3,4
		s1avg=(double) 1.0*Datav2.DELAY_DURATION;
		s2avg=(double) 1.0001*Datav2.DELAY_DURATION;;
		s3avg=(double) 1.0002*Datav2.DELAY_DURATION;;
		s4avg=(double) 1.0003*Datav2.DELAY_DURATION;;

		int[] queryStatus=new int[4];int j;int repliesReceived=0;
		String s1TempAddress,s2TempAddress,s3TempAddress,s4TempAddress,sTempAddress;
		
		
		//asking for packets
		for (i=0;i<Datav2.NUM_UNIQUE_CHARACTERS;i=i+4)
		{	
			if(commandOuter.contains("play"))
			{
				
			}
			if(commandOuter.contains("pause"))
			{
				i=i-4;
			}
			if(commandOuter.contains("forward"))
			{
				i=i+offset-1;//-1 because i has been increased by already
				commandOuter="pause";
			}
			if(commandOuter.contains("rewind"))
			{
				i=i-offset-1;//-1 because i has been increased by already
				commandOuter="pause";
			}
			System.out.println(commandOuter+" "+offset);
			Thread.sleep(1000);
		}
		Arrays.sort(delays);
		for (i=0;i<Datav2.NUM_UNIQUE_CHARACTERS;i++)
		{
			delaysFinal[i]=delays[Datav2.NUM_UNIQUE_CHARACTERS-i-1];
		}
		double S2,S10,S20,S100,S1000,S3000;
		S2=Functionsv2.getSk(delaysFinal, 2);
		S10=Functionsv2.getSk(delaysFinal, 10);
		S20=Functionsv2.getSk(delaysFinal, 20);
		S100=Functionsv2.getSk(delaysFinal, 100);
		S1000=Functionsv2.getSk(delaysFinal, 1000);
		S3000=Functionsv2.getSk(delaysFinal, 3000);
		System.out.println("S2="+S2+" S10="+S10+" S20="+S20+" S100="+S100+" S1000="+S1000+" S3000="+S3000);
		System.out.println("done");
	}
	public static int[] reorderArray(int array[],int cycles)
	{
		int answer[]=new int[4];
		int i,j,temp;
		//System.out.println("in reorder cycles="+cycles);
		for(j=0;j<4;j++){answer[j]=array[j];}
		for (i=0;i<cycles%4;i++)
		{
			temp=answer[0];
			for(j=0;j<3;j++)
			{answer[j]=answer[j+1];}
			answer[3]=temp;
			//for(j=0;j<4;j++){System.out.println(answer[j]);}
			//System.out.println();
		}
		return answer;
	}
	
	public static double[] reorderArray(double array[],int cycles)
	{
		
		int i,j;
		double temp;
		for (i=0;i<cycles%4;i++)
		{
			temp=array[0];
			for(j=0;j<3;j++)
			{array[j]=array[j+1];}
			array[3]=temp;
		}
		return array;
	}
}

import java.io.*;//for input and output
import java.net.*;// for sockets library
import java.nio.ByteBuffer;
import java.lang.*;
import java.util.*;
import java.lang.instrument.Instrumentation;

public class Cv32FinalLog {
	public static volatile String commandOuter;
	public static volatile int offset;
	public static double startTime;
	public static PrintWriter writer;
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
	    			//System.out.println (command);
	    		}
	        }
	    });
	    inputThread.start();

		String filename=Datav2.FILENAME_CLIENT_LOG;
		writer = new PrintWriter(filename, "UTF-8");
		String sQuery;String sTime;//time is in milliseconds from start
		double packetArrivalTime;
		startTime=System.nanoTime();
	    
		int i;
		DatagramSocket skt=Functionsv2.createClientSocket();
		skt.setSoTimeout(Datav2.SOCKET_TIMEOUT);
		DatagramPacket request=Functionsv2.createPacket();
		DatagramPacket lastRequestPkt=Functionsv2.createPacket();
		DatagramPacket reply=Functionsv2.createServerPacket();
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
			//logWrite(request,0);
			skt.receive(reply);
			//logWrite(reply,1);
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
		for (i=0;i<=Datav2.NUM_UNIQUE_CHARACTERS+4;i=i+4)
		{	
			if(commandOuter.contains("play"))
			{
				if(i>=Datav2.NUM_UNIQUE_CHARACTERS+4)
				{
					System.out.println("ending");
					skt.close();
					writer.close();
					break;
				}
				s1TempAddress=Datav2.SERVER1_ADDRESS;s2TempAddress=Datav2.SERVER2_ADDRESS;s3TempAddress=Datav2.SERVER3_ADDRESS;s4TempAddress=Datav2.SERVER4_ADDRESS;
				repliesReceived=0;
				for(j=0;j<4;j++){delayTemp[j]=0;delayTempOld[j]=0;}
				//sending requests
				for(j=0;j<4;j++)
				{
					requestString=Integer.toString(i+j+1);
					if(j==0)
					{
						requestedServerAddress=Datav2.SERVER1_ADDRESS;
						Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
						delayTemp1=System.nanoTime();
					}
					else if(j==1)
					{
						requestedServerAddress=Datav2.SERVER2_ADDRESS;
						Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
						delayTemp2=System.nanoTime();
					}
					else if(j==2)
					{
						requestedServerAddress=Datav2.SERVER3_ADDRESS;
						Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
						delayTemp3=System.nanoTime();
					}
					else if(j==3)
					{	
						requestedServerAddress=Datav2.SERVER4_ADDRESS;
						Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
						delayTemp4=System.nanoTime();
					}
					//System.out.println();
					//System.out.println(requestString+" req sent");
					skt.send(request);
					logWrite(request,0);
				}
				//setting lost query to [0,0,0,0]
				for(j=0;j<4;j++){queryStatus[j]=0;}
				
				//receiving replies
				//System.out.println("receiving 4replies");
				for(j=0;j<4;j++)
				{
					try
					{
						skt.receive(reply);
						logWrite(reply,1);
						String replyServerName=reply.getAddress().toString();
						replyServerName=replyServerName.substring(1,replyServerName.length());
						if(Datav2.SERVER1_ADDRESS.contains(replyServerName))
						{
							delayTemp1=System.nanoTime()-delayTemp1;delayTemp1=delayTemp1/1000000;delayTemp1=delayTemp1;//+Datav2.DELAY_DURATION;
							queryStatus[0]=1;
							delayTemp[0]=delayTemp1;
						}
						else if(Datav2.SERVER2_ADDRESS.contains(replyServerName))
						{
							delayTemp2=System.nanoTime()-delayTemp2;delayTemp2=delayTemp2/1000000;delayTemp2=delayTemp2;//+Datav2.DELAY_DURATION;
							queryStatus[1]=1;
							delayTemp[1]=delayTemp2;
						}
						else if(Datav2.SERVER3_ADDRESS.contains(replyServerName))
						{
							delayTemp3=System.nanoTime()-delayTemp3;delayTemp3=delayTemp3/1000000;delayTemp3=delayTemp3;//+Datav2.DELAY_DURATION;
							queryStatus[2]=1;
							delayTemp[2]=delayTemp3;
						}
						else if(Datav2.SERVER4_ADDRESS.contains(replyServerName))
						{
							delayTemp4=System.nanoTime()-delayTemp4;delayTemp4=delayTemp4/1000000;delayTemp4=delayTemp4;//+Datav2.DELAY_DURATION;
							queryStatus[3]=1;
							delayTemp[3]=delayTemp4;
						}
					}
					catch(Exception e)
					{
						System.out.println("timeout "+i);
					}
				}
				for(j=0;j<4;j++)
				{
					if(delayTempOld[j]==delayTemp[j])
					{
						delayTemp[j]=delayTemp[j]+Datav2.SOCKET_TIMEOUT;
					}
				}
				
				for (j=0;j<4;j++)
				{
					//System.out.print(" "+queryStatus[j]);
					if(queryStatus[j]==1){repliesReceived++;}
				}
				//System.out.println();
				//for(j=0;j<4;j++){System.out.print(" "+delayTemp[j]);}
				//Thread.sleep(5000);	
				
				int cycles=0;int[] queryStatusNew=new int[4];
				for(j=0;j<4;j++){delayTempOld[j]=delayTemp[j];}
				//int queryStatusNewCorrect[]=new int[4];
				
				//add part that calculates the delays
				//now writing the code that handles exception
				while(repliesReceived<4)//Change Caution !!!! change to string server names
				{
					cycles++;
					if(cycles==1)
					{
						for(j=0;j<4;j++){delayTempOld[j]=delayTemp[j];delayTemp[j]=0;}
					}
					//Thread.sleep(2000);
					//System.out.println("packet(s) lost");
					//determining which packets were lost
						//setting inital values
						for(j=0;j<4;j++){queryStatusNew[j]=0;}
						
						//cyclic exchange of server addresses 
						// String s1TempAddress,s2TempAddress,s3TempAddress,s4TempAddress,sTempAddress;
						sTempAddress=s4TempAddress;
						s4TempAddress=s3TempAddress;
						s3TempAddress=s2TempAddress;
						s2TempAddress=s1TempAddress;
						s1TempAddress=sTempAddress;					
						
						//sending repeat requests
						for(j=0;j<4;j++)
						{
							requestString=Integer.toString(i+j+1);
							if(j==0)
							{
								requestedServerAddress=s1TempAddress;
								Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
								delayTemp1=System.nanoTime();
							}
							else if(j==1)
							{
								requestedServerAddress=s2TempAddress;
								Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
								delayTemp2=System.nanoTime();
							}
							else if(j==2)
							{
								requestedServerAddress=s3TempAddress;
								Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
								delayTemp3=System.nanoTime();
							}
							else if(j==3)
							{	
								requestedServerAddress=s4TempAddress;
								Functionsv2.updatePacket(request, requestedServerAddress, Datav2.PORT_NUMBER_SERVER, requestString);
								delayTemp4=System.nanoTime();
							}
							//System.out.println("repeat req sent");
							skt.send(request);
							logWrite(request,0);
						}
						
						//resetting queryStatusNew
						for(j=0;j<4;j++){queryStatusNew[j]=0;}
						//receiving replies
					//	System.out.println("receiving 4 repeat replies");
						
						for(j=0;j<4;j++)
						{
							try
							{
								skt.receive(reply);
								logWrite(reply,1);
								String replyServerName=reply.getAddress().toString();
								replyServerName=replyServerName.substring(1,replyServerName.length());
								if(s1TempAddress.contains(replyServerName))
								{
									delayTemp1=System.nanoTime()-delayTemp1;delayTemp1=delayTemp1/1000000;delayTemp1=delayTemp1;//+Datav2.DELAY_DURATION;
									queryStatusNew[0]=1;
									delayTemp[0]=delayTemp1;
								}
								else if(s2TempAddress.contains(replyServerName))
								{
									delayTemp2=System.nanoTime()-delayTemp2;delayTemp2=delayTemp2/1000000;delayTemp2=delayTemp2;//+Datav2.DELAY_DURATION;
									queryStatusNew[1]=1;
									delayTemp[1]=delayTemp2;
								}
								else if(s3TempAddress.contains(replyServerName))
								{
									delayTemp3=System.nanoTime()-delayTemp3;delayTemp3=delayTemp3/1000000;delayTemp3=delayTemp3;//+Datav2.DELAY_DURATION;
									queryStatusNew[2]=1;
									delayTemp[2]=delayTemp3;
								}
								else if(s4TempAddress.contains(replyServerName))
								{
									delayTemp4=System.nanoTime()-delayTemp4;delayTemp4=delayTemp4/1000000;delayTemp4=delayTemp4;//+Datav2.DELAY_DURATION;
									queryStatusNew[3]=1;
									delayTemp[3]=delayTemp4;
								}
							}
							catch(Exception e)
							{
								System.out.println("timeout "+i);
							}
						}
						for(j=0;j<4;j++)
						{
							if(delayTemp[j]==0){delayTemp[j]=Datav2.SOCKET_TIMEOUT;}
						}
						
						//queryStatusNewCorrect=reorderArray(queryStatusNew,cycles);
						//delayTempNewCorrect=reorderArray(delayTempNew,cycles);
						repliesReceived=0;
						//System.out.println("in cycle="+cycles+" queryStatus=");
						for(j=0;j<4;j++)
						{
							if(queryStatus[j]==0&&queryStatusNew[j]==0){delayTempOld[j]=delayTempOld[j]+Datav2.SOCKET_TIMEOUT;}
							if(queryStatus[j]==0&&queryStatusNew[j]==1){delayTempOld[j]=delayTempOld[j]+delayTemp[j];}
							if(queryStatus[j]==1){delayTempOld[j]=delayTempOld[j];}
							
							if(queryStatus[j]==1||queryStatusNew[j]==1){queryStatus[j]=1;repliesReceived++;}
							if(queryStatus[j]*queryStatusNew[j]==0){}
							//{delayTemp[j]=delayTemp[j]+delayTempNew[j];}
							
						}
						
				}
				for(j=0;j<4;j++){delays[i+j]=delayTempOld[j];}
			}
			if(commandOuter.contains("pause"))
			{
				i=i-4;
			}
			if(commandOuter.contains("forward"))
			{
				i=i+offset-4;//-1 because i has been increased by already
				commandOuter="pause";
			}
			if(commandOuter.contains("rewind"))
			{
				i=i-offset-4;//-1 because i has been increased by already
				commandOuter="pause";
			}
			
			//System.out.println(commandOuter+" "+offset+" "+i);
			//Thread.sleep(1000);
		}
		//sending stop signal to servers
		
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
		Functionsv2.writeSkFile(delaysFinal);
		System.out.println("S2="+S2+" S10="+S10+" S20="+S20+" S100="+S100+" S1000="+S1000+" S3000="+S3000);
		System.out.println("done");
		inputThread.stop();
		
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
	
	public static void openLog(PrintWriter writer,String sQuery,String sTime,String serverName)
	{
		String s1=serverName+" "+sQuery+" "+sTime;
		writer.println(s1);
	}
	
	public static void logWrite(DatagramPacket pkt,int id)
	{
		//queryNumber,id servername,time
		//the packet number , sent/received -0/1 , servername,time
		double endTime=System.nanoTime();
		String servername=pkt.getAddress().toString();
		String queryNumber= new String(pkt.getData()).trim();
		String sFinal=null;
		String temp;
		
		if(id==0){}
		else if(id==1)
		{
			//queryNumber=queryNumber.substring(Datav2.NUM_UNIQUE_CHARACTERS, queryNumber.length());
			//queryNumber=queryNumber.substring(0,Datav2.NUM_UNIQUE_CHARACTERS);
			
			System.out.println(queryNumber);
			System.out.println(queryNumber.length());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(queryNumber.substring(Datav2., endIndex));
		}
		sFinal=queryNumber+" "+Integer.toString(id)+" "+servername+" "+Double.toString((endTime-startTime)/1000000000)+" sec";
		writer.println(sFinal);
	}
}

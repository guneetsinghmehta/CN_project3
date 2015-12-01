import java.io.*;//for input and output
import java.net.*;// for sockets library
import java.lang.*;
import java.util.*;
import java.text.*;

/*
 * NOTES and ideas
 * 1. make the size of packets sent by client small - say 10 bytes and not the same as server , this will increase the performance
 */

public class Datav2 {
	//static class containing the variables and functions
	//variables used
	/* PORT_NUMBER_CLIENT - port number of client
	 * PORT_NUMBER_SERVER - port number of server
	 * BUFFER_SIZE_CLIENT - buffer size - defining how many frames are there in the buffer of client (32 taken for project)
	 * NUM_UNIQUE_CHARACTERS -if value is 2 then there will PACKET_SIZE number of repetitions of A and B 
	 * PACKET_SIZE  -(default=1 - need to change) defines the size of each packet of data sent in packet. ALso the number of repetitions of each character in file 
	 * VERBOSE  - if 1 then it prints out the transactions , if 0 then does not
	 * PAUSE_DURATION - duration of pause after each step (units milli seconds)
	 * DELAY_DURATION - (default - 100) duration of delay between sending each packet 
	 * FILENAME - filename of the concerned file being requested
	 * SERVER1_ADDRESS - stores inet address of server1
	 * SERVER2_ADDRESS
	 * SERVER3_ADDRESS
	 * SERVER4_ADDRESS
	 * CLIENT_ADDRESS -store inet address of server2
	 * BETA - this is used for calculation of s1avg,s2,s3and s4avg
	 * MAX_REPEAT- maximum times a server can be reset
	 */ 
	public static final  int PORT_NUMBER_CLIENT=6790;
	public static final  int PORT_NUMBER_SERVER=6789;
	public static final  int BUFFER_SIZE_CLIENT=32;
	public static final  int NUM_UNIQUE_CHARACTERS=12000;
	public static final  int PACKET_SIZE=1024;
	public static final  int VERBOSE=1;
	public static final int PAUSE_DURATION=200;
	public static final int DELAY_DURATION=100;
	public static final String FILENAME="text2.txt";
	public static final  String SERVER1_ADDRESS="10.10.3.2";
	//public static final  String SERVER1_ADDRESS="localhost";
	public static final  String SERVER2_ADDRESS="10.10.4.2";
	public static final  String SERVER3_ADDRESS="10.10.1.2";
	public static final  String SERVER4_ADDRESS="10.10.2.2";
	//public static final  String CLIENT_ADDRESS="localhost";
	public static final  String CLIENT_ADDRESS="10.10.7.1";
	public static final float BETA=(float)0.9;
	public static final int MAX_REPEAT=50;
	public static final int SOCKET_TIMEOUT=250;
	
}

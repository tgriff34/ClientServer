import java.io.*;
import java.net.*;
import java.util.*;

/*
Name: Tristan Griffin
Class: ITCS 3166 - 051
Date: APR - 30 - 2017
FINAL PROJECT
 */

public class Client {
    public static void main(String args[]) {
        String hostName = "localhost";
        int portNumber = 5566;
        Socket clientSocket;

       try {
           //connects to server
           clientSocket = new Socket(hostName, portNumber);
           System.out.println("Connecting to ... " + hostName + " with port: " + portNumber);

           //establishes read/write capabilities
           PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
           BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

           Scanner userInput = new Scanner(System.in);

           String output;
           String input;
           boolean validInput = true;

           //reads output from server
           while ((output = br.readLine()) != null) {
               if (output.equals("CLOSE")){
                   break;
               }
               //When server wants a response from the client it receives it here
               if (output.equals("ROWINPUT")) {
                   do {
                       input = userInput.nextLine();
                       if (input.equals("A") || input.equals("a")) {
                           input = "1";
                           validInput = true;
                       } else if (input.equals("B") || input.equals("b")) {
                           input = "2";
                           validInput = true;
                       } else if (input.equals("C") || input.equals("c")) {
                           input = "3";
                           validInput = true;
                       } else if (input.equals("D") || input.equals("d")) {
                           input = "4";
                           validInput = true;
                       } else if (input.equals("E") || input.equals("e")) {
                           input = "5";
                           validInput = true;
                       } else {
                           System.out.println("Please enter a valid input for the row (i.e. A).");
                           validInput = false;
                       }
                   } while (!validInput);
                   pw.println(input);
               }
               if (output.equals("COLUMNINPUT")) {
                   do {
                       input = userInput.nextLine();
                       if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5")) {
                           validInput = true;
                       } else {
                           System.out.println("Please enter a valid input for the column. (i.e. 1)");
                           validInput = false;
                       }
                   } while (!validInput);
                   pw.println(input);
               }
               //otherwise it is going to print whatever the client sends to it
               if (!output.equals("COLUMNINPUT") && !output.equals("ROWINPUT")){
                   System.out.println(output);
               }
           }

           //closes connections
           pw.close();
           br.close();
           clientSocket.close();
           userInput.close();

       } catch (IOException e) {
           System.out.println(e);
       }
    }
}

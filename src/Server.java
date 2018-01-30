import java.net.*;
import java.io.*;
import java.util.*;

/*
Name: Tristan Griffin
Class: ITCS 3166 - 051
Date: APR - 30 - 2017
FINAL PROJECT
 */

public class Server {

    public static void main(String[] args) {

        //Variables
        int maxNumberOfClients = 2;
        Socket clientSocket[] = new Socket[maxNumberOfClients];
        int SocketID[] = new int[maxNumberOfClients];
        int masterGameBoard[][] = new int[5][5];
        char playerGameBoard[][] = new char[5][5];
        int numberOfSquares[] = new int[25];
        ServerSocket serverSocket;
        int portNumber = 5566;

        try {

            serverSocket = new ServerSocket(portNumber);
            System.out.println("Creating connection to port: " + portNumber);

            /* =====================================================|
             |                                                      |
             |                 Connects to players                  |
             |                                                      |
             |===================================================== */
            int numberOfClients = 0;

            //waits for both clients to connect
            while (numberOfClients < 2) {
                clientSocket[numberOfClients] = serverSocket.accept();
                System.out.println("Connected to client.");
                SocketID[numberOfClients] = clientSocket[numberOfClients].hashCode();

                numberOfClients++;
            }

            //allows the reading and writing of data to clients
            PrintWriter pw1 = new PrintWriter(clientSocket[0].getOutputStream(), true);
            PrintWriter pw2 = new PrintWriter(clientSocket[1].getOutputStream(), true);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(clientSocket[0].getInputStream()));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(clientSocket[1].getInputStream()));

            /* =====================================================|
             |                                                      |
             |               Creates random gameboard               |
             |                                                      |
             |===================================================== */
            for (int i = 0; i < numberOfSquares.length; i++) {
                numberOfSquares[i] = i;
            }
            int cap = 24;
            int max = 6;
            Random rand = new Random();
            //randomly choose 7 slots on the gameboard that will become ships
            //this creates the master gameboard which will be used to determine
            //if the players have hit or missed a ship without showing the players the board.

            while (max >= 0) {
                int random = rand.nextInt(cap + 1);
                int loc = numberOfSquares[random];
                masterGameBoard[loc%5][loc/5] = 1;
                numberOfSquares[random] = numberOfSquares[cap];
                cap--;
                max--;
            }
            //creates the board that the players will see
            for (int i = 0; i < playerGameBoard.length; i++) {
                for (int j = 0; j < playerGameBoard[i].length; j++) {
                    playerGameBoard[i][j] = '?';
                }
            }
            /* =====================================================|
             |                                                      |
             |              While the game is running...            |
             |                                                      |
             |===================================================== */
            boolean playing = true;
            boolean playerOne = true;
            boolean playerTwo = false;
            String row, column;
            int playerOneScore = 0, playerTwoScore = 0, totalHits = 0;

            while (playing) {
                if (playerOne) {
                    if (totalHits == 7) { playerOne = false; playerTwo = false; playing = false; break; }
                    pw2.println("Waiting for other player to take their turn...");
                    pw1.println("Current state of the game: ");
                    pw1.println("Rules: ? = Unknown (space not hit), H = Hit!, M = Miss!");
                    pw1.println("Your current score is: " + playerOneScore + " ships destroyed!");
                    for (int i = 0; i < playerGameBoard.length; i++) {
                        for (int j = 0; j < playerGameBoard[i].length; j++) {
                            pw1.print(playerGameBoard[i][j] + " ");
                        }
                        pw1.println();
                    }
                    pw1.println("What row would you like to fire into next? Choose A - E: ");
                    pw1.println("ROWINPUT");
                    row = br1.readLine();
                    System.out.println(SocketID[0] + ": Player one has chosen row: " + row);
                    pw1.println("What column would you like to fire into next? Choose 1 - 5: ");
                    pw1.println("COLUMNINPUT");
                    column = br1.readLine();
                    System.out.println(SocketID[0] + ": Player one has chosen column: " + column);

                    if (masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] == 2) {
                        pw1.println("You or the other player already fired in this square please choose a different square.");
                    }
                    else if (masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] == 1) {
                        pw1.println("Hit!");
                        playerGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 'H';
                        masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 2;
                        playerOneScore++;
                        totalHits++;
                        playerOne = false;
                        playerTwo = true;
                    }
                    else {
                        pw1.println("Miss!");
                        playerGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 'M';
                        masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 2;
                        playerOne = false;
                        playerTwo = true;
                    }

                    //TESTING
                    System.out.println("========================");
                    for (int i = 0; i < playerGameBoard.length; i++) {
                        for (int j = 0; j < playerGameBoard[i].length; j++) {
                            System.out.print(playerGameBoard[i][j] + " ");
                        }
                        System.out.println();
                    }

                    System.out.println();

                    for (int i = 0; i < masterGameBoard.length; i++) {
                        for (int j = 0; j < masterGameBoard[i].length; j++) {
                            System.out.print(masterGameBoard[i][j] + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("========================");
                    //TESTING
                }

                //when it's players two turn
                if (playerTwo) {
                    if (totalHits == 7) { playerOne = false; playerTwo = false; playing = false; break; }
                    //shows gameboard
                    pw1.println("Waiting for other player to take their turn...");
                    pw2.println("Current state of the game: ");
                    pw2.println("Rules: ? = Unknown (space not hit), H = Hit!, M = Miss!");
                    pw2.println("Your current score is: " + playerTwoScore + " ships destroyed!" );
                    for (int i = 0; i < playerGameBoard.length; i++) {
                        for (int j = 0; j < playerGameBoard[i].length; j++) {
                            pw2.print(playerGameBoard[i][j] + " ");
                        }
                        pw2.println();
                    }
                    //What row do they want to fire in
                    pw2.println("What row would you like to fire into next? Choose A - E:");
                    pw2.println("ROWINPUT");
                    row = br2.readLine();
                    System.out.println(SocketID[1] + ": Player two has chosen row: " + row);
                    //What column they wwant to fire in
                    pw2.println("What column would you like to fire into next? Choose 1 - 5: ");
                    pw2.println("COLUMNINPUT");
                    column = br2.readLine();
                    System.out.println(SocketID[1] + ": Player two has chosen row: " + column);

                    //determines if that square already has been hit for them to choose a different square
                    if (masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] == 2) {
                        pw2.println("You or the other player already fired in this square please choose a different square.");
                    }
                    //Determines if the square has a ship in it
                    else if (masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] == 1) {
                        pw2.println("Hit!");
                        playerGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 'H';
                        masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 2;
                        playerTwoScore++;
                        totalHits++;
                        playerOne = true;
                        playerTwo = false;
                    }
                    //otherwise its a miss
                    else {
                        pw2.println("Miss!");
                        playerGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 'M';
                        masterGameBoard[Integer.parseInt(row) - 1][Integer.parseInt(column) - 1] = 2;
                        playerOne = true;
                        playerTwo = false;
                    }

                    //TESTING
                    System.out.println("========================");
                    System.out.println("What players see:");
                    for (int i = 0; i < playerGameBoard.length; i++) {
                        for (int j = 0; j < playerGameBoard[i].length; j++) {
                            System.out.print(playerGameBoard[i][j] + " ");
                        }
                        System.out.println();
                    }

                    System.out.println();
                    System.out.println("1's = Ships, 2's = Players hits/misses, 0's = empty");

                    for (int i = 0; i < masterGameBoard.length; i++) {
                        for (int j = 0; j < masterGameBoard[i].length; j++) {
                            System.out.print(masterGameBoard[i][j] + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("========================");
                    //TESTING
                }
            }

            //Lets players know game is over and who won.
            pw1.println("All ships have been destroyed!");
            pw2.println("All ships have been destroyed!");
            pw1.println("Player One total score: " + playerOneScore);
            pw2.println("Player One total score: " + playerOneScore);
            pw1.println("Player Two total score: " + playerTwoScore);
            pw2.println("Player Two total score: " + playerTwoScore);
            if (playerOneScore > playerTwoScore) {
                pw1.println("You Won! And destroyed " + playerOneScore + " ships!");
                pw2.println("You Lost! Player One wins with " + playerOneScore + " ships destroyed!");
            }
            else {
                pw1.println("You Lost! Player Two wins with " + playerTwoScore + " ships destroyed!");
                pw2.println("You Won! And destroyed " + playerTwoScore + " ships!");
            }
            pw1.println("Thanks for playing!");
            pw2.println("Thanks for playing!");


            //Closes all connections
            System.out.println("Game finished. Closing connections...");
            pw1.close();
            pw2.close();
            br1.close();
            br2.close();
            clientSocket[0].close();
            clientSocket[1].close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

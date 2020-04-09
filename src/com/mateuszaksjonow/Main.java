package com.mateuszaksjonow;


import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws  ArrayIndexOutOfBoundsException {

        int yAxis = 15;
        int xAxis = 9;

        String[][] mineField = new String[yAxis][xAxis];
        String[][] mineFieldHidden = new String[yAxis][xAxis];
        for (int y = 0; y < yAxis; y++) {
            for (int x = 0; x < xAxis; x++) {
                mineField[y][x]  = "/";
                mineFieldHidden[y][x] = ".";
            }
        }
        mineSweeper(mineField, mineFieldHidden, yAxis, xAxis);
    }

    private static void mineSweeper(String[][] mineField, String[][] mineFieldHidden, int yAxisStarting, int xAxisStarting)  {
        //decrements fields for array use
        int yAxis = yAxisStarting - 1;
        int xAxis = xAxisStarting - 1;
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        int mineQuantity = 1;
        try {
            mineQuantity = scanner.nextInt();
        }catch (InputMismatchException e) {
            System.out.println("Wrong input! 1 mine added");
        }

        //starting game, choose first field to uncover
        printMineField(mineFieldHidden, yAxis, xAxis);
        System.out.print("Claim a cell as free: ");
        int startingY = scanner.nextInt() - 1;
        int startingX = scanner.nextInt() - 1;
        scanner.nextLine();

        placeMines(mineField, mineQuantity, startingY, startingX, yAxis, xAxis);
        placeNumbers(mineField, yAxis, xAxis);
        mineFieldHidden[startingY][startingX] = mineField[startingY][startingX];
        checkForUncover(mineField, mineFieldHidden, startingY, startingX, yAxis, xAxis);
        boolean quit = false;
        int minesFound = 0;
        int flagsCounter = 0;
        System.out.println("Instruction: type 2 numbers: first for Y Axis, second for X Axis. Then type 'mine' to flag field or 'free' to uncover it.");
        while (!quit) {
            System.out.println();
            printMineField(mineFieldHidden, yAxis, xAxis);

            boolean found = false;
            while (!found) {
                System.out.print("Set/unset mines marks or claim a cell as free: ");
                int y = startingY;
                int x = startingX;
                try {
                    x = scanner.nextInt() - 1;
                    y = scanner.nextInt() - 1;
                }catch (InputMismatchException e) {
                    System.out.println("Wrong input!");
                }
                String command = scanner.next();
                scanner.nextLine();
                //check if field is covered
                if (mineFieldHidden[x][y].matches("[\\d/]")) {
                    System.out.println("You already uncovered this field!");
                } else {
                    if (command.equalsIgnoreCase("free")) {
                        //check for mine
                        if (mineField[x][y].equalsIgnoreCase("X")) {
                            printMineField(mineField, yAxis, xAxis);
                            System.out.println("You stepped on a mine and failed!");
                            quit = true;
                        }//uncovers if empty
                        else if (mineField[x][y].equals("/")) {
                            mineFieldHidden[x][y] = "/";
                            checkForUncover(mineField, mineFieldHidden, y, x, yAxis, xAxis);
                        }//shows digit
                        else if (mineField[x][y].matches("\\d")) {
                            mineFieldHidden[x][y] = mineField[x][y];
                        }
                        found = true;
                    } else if (command.equalsIgnoreCase("mine")) {
                        //checks if flagged field is a mine, counts flags and mines
                        if (mineField[x][y].equalsIgnoreCase("X") && mineFieldHidden[x][y].equals(".")) {
                            mineFieldHidden[x][y] = "*";
                            flagsCounter++;
                            minesFound++;
                        }//unflag mine
                        else if (mineField[x][y].equalsIgnoreCase("X") && mineFieldHidden[x][y].equals("*")) {
                            mineFieldHidden[x][y] = ".";
                            flagsCounter--;
                            minesFound--;
                        }//flag and unflag non mine fields
                        else if (mineFieldHidden[x][y].equals(".")) {
                            mineFieldHidden[x][y] = "*";
                            flagsCounter++;
                        }else if (mineFieldHidden[x][y].equals("*")) {
                            mineFieldHidden[x][y] = ".";
                            flagsCounter--;
                        }
                        found = true;
                    } else {
                        System.out.println("Unknown command");
                    }
                    if (minesFound == mineQuantity && flagsCounter <= mineQuantity) {
                        printMineField(mineFieldHidden, yAxis, xAxis);
                        System.out.println("Congratulations! You found all mines!");
                        quit = true;
                    }
                }
            }
        }
    }

    //print hiddenMineField and frame around it
    private static void printMineField(String[][] mineField, int yAxis, int xAxis) {
        int counter = 1;
        for (int i = 0; i < mineField.length; i++) {
            if (i == 0) {
                if (yAxis > 9) System.out.print(" ");
                System.out.print(" │");
                for (int x = 0; x <= xAxis; x++) System.out.print(x+1);
                System.out.println("│");
                if (yAxis > 9) System.out.print(" ");
                System.out.print("—│");
                for (int x = 0; x <= xAxis; x++) System.out.print("-");
                System.out.println("|");
            }
            String[] row = mineField[i];
            System.out.print(counter);
            if (yAxis > 9 && counter < 10) System.out.print(" ");
            System.out.print("|");
            for (String string : row) System.out.print(string);
            counter++;
            System.out.println("|");
        }
        if (yAxis > 9 && counter < 10) System.out.print(" ");
        if (yAxis > 9) System.out.print(" ");
        System.out.print("—│");
        for (int x = 0; x <= xAxis; x++) System.out.print("-");
        System.out.println("|");
    }

    private static String[][] placeMines(String[][] mineField, int mineQuantity, int y, int x, int yAxis, int xAxis) {
        Random random = new Random();
        int mineCounter = 0;
        while (mineCounter < mineQuantity) {
            int row = random.nextInt(yAxis);
            int column = random.nextInt(xAxis);
            //checks if isn't initial uncovered field
            if (!(row == x && column == y)) {
                //checks if not already a mine
                if (!mineField[row][column].equalsIgnoreCase("X")) {
                    mineField[row][column] = "X";
                    mineCounter++;
                }
            }
        }
        return mineField;
    }

    //depending on position checks how many fields are around
    private static String[][] checkForUncover(String[][] mineField, String[][] mineFieldHidden, int y, int x, int yAxis, int xAxis) {
        if (y > 0) uncoverField(mineField, mineFieldHidden, y-1, x, yAxis, xAxis);
        if (x > 0) uncoverField(mineField, mineFieldHidden, y, x-1, yAxis, xAxis);
        if (y > 0 && x > 0) uncoverField(mineField, mineFieldHidden, y-1, x-1, yAxis, xAxis);
        if (y < yAxis) uncoverField(mineField, mineFieldHidden, y+1, x, yAxis, xAxis);
        if (x < xAxis) uncoverField(mineField, mineFieldHidden, y, x+1, yAxis, xAxis);
        if (y < yAxis && x < xAxis) uncoverField(mineField, mineFieldHidden, y+1, x+1, yAxis, xAxis);
        if (y > 0 && x < xAxis) uncoverField(mineField, mineFieldHidden, y-1, x+1, yAxis, xAxis);
        if (y < yAxis && x > 0) uncoverField(mineField, mineFieldHidden, y+1, x-1, yAxis, xAxis);
        return mineFieldHidden;
    }

    //uncovers new field and checks for more fields to uncover
    private static void uncoverField(String[][] mineField, String[][] mineFieldHidden, int y, int x, int yAxis, int xAxis) {
        if (mineField[y][x].matches("[\\d/]") && !mineFieldHidden[y][x].matches("[\\d/]")) {
            mineFieldHidden[y][x] = mineField[y][x];
            checkForUncover(mineField, mineFieldHidden, y, x, yAxis, xAxis);
        }
    }

    //depending on position placing numbers around mine
    private static String[][] placeNumbers(String[][] mineField, int yAxis, int xAxis) {
        for (int y = 0; y < mineField.length; y++) {
            for (int x = 0; x < mineField[y].length; x++) {
                if (mineField[y][x].equalsIgnoreCase("X")) {
                    if (y > 0) placeNumber(mineField, y-1, x);
                    if (x > 0) placeNumber(mineField, y, x-1);
                    if (y > 0 && x > 0) placeNumber(mineField, y-1, x-1);
                    if (y < yAxis) placeNumber(mineField, y+1, x);
                    if (x < xAxis) placeNumber(mineField, y, x+1);
                    if (y < yAxis && x < xAxis) placeNumber(mineField, y+1, x+1);
                    if (y > 0 && x < xAxis) placeNumber(mineField, y-1, x+1);
                    if (y < yAxis && x > 0) placeNumber(mineField, y+1, x-1);
                }
            }
        }
        return mineField;
    }

    //places number or increments it
    private static void placeNumber(String[][] mineField, int y, int x) {
        if (!mineField[y][x].equalsIgnoreCase("X")) {
            if (mineField[y][x].equals("/")) {
                mineField[y][x] = "1";
            }else if (mineField[y][x].matches("\\d")) {
                mineField[y][x] = Integer.toString(Integer.parseInt(mineField[y][x]) + 1);
            }
        }
    }
}


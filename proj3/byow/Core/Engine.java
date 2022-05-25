package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.awt.*;
import java.util.*;
import java.


import byow.TileEngine.Tileset;
import byow.lab12.*;
import java.io.*;
import edu.princeton.cs.algs4.StdDraw;
import net.sf.saxon.type.StringConverter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private Random random;
    private Position position;
    private long seed;
    private TETile[][] world;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        world = new TETile[WIDTH][HEIGHT];
        World myWorld = new World(WIDTH, HEIGHT, seed);
        myWorld = myWorld.generateMyWorld();
        ter.renderFrame(world);
        mainMenu();
        createNewGame();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        if (input.toUpperCase().contains("N") && input.toUpperCase().contains("S")) {
            int start = input.toUpperCase().indexOf("N") + 1;
            int end = input.toUpperCase().indexOf("S");
            if (input.substring(start, end).length() > 0) {
                seed = Long.valueOf(input.substring(start, end));
            } else {
                throw new IllegalArgumentException("Please enter a seed.");
            }
        }
        World newWorld = new World(WIDTH, HEIGHT, seed);
        return newWorld.myWorld;
    }

//    private void createWorld() {
//        TETile[][] createdWorld =
//    }

    private void mainMenuFonts() {
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Times New Roman", Font.BOLD, 25));
        StdDraw.text(WIDTH, HEIGHT, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Times New Roman", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Quit (Q)");
        StdDraw.show();
    }

    private void mainMenu() {
        mainMenuFonts();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char nextKey = StdDraw.nextKeyTyped();
                switch (nextKey) {
                    case 'N':
                    case 'n': {
                        returnSeed();
                        break;
                    }
                    case 'L':
                    case 'l': {
                        load();
                        createNewGame();
                        break;
                    }
                    case 'Q':
                    case 'q': {
                        System.exit(0);
                    }
                    default:
                        break;
                }
            }
        }
    }

    private void createNewGame() {
        ter.renderFrame(world);
        if (StdDraw.hasNextKeyTyped()) {
            switch (StdDraw.nextKeyTyped()) {
                avatarControls();
                case 'Q':
                case 'q': {
                    save();
                    System.exit(0);
                }
                default:
                    break;
            }
        }
    }

    private void avatarControls() {
        ter.renderFrame(world);
        if (StdDraw.hasNextKeyTyped()) {
            switch (StdDraw.nextKeyTyped()) {
                case 'W':
                case 'w': {
                    if (world[position.x][position.y + 1].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[position.x][position.y] = Tileset.FLOOR;
                        position.y += 1;
                        ter.renderFrame(world);
                    }
                }
                case 'S':
                case 's': {
                    if (world[position.x][position.y - 1].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[position.x][position.y] = Tileset.FLOOR;
                        world[position.x][position.y - 2] = Tileset.AVATAR;
                        position.y -= 1;
                        ter.renderFrame(world);
                    }
                }
                case 'D':
                case 'd': {
                    if (world[position.x + 1][position.y].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[position.x][position.y] = Tileset.FLOOR;
                        world[position.x + 1][position.y] = Tileset.AVATAR;
                        position.x += 1;
                        ter.renderFrame(world);
                    }
                }
                case 'A':
                case 'a': {
                    if (world[position.x - 1][position.y].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[position.x][position.y] = Tileset.FLOOR;
                        world[position.x - 1][position.y] = Tileset.AVATAR;
                        position.x -= 1;
                        ter.renderFrame(world);
                    }
                }
                default:
                    break;
            }
        }
    }

    private void returnSeed() {
        StringBuilder stg = new StringBuilder();
        if (StdDraw.hasNextKeyTyped()) {
            String input;
            if (StdDraw.nextKeyTyped() == input.toUpperCase('S')) {
                seed = Long.valueOf(stg.toString());
                random = new Random(seed);
            }
            displayGame();
        }
    }

    private void displayGame() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Times New Roman", Font.BOLD, WIDTH));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Times New Roman", Font.BOLD, WIDTH / 2));
        StdDraw.text(WIDTH / 3, HEIGHT / 3, "Please enter a seed.");
        StdDraw.show();
    }

    private void save() {
        File file = new File("savedWorld.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(position);
            objectStream.writeObject(seed);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void load() {
        File file = new File("savedWorld.txt");
        String inputs = null;
        if (!file.exists()) {
            try {
                FileOutputStream fileStream = new FileOutputStream(file);
                ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
                objectStream.writeObject(position);
                objectStream.writeObject(seed);
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
        interactWithInputString(inputs);
    }

    private void addMusic() {
        File file = new File("/Users/aarushiwalia/Downloads/cello.wav");
        try {
            AudioInputStream myMusic = AudioSystem.getAudioInputStream(file);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
        try {
            AudioFormat musicFormatted = myMusic.getFormat();
        } catch (Exception e){
            System.out.println(e);
            return;
        }
        DataLine.info information = new DataLine.Info(SourceDataLine.class, musicFormatted);
    }
//    private void startNewGame() {
//        StdDraw.setPenColor(Color.WHITE);
//        StdDraw.text(WIDTH, HEIGHT, "Enter a seed.");
//        StdDraw.show();
//
//    }
}

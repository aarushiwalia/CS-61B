package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.*;

import java.util.*;
//import java.io.*;

public class World {
    TERenderer ter = new TERenderer();
    TETile[][] myWorld;
    private int width;
    private int height;
    private long seed;
    private Random random;
    private Position current;
    private static final String north = "N";
    private static final String south = "S";
    private static final String east = "E";
    private static final String west = "W";

    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
//        current = new Position(x, y);
//        ter.initialize(width, height);
//        myWorld = new TETile[width][height];
        random = new Random(seed);
    }

    private void init(TETile[][] mmyWorld) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                myWorld[x][y] = Tileset.NOTHING;
            }
        }
    }

    private TETile[][] returnWorld() {
        return myWorld;
    }

    public TETile[][] generateMyWorld() {
        ter.initialize(width, height);
        myWorld = new TETile[width][height];
        init(myWorld);
        int x = Math.max(width, height);

        for (int i = 0; i < x; i++) {
            Position s = new Position(RandomUtils.uniform(random, width), RandomUtils.uniform(random, height));
            //createRoom();
        }

        for (int i = 0; i < 2*x; i++) {
            Position s = new Position(RandomUtils.uniform(random, width), RandomUtils.uniform(random, height));
            createHallWay(s, myWorld, random);
        }
        ter.renderFrame(myWorld);
        return myWorld;
    }

    private boolean canRoomBeCreated(int width, int height, Position current, TETile[][] myWorld) {
        if (width + current.x >= width || height + current.y >= height) {
            return false;
        }
        for (int x = current.x; x < width + current.x; x++) {
            for (int y = current.y; y < height + current.y; y++) {
                if (myWorld[x][y].equals(Tileset.WALL)
                        || myWorld[x][y].equals(Tileset.FLOOR)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createRoom(Position current, Random random, TETile[][] myWorld, Position bottomLeft, Position upperRight) {
//        int width = RandomUtils.uniform(random, 10, 10);
//        int height = RandomUtils.uniform(random, 10, 10);
//
//        if (canRoomBeCreated(width, height, current, theWorld)) {
//            for (int x = current.x; x < width + current.x; x++) {
//                theWorld[x][current.y] = Tileset.WALL;
//                theWorld[x][width - 1] = Tileset.WALL;
//            }
//            for (int y = current.y; y < height + current.y; y++) {
//                theWorld[current.x][y] = Tileset.WALL;
//                theWorld[height - 1][y] = Tileset.WALL;
//            }
//        }
        for (int x = bottomLeft.x; x <= upperRight.x; x++) {
            for (int y = bottomLeft.y; y <= upperRight.y; y++) {
                if (x == bottomLeft.x || x == upperRight.x
                        || y == bottomLeft.y || y == upperRight.y) {
                    myWorld[x][y] = Tileset.WALL;
                } else {
                    myWorld[x][y] = Tileset.FLOOR;
                }
            }
        }

        for (int x = current.x + 1; x < current.x + width - 1; x++) {
            for (int y = current.y + 1; y < current.y + height - 1; y++) {
                myWorld[x][y] = Tileset.FLOOR;
            }
        }
    }

    private void enter(Position enter) {
        myWorld[enter.x][enter.y] = Tileset.FLOOR;
    }

    private void exit(Position exit) {
        myWorld[exit.x][exit.y] = Tileset.FLOOR;
    }

    private boolean canHallwayBeCreated(Position current, TETile[][] theWorld) {
        if (theWorld[current.x+1][current.y].equals(Tileset.FLOOR) || (theWorld[current.x-1][current.y].equals(Tileset.FLOOR)
                || (theWorld[current.x][current.y+1].equals(Tileset.FLOOR)
                || (theWorld[current.x][current.y-1].equals(Tileset.FLOOR))))) {
            return true;
        }
        return false;
    }

    private void createHallWay(Position current, TETile[][] theWorld, Random random) {
        if (canHallwayBeCreated(current, theWorld)) {
            while(!theWorld[current.x][current.y].equals(Tileset.WALL)) {
                int newWidth = RandomUtils.uniform(random, width);
                int newHeight = RandomUtils.uniform(random, height);
                current = new Position(newWidth, newHeight);
            }
        }
    }

    public boolean hallAndRoomConnected(int w, int h) {
        return myWorld[w][h] == Tileset.FLOOR;
    }

    public Position generatePosition(int x) {
        int newWidth = random.nextInt(width - x);
        int newHeight = random.nextInt(height - x);
        return new Position(newWidth, newHeight);
    }

//    public void createMultipleHallways (int x, int y) {
//        for (int i = 0; i < x; i++) {
//            Position current = generatePosition(5);
//            createHallWay(current, );
//        }
//    }

//    private boolean validPosition(int newWidth, int newHeight, int x, TETile[][] myWorld) {
//        int i = 0;
//        for (int
//

    public void createWall(TETile[][] myWorld) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (myWorld[x][y] == Tileset.NOTHING) {
                    myWorld[x][y] = Tileset.WALL;
                }
            }
        }
    }

    public void createSpace (Position current, TETile tile) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (myWorld[x + current.x][y + current.y] == Tileset.NOTHING) {
                    myWorld[x + current.x][y + current.y] = tile;
                }
            }
        }
    }

    private void removeWall(TETile[][] myWorld) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

            }
        }
    }

    public void connectRooms(ArrayList<Room> rooms, TETile[][] myWorld) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room one = rooms.get(i);
            Room two = rooms.get(i+1);
            int newWidth1 = random.nextInt(one.width);
            int newHeight1 = random.nextInt(one.height);
            Position onePos = new Position(newWidth1 + one.current.x, newHeight1 + one.current.y);
            int newWidth2 = random.nextInt(two.width);
            int newHeight2 = random.nextInt(two.height);
            Position twoPos = new Position(newWidth2 + two.current.x, newHeight2 + two.current.y);
        }
    }

    private Position northPos(Position curr, int newWidth, int newHeight) {
        int currX = curr.x;
        int currY = curr.y;
        int bottomLeftX = curr.x-random.nextInt(width) - 1;
        int bottomLeftY = curr.y;
        int upperRightX = bottomLeftX + newWidth + 1;
        int upperRightY = bottomLeftY + newHeight + 1;
        Position bottomLeft = new Position(bottomLeftX, bottomLeftY);
        Position upperRight = new Position(upperRightX, upperRightY);

        if (!canRoomBeCreated(newWidth, newHeight, curr, myWorld)) {
            return null;
        } else {
            return new Position(bottomLeft, upperRight);
        }
    }


}


package byow.Core;
import java.util.*;

public class Room {
    int width;
    int height;
    Position current;

    public Room(int width, int height, Position current) {
        this.width = width;
        this.height = height;
        this.current = current;
    }

    public static Room createRoom(int WIDTH, int HEIGHT, Random random) {
        int roomWidth = random.nextInt(WIDTH) + 2;
        int roomHeight = random.nextInt(HEIGHT) + 2;
        int x = random.nextInt(WIDTH - roomWidth);
        int y = random.nextInt(HEIGHT - roomHeight);
        Position updated = new Position(x, y);
        Room currentRoom = new Room(roomWidth, roomHeight, updated);
        return currentRoom;
    }
}

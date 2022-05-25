package byow.Core;
import java.util.*;

public class Hallway {
    int length;
    Position current;
    boolean isHorizontal;

    public Hallway(int length, Position current, boolean isHorizontal) {
        this.length = length;
        this.current = current;
        this.isHorizontal = isHorizontal;
    }

    public static List<Hallway> createHallWays(Room firstRoom, Room secondRoom, Random random) {
        int room1X = random.nextInt(firstRoom.width - 3) + firstRoom.current.x + 1;
        int room1Y = random.nextInt(firstRoom.height - 3) + firstRoom.current.y + 1;
        int room2X = random.nextInt(secondRoom.width - 3) + secondRoom.current.x + 1;
        int room2Y = random.nextInt(secondRoom.width - 3) + secondRoom.current.x + 1;
        Position room1XPosition = new Position(room1X, room1Y);
        int hallX = room2X - room1X;
        Position room1YPosition = new Position(room1X + hallX, room1Y);
        int hallY = room2Y - room1Y;
        Hallway vertical = new Hallway(hallY, room1YPosition, false);
        Hallway horizontal = new Hallway(hallX,room1XPosition, true);
        ArrayList<Hallway> hallwaysList = new ArrayList<>();
        if (vertical.length != 0) {
            hallwaysList.add(vertical);
        }
        if (horizontal.length != 0) {
            hallwaysList.add(horizontal);
        }
        return hallwaysList;
    }
}

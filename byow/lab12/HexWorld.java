package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final TETile[] types = new TETile[]{Tileset.AVATAR, Tileset.FLOWER,
        Tileset.WALL, Tileset.FLOOR, Tileset.GRASS, Tileset.LOCKED_DOOR, Tileset.MOUNTAIN,
        Tileset.SAND, Tileset.TREE, Tileset.UNLOCKED_DOOR, Tileset.WATER};

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        public int x;
        public int y;

        public Position(int xx, int yy) {
            x = xx;
            y = yy;
        }

    }

    //adds hexagon to x, y point of the world
    public static void addHexagon(int s, Position p, TETile[][] t, TETile type) {
        if (s < 2) {
            throw new IllegalArgumentException("Hex must be atleast size 2!");
        }
        int max = maxWidth(s);
        int hexHeight = max - 1;

        for (int yi = 0; yi < 2 * s; yi += 1) {
            int rowY = p.y + yi;
            int rowXStart = p.x + hexOffset(s, yi);
            Position pp = new Position(rowXStart, rowY);
            int rowWidth = hexRowWidth(s, yi);

            addR(t, pp, rowWidth, type);
        }

    }

    private static int hexOffset(int s, int y) {
        int eff = y;
        if (y >= s) {
            eff = 2 * s - 1 - eff;
        }
        return -eff;
    }
    /*



        for (int i = 0; i < hexHeight; i++) {
            for (int j = 0; j < hexRowWidth(s, i); j++) {

                t[y - hexHeight / 2 + i][x - hexRowWidth(s, i) / 2 + j] = Tileset.FLOWER;
            }
        }


        for (int r = 0; r < t.length; r++) {
            for (int c = 0; c < t[0].length; c++) {
                if (t[r][c] == null) {
                    System.out.print("(" + r + ", " + c + ")  ");
                    t[r][c] = Tileset.NOTHING;
                }

                if (r == x && c == y) {
                    t[r][c] = Tileset.WALL;
                }
            }
            System.out.println();
        }

    }
    */

    private static void addR(TETile[][] t, Position p, int width, TETile type) {
        for (int xi = 0; xi < width; xi += 1) {
            int x = p.x + xi;
            int y = p.y;
            t[x][y] = TETile.colorVariant(type, 32, 32, 32, RANDOM);
        }
    }

    private static int maxWidth(int s) {
        return 2 * s + (s - 2);
    }

    //i = 0, bottom of hexagon
    private static int hexRowWidth(int s, int i) {
        int eff = i;
        if (i >= s) {
            eff = 2 * s - 1 - eff;
        }

        return s + 2 * eff;
    }

    public static void addNumHexes(int s, int num) {


    }

    public static Position topRightNeb(Position p, int size) {
        //size = 2 * s - 1 to the right, s up
        return new Position(p.x + 2 * size - 1, p.y + size);
    }

    public static Position topLeftNeb(Position p, int size) {
        return new Position(p.x - (2 * size - 1), p.y + size);
    }

    public static void addNineteen(int s, TETile[][] t) {
        int numLeft = 19;
        Position now = new Position(25, 0);
        int numChildren = 3;

        for (int i = 0; i < 19; i+= (numChildren - 1) * 2 + 1) {
            if (numLeft <= 2 * numChildren) {
                System.out.println("wot");
                numChildren--;
            }
            addLchild(now, numChildren, s, t);
            addRchild(now, numChildren, s, t);
            now = nextNow(now, s);
            numLeft -= (numChildren - 1) * 2 + 1;
            System.out.println("child " + numChildren + "  numLeft " + numLeft);
        }

    }

    public static Position nextNow(Position now, int s) {
        return new Position(now.x, now.y + 2 * s);
    }

    public static void addLchild(Position now, int i, int s, TETile[][] t) {
        if (i == 0) {
            return;
        }
        addHexagon(s, now, t, types[Math.floorMod(RANDOM.nextInt(), types.length)]);
        now = topLeftNeb(now, s);
        addLchild(now, i - 1, s, t);
    }

    public static void addRchild(Position now, int i, int s, TETile[][] t) {
        if (i == 0) {
            return;
        }
        addHexagon(s, now, t, types[Math.floorMod(RANDOM.nextInt(), types.length)]);
        now = topRightNeb(now, s);
        addRchild(now, i - 1, s, t);
    }



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        for (int r = 0; r < hexTiles.length; r++) {
            for (int c = 0; c < hexTiles[r].length; c++) {
                hexTiles[r][c] = Tileset.NOTHING;
            }
        }


        Position start = new Position(25, 0);
        addHexagon(3, start, hexTiles, Tileset.FLOOR);
        //hexTiles[25][25] = Tileset.FLOWER;
        addNineteen(3, hexTiles);
        ter.renderFrame(hexTiles);
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;


// Changing these variables may causes some tests to fail

interface Global {

  int BOARD_SIZE = 16; // How many cells on the width/length
  int CELL_SIZE = 24; // Actual width and height of each cell
  int NUM_COLORS = 6; // Number of colors that the board contains.

  int width = BOARD_SIZE * CELL_SIZE;
  int bottomMargin = 100;
  int height = BOARD_SIZE * CELL_SIZE + bottomMargin;

  int fontSize = 20;
  Color fontColor = Color.BLACK;
  int tickRate = 35; // Ticks per second

  int MAX_STEPS = BOARD_SIZE * 2 - 1;

  ArrayList<Color> colors = new ArrayList<Color>(
      Arrays.asList(Color.BLUE, Color.RED, Color.PINK, Color.GREEN, Color.YELLOW, Color.CYAN));

}

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  // Cells to flood
  ArrayList<Cell> cellsToFlood;
  // either "static" or "flooding"
  String worldState;

  int currentTick;
  String time;

  Random rand;

  Color floodingColor;

  int currentMoves = 0;

  // TESTING CONSTRUCTOR: DOES NOT initalize the board or connect cells
  FloodItWorld(Random rand, String s) {
    this.currentTick = 0;
    this.time = "";
    this.worldState = "static";
    this.board = new ArrayList<ArrayList<Cell>>();
    this.cellsToFlood = new ArrayList<Cell>();
    this.rand = rand;
    this.currentMoves = 0;
  }

  FloodItWorld(Random rand) {
    this.currentTick = 0;
    this.time = "";
    this.worldState = "static";
    this.board = new ArrayList<ArrayList<Cell>>();
    this.cellsToFlood = new ArrayList<Cell>();
    this.rand = rand;
    this.currentMoves = 0;
    initBoard();
    linkCells();
  }

  FloodItWorld() {
    this(new Random());
  }

  // Creates all cells in the board with no links
  public void initBoard() {
    int curX = 0;
    int curY = 0;
    for (int i = 0; i < Global.BOARD_SIZE; i++) {
      curX = 0;
      board.add(new ArrayList<Cell>());
      for (int j = 0; j < Global.BOARD_SIZE; j++) {
        Color c = Global.colors.get(rand.nextInt(Global.colors.size())); 

        this.board.get(i).add(new Cell(curX, curY, c, null, null, null, null));
        curX += Global.CELL_SIZE;
      }
      curY += Global.CELL_SIZE;
    }
  }

  // Links the cells to each other
  public void linkCells() {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        if (i == 0) {
          this.board.get(i).get(j).bottom = this.board.get(i + 1).get(j);

        }
        else if (i == this.board.size() - 1) {
          this.board.get(i).get(j).top = this.board.get(i - 1).get(j);

        }
        else {
          this.board.get(i).get(j).top = this.board.get(i - 1).get(j);
          this.board.get(i).get(j).bottom = this.board.get(i + 1).get(j);

        }

        if (j == 0) {
          this.board.get(i).get(j).right = this.board.get(i).get(j + 1);

        }
        else if (j == this.board.size() - 1) {
          this.board.get(i).get(j).left = this.board.get(i).get(j - 1);

        }
        else {
          this.board.get(i).get(j).left = this.board.get(i).get(j - 1);
          this.board.get(i).get(j).right = this.board.get(i).get(j + 1);

        }

      }
    }
  }

  @Override
  // Draws the scenes
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(Global.width, Global.height);
    drawCells(scene);
    scene.placeImageXY(new TextImage(this.currentMoves + "/" + Global.MAX_STEPS, Global.fontSize,
        Global.fontColor), Global.width / 3, Global.height - (Global.bottomMargin * 3 / 4));
    scene.placeImageXY(new TextImage(this.tickToString(), Global.fontSize, Global.fontColor),
        Global.width * 2 / 3, Global.height - (Global.bottomMargin * 3 / 4));
    scene.placeImageXY(new TextImage("[r]eset", Global.fontSize, Global.fontColor),
        Global.width / 3, Global.height - (Global.bottomMargin / 4));

    return scene;
  }

  // Checks it the game should be over
  public WorldEnd worldEnds() {
    if (this.currentMoves > Global.MAX_STEPS || allFlooded()) {
      return new WorldEnd(true, this.makeEndScene());
    }
    return new WorldEnd(false, this.makeEndScene());
  }

  // Creates the game end scene
  public WorldScene makeEndScene() {
    WorldScene scene = new WorldScene(Global.width, Global.height);
    if (allFlooded()) {
      scene.placeImageXY(new TextImage("You Won!", Global.fontSize, Global.fontColor),
          Global.width / 2, Global.height / 2);
      scene.placeImageXY(new TextImage("You finished in " + this.tickToString(), Global.fontSize,
          Global.fontColor), Global.width / 2, Global.height / 2 + Global.fontSize * 2);
    }
    else {
      scene.placeImageXY(new TextImage("You Lost!", Global.fontSize, Global.fontColor),
          Global.width / 2, Global.height / 2);
      scene.placeImageXY(
          new TextImage("Time elapsed " + this.tickToString(), Global.fontSize, Global.fontColor),
          Global.width / 2, Global.height / 2 + Global.fontSize * 2);
    }
    return scene;
  }

  // Draws the squares on the scene
  public void drawCells(WorldScene scene) {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {

        Cell cur = this.board.get(i).get(j);
        scene.placeImageXY(
            new RectangleImage(Global.CELL_SIZE, Global.CELL_SIZE, OutlineMode.SOLID, cur.color),
            cur.x + Global.CELL_SIZE / 2, cur.y + Global.CELL_SIZE / 2);
      }
    }
  }

  // Handles ontick methods
  public void onTick() {
    if (this.worldState.equals("flooding")) {

      this.floodAdjacent();

      if (this.cellsToFlood.size() == 0) {
        this.worldState = "static";
        this.resetFloodedBool();
      }
      this.currentTick++;
    }
    else if (this.worldState.equals("static")) {

      this.currentTick++;
    }
    else {
      throw new RuntimeException("World state not recognized");
    }
  }

  // Starts the floodfilling (changes the state to flooding)
  public void startFloodFill(Color c) {
    this.floodingColor = c;
    this.cellsToFlood.add(this.board.get(0).get(0));
    this.worldState = "flooding";
  }

  // Only floods the adjacent squares to any already flooded squares
  public void floodAdjacent() {
    ArrayList<Cell> temp = new ArrayList<Cell>();
    for (int i = 0; i < this.cellsToFlood.size(); i++) {
      this.cellsToFlood.get(i).flood(this.floodingColor, temp);

    }

    this.cellsToFlood = new ArrayList<Cell>(temp);

  }

  // Checks if all the square are flooded.
  public boolean allFlooded() {
    Color c = this.board.get(0).get(0).color;

    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        if (!board.get(i).get(j).color.equals(c)) {
          return false;
        }
      }
    }
    return true;
  }

  // Makes all the cells not flooded anymore
  public void resetFloodedBool() {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.board.get(i).get(j).flooded = false;
      }
    }
  }

  // Handles the mouseClicked event
  public void onMouseClicked(Posn pos) {
    if (this.worldState.equals("static")) {
      Posn boardLocation = gridLocation(pos);

      if (boardLocation.x >= 0 && boardLocation.y >= 0) {
        Cell clickedCell = this.board.get(gridLocation(pos).x).get(gridLocation(pos).y);
        // System.out.println(clickedCell.color);
        if (!clickedCell.color.equals(this.board.get(0).get(0).color)) {
          this.currentMoves++;
          startFloodFill(clickedCell.color);
        }
      }
    }

  }

  // Handles when the player presses a key
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.reset();
    }
  }

  // Resets the game
  public void reset() {
    this.currentTick = 0;
    this.worldState = "static";
    this.currentMoves = 0;
    this.board = new ArrayList<ArrayList<Cell>>();
    this.cellsToFlood = new ArrayList<Cell>();
    this.initBoard();
    this.linkCells();

  }

  // Returns a Posn containing the BOARD POSITION (not coordinates) of
  // where the mouse was clicked. Returns (-1, -1) if not mouse was not in the
  // grid
  public Posn gridLocation(Posn pos) {
    int mouseX = pos.x;
    int mouseY = pos.y;

    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        Cell cur = this.board.get(i).get(j);
        if (mouseX >= cur.x && mouseX <= cur.x + Global.CELL_SIZE && mouseY >= cur.y
            && mouseY <= cur.y + Global.CELL_SIZE) {
          return new Posn(i, j);
        }
      }
    }

    return new Posn(-1, -1);
  }

  // Converts the current tick to a string that represents the time.
  public String tickToString() {
    int totalTimeInSeconds = (this.currentTick / Global.tickRate);
    int seconds = (totalTimeInSeconds) % 60;
    int minutes = (totalTimeInSeconds / 60) % 60;
    int hours = (totalTimeInSeconds / 3600) % 24;

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    // return minutes + ":" + seconds + ":" + miliseconds;
  }

}

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int x, int y, Color color, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // Floods the cell and adds its neighbors to the toBeFlooded list
  void flood(Color c, ArrayList<Cell> temp) {

    if (this.right != null && this.right.color.equals(this.color) && !this.right.flooded) {

      temp.add(this.right);
    }
    if (this.bottom != null && this.bottom.color.equals(this.color) && !this.bottom.flooded) {
      temp.add(this.bottom);
    }
    if (this.left != null && this.left.color.equals(this.color) && !this.left.flooded) {
      temp.add(this.left);
    }
    if (this.top != null && this.top.color.equals(this.color) && !this.top.flooded) {
      temp.add(this.top);
    }

    this.flooded = true;
    this.color = c;
  }
}

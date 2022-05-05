import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;

interface Global {
  // Number of cells on height and width
  int boardWidth = 3;
  int boardHeight = 3;

  int cellLength = 50;

  int width = boardWidth * cellLength;
  int height = boardHeight * cellLength;

  // How thick the walls should be
  int wallThickness = 2;

  Color wallColor = Color.BLACK;
  Color cellColor = Color.LIGHT_GRAY;
  Color searchColor = Color.CYAN;
  Color backtrackColor = Color.GREEN;

  double tickRate = 500.0;
}

class Maze extends World {
  Random rand;

  ArrayList<ArrayList<Cell>> board;
  HashMap<Cell, Cell> reps; // Represenatives
  List<Edge> edgesInTree;
  List<Edge> workList;

  // Either "solve", "backtrack", "final", or "none"
  String worldState;

  IDeque todoDeque;
  ArrayList<Cell> visited;
  Cell target;
  HashMap<Cell, Cell> pathMap;
  ArrayList<Cell> finalPath;

  Maze(Random rand) {
    this.rand = rand;
    this.board = new ArrayList<ArrayList<Cell>>();
    this.reps = new HashMap<Cell, Cell>();
    this.edgesInTree = new ArrayList<Edge>();
    this.workList = new ArrayList<Edge>();
    this.worldState = "none";

    this.initBoard();
    this.createEdges();
    this.kruskal();
    this.updateCellEdges();

    this.target = this.board.get(this.board.size() - 1).get(this.board.get(0).size() - 1);
    this.pathMap = new HashMap<Cell, Cell>();
    this.finalPath = new ArrayList<Cell>();

    /*
     * for (int i = 0; i < board.size(); i++) { for (int j = 0; j <
     * board.get(i).size(); j++) { Cell c = board.get(i).get(j);
     * System.out.print("(" + (c.x / Global.cellLength) + "," + (c.y /
     * Global.cellLength) + ")"); for (int k = 0; k < c.edges.size(); k++) {
     * System.out.print((c.edges.get(k).c1.x / Global.cellLength) + "," +
     * (c.edges.get(k).c1.y / Global.cellLength) + "|");
     * System.out.print((c.edges.get(k).c2.x / Global.cellLength) + "," +
     * (c.edges.get(k).c2.y / Global.cellLength) + " "); }
     * 
     * System.out.println();
     * 
     * } }
     */

  }

  // Constructor for testing purposes
  Maze(Random rand, int test) {
    this.rand = rand;
    this.board = new ArrayList<ArrayList<Cell>>();
    this.reps = new HashMap<Cell, Cell>();
    this.edgesInTree = new ArrayList<Edge>();
    this.workList = new ArrayList<Edge>();

    if (test >= 1) {
      this.initBoard();

    }
    if (test >= 2) {
      this.createEdges();

    }
    if (test >= 3) {
      this.kruskal();
    }
    if (test >= 4) {
      this.updateCellEdges();
    }
  }

  Maze() {
    this(new Random());
  }

  // Initializes the board of cells. No edges/connections yet
  void initBoard() {
    for (int i = 0; i < Global.boardHeight; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < Global.boardWidth; j++) {
        Cell c = new Cell(j * Global.cellLength, i * Global.cellLength, new ArrayList<Edge>());
        row.add(c);
      }

      this.board.add(row);
    }
  }

  // Creates all the inital edges to the worklist. Only creates the right and down
  // edge to avoid
  // duplicates.
  // (If cells 1 and 2 are connected, there will only be one edge for them)
  void createEdges() {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        Cell c = this.board.get(i).get(j);

        // Edge down
        if (i < this.board.size() - 1) {
          int weight = this.rand.nextInt();

          Edge e = new Edge(c, this.board.get(i + 1).get(j), weight);
          this.workList.add(e);
        }

        // Edge right
        if (j < this.board.get(i).size() - 1) {
          int weight = this.rand.nextInt();

          Edge e = new Edge(c, this.board.get(i).get(j + 1), weight);
          this.workList.add(e);
        }
      }
    }

    // Sorts the worklist, so the order of edges becomes randomized
    for (int i = 0; i < this.workList.size() - 1; i++) {
      for (int j = 0; j < this.workList.size() - i - 1; j++) {
        if (this.workList.get(j).weight > this.workList.get(j + 1).weight) {
          Collections.swap(this.workList, j, j + 1);
        }
      }
    }

  }

  // Performs kruskals algorithm to generate the maze with the min-spanning tree
  // with union-find.
  void kruskal() {

    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.reps.put(this.board.get(i).get(j), this.board.get(i).get(j));
      }
    }

    while (this.edgesInTree.size() < this.reps.size() - 1) {

      Edge e = this.workList.get(0);
      if (this.find(reps, e.c1).equals(this.find(reps, e.c2))) {
        this.workList.remove(0);
      }
      else {
        this.edgesInTree.add(e);

        this.reps.put(this.find(reps, e.c2), this.find(reps, e.c1));
        this.workList.remove(0);

      }

    }

  }

  // Follows the links back to the parent cell
  Cell find(HashMap<Cell, Cell> reps, Cell c) {
    if (reps.get(c).equals(c)) {
      return reps.get(c);
    }
    else {
      return find(reps, reps.get(c));
    }
  }

  // Adds the corresponding edges to the edge list of the correct cells.
  // Also updates every cell's left/up/down/right
  void updateCellEdges() {
    for (int i = 0; i < this.edgesInTree.size(); i++) {
      Edge e = this.edgesInTree.get(i);
      e.c1.edges.add(e);
      e.c2.edges.add(e);

      if (e.c1.x == e.c2.x) { // X's are equal, must be up or down
        // Nothing
      }
      else if (e.c1.x > e.c2.x) { // c2 is to left of c1
        e.c1.left = e.c2;
        e.c2.right = e.c1;
      }
      else { // c2 is to the right of c1
        e.c1.right = e.c2;
        e.c2.left = e.c1;
      }

      if (e.c1.y == e.c2.y) { // Y's are equal
        // Nothing
      }
      else if (e.c1.y < e.c2.y) { // C1 is above, c2 is below
        e.c1.down = e.c2;
        e.c2.up = e.c1;
      }
      else { // C2 is above, c1 is below
        e.c1.up = e.c2;
        e.c2.down = e.c1;
      }
    }
  }

  @Override
  // Draws the scene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(Global.width, Global.height);

    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.board.get(i).get(j).drawCell(scene, board);
      }
    }

    return scene;
  }

  // Handles the ontick/maze searching
  public void onTick() {
    if (this.worldState.equals("solve")) {
      if (!this.todoDeque.isEmpty()) {
        this.goNext();
      }
      else {

        this.worldState = "backtrack";
      }
    }
    else if (this.worldState.equals("backtrack")) {

      this.displayFinalPath();

      this.worldState = "final";

    }
  }

  // Displays the final path
  public void displayFinalPath() {
    for (int i = 0; i < this.finalPath.size(); i++) {
      this.finalPath.get(i).c = Global.backtrackColor;
    }
  }

  // Performs the next step of the search
  public void goNext() {
    Cell next = this.todoDeque.pop();

    if (this.visited.contains(next)) {
      // Cell already visited
    }
    else if (next.equals(this.target)) {
      this.todoDeque.removeAll();
      // next.c = Global.backtrackColor;
      this.finalPath.add(this.board.get(this.board.size() - 1).get(this.board.get(0).size() - 1));
      
      /*
      for (Cell c : this.pathMap.keySet()) {
        System.out.println((c.x / Global.cellLength) + " " + (c.y / Global.cellLength) + "|"
            + (this.pathMap.get(c).x / Global.cellLength) + " "
            + (this.pathMap.get(c).y / Global.cellLength));
      }
      */

      this.getPath(next);
    }
    else {

      next.c = Global.searchColor;
      this.visited.add(next);
      for (int i = 0; i < next.edges.size(); i++) {
        if (!next.edges.get(i).c2.equals(next)) {
          if (!this.visited.contains(next.edges.get(i).c2)) {
            this.todoDeque.push(next.edges.get(i).c2);
            this.pathMap.put(next.edges.get(i).c2, next);
          }
        }
        else if (!next.edges.get(i).c1.equals(next)) {
          if (!this.visited.contains(next.edges.get(i).c1)) {
            this.todoDeque.push(next.edges.get(i).c1);
            this.pathMap.put(next.edges.get(i).c1, next);
          }
        }

      }
    }
  }

  // Gets the path after doing the search
  public void getPath(Cell c) {
    if (this.board.get(0).get(0).equals(c)) {
      // Finished
    }
    else {
      this.finalPath.add(this.pathMap.get(c));
      this.getPath(this.pathMap.get(c));
    }
  }

  // Handles when the player presses a key
  public void onKeyEvent(String key) {
    if (key.equals("b") && this.worldState.equals("none")) {
      this.worldState = "solve";
      this.todoDeque = new CellQueue();
      this.todoDeque.push(this.board.get(0).get(0));
      this.visited = new ArrayList<Cell>();
    }

    if (key.equals("d") && this.worldState.equals("none")) {
      this.worldState = "solve";
      this.todoDeque = new CellStack();
      this.todoDeque.push(this.board.get(0).get(0));
      this.visited = new ArrayList<Cell>();
    }

    if (key.equals("r")) {
      this.worldState = "none";
      this.todoDeque = null;
      for (int i = 0; i < this.board.size(); i++) {
        for (int j = 0; j < this.board.get(i).size(); j++) {
          this.board.get(i).get(j).c = Global.cellColor;
        }
      }

      this.pathMap = new HashMap<Cell, Cell>();
      this.finalPath = new ArrayList<Cell>();
    }

  }
}

class Edge {
  // From c1 to c2
  Cell c1;
  Cell c2;
  int weight;

  Edge(Cell c1, Cell c2, int weight) {
    this.c1 = c1;
    this.c2 = c2;
    this.weight = weight;
  }
}

class Cell {
  int x;
  int y;
  ArrayList<Edge> edges;

  Cell up;
  Cell right;
  Cell down;
  Cell left;

  Color c;

  Cell(int x, int y, ArrayList<Edge> edges) {
    this.x = x;
    this.y = y;
    this.edges = edges;

    this.up = null;
    this.right = null;
    this.down = null;
    this.left = null;

    c = Global.cellColor;
  }

  // Equals override
  public boolean equals(Object other) {
    if (!(other instanceof Cell)) {
      return false;
    }

    Cell c = (Cell) other;
    return this.x == c.x && this.y == c.y && this.edges.equals(c.edges);
  }
  
  // Hashcode override
  public int hashCode() {
    return this.x * 10000 + this.y;
  }

  // Draws this cell and it's walls
  void drawCell(WorldScene scene, ArrayList<ArrayList<Cell>> board) {

    scene.placeImageXY(
        new RectangleImage(Global.cellLength, Global.cellLength, OutlineMode.SOLID, c),
        this.x + Global.cellLength / 2, this.y + Global.cellLength / 2);

    if (this.up == null) {
      scene.placeImageXY(new RectangleImage(Global.cellLength, Global.wallThickness,
          OutlineMode.SOLID, Color.BLACK), this.x + Global.cellLength / 2, this.y);
    }
    if (this.down == null) {
      scene.placeImageXY(new RectangleImage(Global.cellLength, Global.wallThickness,
          OutlineMode.SOLID, Color.BLACK), this.x + Global.cellLength / 2,
          this.y + Global.cellLength);
    }
    if (this.left == null) {
      scene.placeImageXY(new RectangleImage(Global.wallThickness, Global.cellLength,
          OutlineMode.SOLID, Color.BLACK), this.x, this.y + Global.cellLength / 2);
    }
    if (this.right == null) {
      scene.placeImageXY(new RectangleImage(Global.wallThickness, Global.cellLength,
          OutlineMode.SOLID, Color.BLACK), this.x + Global.cellLength,
          this.y + Global.cellLength / 2);
    }

  }

}

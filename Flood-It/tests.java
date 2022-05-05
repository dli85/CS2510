import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import tester.Tester;

class ExamplesGame {
  FloodItWorld game;

  // Creates a game but does not set up the board or connect the cells
  void initNoBoard() {
    int seed = 53894 + 14 + 1;
    game = new FloodItWorld(new Random(seed), "TESTING");
  }

  void init() {
    int seed = 53894 + 14 + 1;
    game = new FloodItWorld(new Random(seed));
  }

  // Makes all the cells the same color as the top left cell for testing purposes
  void makeAllFlooded(ArrayList<ArrayList<Cell>> board) {
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        board.get(i).get(j).color = board.get(0).get(0).color;
      }
    }
  }

  boolean testBigBang(Tester t) {
    game = new FloodItWorld();

    game.bigBang(Global.width, Global.height, 1.0 / Global.tickRate);

    return false;
  }

  // Test that the board is created
  boolean testInitBoard(Tester t) {
    initNoBoard();
    boolean init = t.checkExpect(game.board, new ArrayList<ArrayList<Cell>>());

    game.initBoard();

    boolean finalConditions = t.checkExpect(game.board.get(0).get(0),
        new Cell(0, 0, Color.YELLOW, null, null, null, null))
        && t.checkExpect(game.board.get(game.board.size() - 1).get(game.board.size() - 1),
            new Cell(360, 360, Color.BLUE, null, null, null, null));

    return init && finalConditions;
  }

  boolean testLinkCells(Tester t) {
    initNoBoard();
    game.initBoard();
    boolean init = t.checkExpect(game.board.get(0).get(0).left, null)
        && t.checkExpect(game.board.get(0).get(0).right, null)
        && t.checkExpect(game.board.get(0).get(0).top, null)
        && t.checkExpect(game.board.get(0).get(0).bottom, null);
    game.linkCells();
    boolean finalConditions = t.checkExpect(game.board.get(0).get(0).left, null)
        && t.checkExpect(game.board.get(0).get(0).right, game.board.get(0).get(1))
        && t.checkExpect(game.board.get(0).get(0).top, null)
        && t.checkExpect(game.board.get(0).get(0).bottom, game.board.get(1).get(0))
        && t.checkExpect(game.board.get(0).get(1).left, game.board.get(0).get(0))
        && t.checkExpect(game.board.get(1).get(0).top, game.board.get(0).get(0));

    return init && finalConditions;
  }

  boolean testMakeScene(Tester t) {
    init();
    WorldScene result = new WorldScene(Global.width, Global.height);

    game.drawCells(result);
    result.placeImageXY(new TextImage("0/31", 20, Color.BLACK), Global.width / 3,
        Global.height - (Global.bottomMargin * 3 / 4));
    result.placeImageXY(new TextImage("00:00:00", 20, Color.BLACK), Global.width * 2 / 3,
        Global.height - (Global.bottomMargin * 3 / 4));
    result.placeImageXY(new TextImage("[r]eset", Global.fontSize, Global.fontColor),
        Global.width / 3, Global.height - (Global.bottomMargin / 4));

    return t.checkExpect(game.makeScene(), result);
  }

  boolean testDrawCells(Tester t) {
    init();

    WorldScene scene = new WorldScene(Global.width, Global.height);
    WorldScene result = new WorldScene(Global.width, Global.height);

    boolean init = t.checkExpect(scene, new WorldScene(Global.width, Global.height))
        && t.checkExpect(result, new WorldScene(Global.width, Global.height));

    game.drawCells(scene);

    for (int i = 0; i < 16; i++) {
      for (int j = 0; j < 16; j++) {

        Cell cur = game.board.get(i).get(j);
        result.placeImageXY(
            new RectangleImage(Global.CELL_SIZE, Global.CELL_SIZE, OutlineMode.SOLID, cur.color),
            cur.x + Global.CELL_SIZE / 2, cur.y + Global.CELL_SIZE / 2);
      }
    }

    return t.checkExpect(scene, result);

  }

  boolean testAllFlooded(Tester t) {
    init();
    boolean init = t.checkExpect(game.allFlooded(), false);

    makeAllFlooded(game.board);
    boolean finalConditions = t.checkExpect(game.allFlooded(), true);

    return init && finalConditions;
  }

  boolean testTickToString(Tester t) {
    init();
    boolean test1 = t.checkExpect(game.tickToString(), "00:00:00");
    game.currentTick = 500;
    boolean test2 = t.checkExpect(game.tickToString(), "00:00:14");

    return test1 && test2;
  }

  void testWorldEnds(Tester t) {
    init();

    t.checkExpect(game.worldEnds(), new WorldEnd(false, game.makeEndScene()));

    // Set all the colors to the same
    for (int i = 0; i < game.board.size(); i++) {
      for (int j = 0; j < game.board.get(i).size(); j++) {
        game.board.get(i).get(j).color = Color.BLUE;
      }
    }

    t.checkExpect(game.worldEnds(), new WorldEnd(true, game.makeEndScene()));

    init();

    // Set the moves to the max
    game.currentMoves = Global.MAX_STEPS + 1;

    t.checkExpect(game.worldEnds(), new WorldEnd(true, game.makeEndScene()));
  }

  void testMakeEndScene(Tester t) {
    init();

    for (int i = 0; i < game.board.size(); i++) {
      for (int j = 0; j < game.board.get(i).size(); j++) {
        game.board.get(i).get(j).color = Color.BLUE;
      }
    }

    WorldScene winScene = new WorldScene(Global.width, Global.height);
    winScene.placeImageXY(new TextImage("You Won!", Global.fontSize, Global.fontColor),
        Global.width / 2, Global.height / 2);
    winScene.placeImageXY(
        new TextImage("You finished in 00:00:00", Global.fontSize, Global.fontColor),
        Global.width / 2, Global.height / 2 + Global.fontSize * 2);

    t.checkExpect(game.makeEndScene(), winScene);

    init();

    WorldScene loseScene = new WorldScene(Global.width, Global.height);
    loseScene.placeImageXY(new TextImage("You Lost!", Global.fontSize, Global.fontColor),
        Global.width / 2, Global.height / 2);
    loseScene.placeImageXY(
        new TextImage("Time elapsed 00:00:00", Global.fontSize, Global.fontColor), Global.width / 2,
        Global.height / 2 + Global.fontSize * 2);

    t.checkExpect(game.makeEndScene(), loseScene);
  }

  void testReset(Tester t) {
    init();
    game.currentTick = 500;
    game.currentMoves = 10;
    game.worldState = "flooding";
    game.cellsToFlood.add(game.board.get(0).get(0));
    boolean inital = t.checkExpect(game.currentTick, 500) && t.checkExpect(game.currentMoves, 10)
        && t.checkExpect(game.worldState, "flooding") && t.checkExpect(game.cellsToFlood,
            new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(0))));

    game.reset();

    boolean finalConditions = t.checkExpect(game.currentTick, 0)
        && t.checkExpect(game.currentMoves, 0) && t.checkExpect(game.worldState, "static")
        && t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());
  }

  void testOnKeyEvent(Tester t) {
    init();
    game.currentTick = 500;
    game.currentMoves = 10;
    game.worldState = "flooding";
    game.cellsToFlood.add(game.board.get(0).get(0));
    boolean inital = t.checkExpect(game.currentTick, 500) && t.checkExpect(game.currentMoves, 10)
        && t.checkExpect(game.worldState, "flooding") && t.checkExpect(game.cellsToFlood,
            new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(0))));

    game.onKeyEvent("r");

    boolean finalConditions = t.checkExpect(game.currentTick, 0)
        && t.checkExpect(game.currentMoves, 0) && t.checkExpect(game.worldState, "static")
        && t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());

  }

  void testGridLocation(Tester t) {
    init();
    t.checkExpect(game.gridLocation(new Posn(10, 4)), new Posn(0, 0));
    t.checkExpect(game.gridLocation(new Posn(54, 99)), new Posn(4, 2));
  }

  void testResetFloodedBool(Tester t) {
    init();
    game.board.get(0).get(0).flooded = true;
    game.board.get(0).get(1).flooded = true;
    game.board.get(0).get(2).flooded = true;

    t.checkExpect(game.board.get(0).get(0).flooded, true);
    t.checkExpect(game.board.get(0).get(1).flooded, true);
    t.checkExpect(game.board.get(0).get(2).flooded, true);
    t.checkExpect(game.board.get(1).get(0).flooded, false);
    t.checkExpect(game.board.get(1).get(1).flooded, false);

    game.resetFloodedBool();

    t.checkExpect(game.board.get(0).get(0).flooded, false);
    t.checkExpect(game.board.get(0).get(1).flooded, false);
    t.checkExpect(game.board.get(0).get(2).flooded, false);
    t.checkExpect(game.board.get(1).get(0).flooded, false);
    t.checkExpect(game.board.get(1).get(1).flooded, false);

  }

  // yellow, yellow, Pink
  void testOnMouseClicked(Tester t) {
    init();
    t.checkExpect(game.worldState, "static");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());
    game.onMouseClicked(new Posn(12, 12));
    t.checkExpect(game.worldState, "static");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());

    init();
    t.checkExpect(game.worldState, "static");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());
    game.onMouseClicked(new Posn(50, 12));
    t.checkExpect(game.worldState, "flooding");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(0))));
    t.checkExpect(game.floodingColor, Color.PINK);
  }

  void testStartFloodFill(Tester t) {
    init();
    t.checkExpect(game.worldState, "static");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>());
    game.startFloodFill(Color.PINK);
    t.checkExpect(game.worldState, "flooding");
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(0))));
    t.checkExpect(game.floodingColor, Color.PINK);
  }

  void testFloodAdjacent(Tester t) {
    init();
    game.startFloodFill(Color.PINK);
    t.checkExpect(game.board.get(0).get(0).color, Color.YELLOW);
    t.checkExpect(game.cellsToFlood, new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(0))));

    game.floodAdjacent();
    t.checkExpect(game.board.get(0).get(0).color, Color.PINK);
    t.checkExpect(game.cellsToFlood,
        new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(1), game.board.get(1).get(0))));
  }

  void testOnTick(Tester t) {
    init();
    t.checkExpect(game.currentTick, 0);
    game.onTick();
    t.checkExpect(game.currentTick, 1);

    init();
    game.startFloodFill(Color.PINK);
    game.onTick();
    t.checkExpect(game.board.get(0).get(0).color, Color.PINK);
    t.checkExpect(game.cellsToFlood,
        new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(1), game.board.get(1).get(0))));
    t.checkExpect(game.currentTick, 1);
  }

  void testCellFlood(Tester t) {
    init();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    t.checkExpect(game.board.get(0).get(0).color, Color.YELLOW);
    game.board.get(0).get(0).flood(Color.PINK, temp);

    t.checkExpect(game.board.get(0).get(0).color, Color.PINK);
    t.checkExpect(temp,
        new ArrayList<Cell>(Arrays.asList(game.board.get(0).get(1), game.board.get(1).get(0))));
  }

}
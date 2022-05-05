import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import tester.Tester;

// THESE TESTS WILL ONLY WORK FOR THE CURRENT CONDITIONS
// CHANGNING THEM MAY MAKE THE TESTS FAIL
class ExamplesMaze {
  final int cl = Global.cellLength;

  Maze m;
  int seed = 53894 + 14 + 1;

  IDeque stack;
  IDeque queue;

  void init() {

    m = new Maze(new Random(seed));
    stack = new CellStack();
    queue = new CellQueue();
  }

  boolean testBigBang(Tester t) {
    Maze m = new Maze();
    m.bigBang(Global.width, Global.height, 1.0 / Global.tickRate);
    return true;
  }

  void testInitBoard(Tester t) {
    init();
    m = new Maze(new Random(seed), 0);
    boolean initial = t.checkExpect(m.board, new ArrayList<ArrayList<Cell>>());

    m.initBoard();

    boolean finalConditions = t.checkExpect(m.board.get(0).get(0),
        new Cell(0, 0, new ArrayList<Edge>()))
        && t.checkExpect(m.board.get(0).get(1), new Cell(1 * cl, 0, new ArrayList<Edge>()));
  }

  void testCreateEdges(Tester t) {
    init();
    m = new Maze(new Random(seed), 1);
    boolean inital = t.checkExpect(m.workList, new ArrayList<Edge>());
    m.createEdges();

    boolean finalConditions = t.checkExpect(m.workList.get(0),
        new Edge(new Cell(1 * cl, 0, new ArrayList<Edge>()),
            new Cell(2 * cl, 0, new ArrayList<Edge>()), -2009707588))
        && t.checkExpect(m.workList.get(1),
            new Edge(new Cell(1 * cl, 2 * cl, new ArrayList<Edge>()),
                new Cell(2 * cl, 2 * cl, new ArrayList<Edge>()), -1989085598));
  }

  void testKruskal(Tester t) {
    init();
    m = new Maze(new Random(seed), 2);
    boolean inital = t.checkExpect(m.edgesInTree, new ArrayList<Edge>());
    m.kruskal();

    boolean finalConditions = t.checkExpect(m.edgesInTree.get(0),
        new Edge(new Cell(1 * cl, 0, new ArrayList<Edge>()),
            new Cell(2 * cl, 0, new ArrayList<Edge>()), -2009707588))
        && t.checkExpect(m.edgesInTree.get(1),
            new Edge(new Cell(1 * cl, 2 * cl, new ArrayList<Edge>()),
                new Cell(2 * cl, 2 * cl, new ArrayList<Edge>()), -1989085598))
        && t.checkExpect(m.edgesInTree.get(2), new Edge(new Cell(0, 1 * cl, new ArrayList<Edge>()),
            new Cell(0, 2 * cl, new ArrayList<Edge>()), -1769488787));
  }

  boolean testFind(Tester t) {
    init();
    m = new Maze(new Random(seed), 3);

    return t.checkExpect(m.find(m.reps, m.board.get(0).get(0)), m.board.get(0).get(0))
        && t.checkExpect(m.find(m.reps, m.board.get(1).get(1)), m.board.get(0).get(0));
  }

  void testUpdateCellEdge(Tester t) {
    init();
    m = new Maze(new Random(seed), 3);

    boolean inital = t.checkExpect(m.board.get(0).get(0).edges, new ArrayList<Edge>())
        && t.checkExpect(m.board.get(0).get(0).up, null)
        && t.checkExpect(m.board.get(0).get(0).down, null)
        && t.checkExpect(m.board.get(0).get(0).left, null)
        && t.checkExpect(m.board.get(0).get(0).right, null);

    m.updateCellEdges();

    boolean finalConditions = t.checkExpect(m.board.get(0).get(0).up, null)
        && t.checkExpect(m.board.get(0).get(0).left, null)
        && t.checkExpect(m.board.get(0).get(0).right, m.board.get(0).get(1))
        && t.checkExpect(m.board.get(0).get(0).down, m.board.get(1).get(0));

  }

  boolean testMakeScene(Tester t) {
    init();
    WorldScene result = new WorldScene(Global.width, Global.height);

    for (int i = 0; i < m.board.size(); i++) {
      for (int j = 0; j < this.m.board.get(i).size(); j++) {
        this.m.board.get(i).get(j).drawCell(result, m.board);
      }
    }

    return t.checkExpect(m.makeScene(), result);
  }

  void testdrawCell(Tester t) {
    init();
    WorldScene expected = new WorldScene(Global.width, Global.height);
    WorldScene toRunOn = new WorldScene(Global.width, Global.height);

    boolean inital = t.checkExpect(toRunOn, expected);

    m.board.get(0).get(0).drawCell(toRunOn, m.board);

    expected.placeImageXY(new RectangleImage(Global.cellLength, Global.cellLength,
        OutlineMode.SOLID, Color.LIGHT_GRAY), 0 + Global.cellLength / 2, 0 + Global.cellLength / 2);

    expected.placeImageXY(
        new RectangleImage(Global.cellLength, Global.wallThickness, OutlineMode.SOLID, Color.BLACK),
        0 + Global.cellLength / 2, 0);

    expected.placeImageXY(
        new RectangleImage(Global.wallThickness, Global.cellLength, OutlineMode.SOLID, Color.BLACK),
        0, 0 + Global.cellLength / 2);

    boolean finalConditions = t.checkExpect(expected, toRunOn);

  }

  // Tests all the methods for an IDeque
  void testIDeque(Tester t) {
    init();
    boolean inital = t.checkExpect(stack.isEmpty(), true) && t.checkExpect(queue.isEmpty(), true);
    stack.push(new Cell(0, 0, new ArrayList<Edge>()));
    queue.push(new Cell(0, 0, new ArrayList<Edge>()));
    stack.push(new Cell(1, 1, new ArrayList<Edge>()));
    queue.push(new Cell(1, 1, new ArrayList<Edge>()));
    stack.push(new Cell(2, 2, new ArrayList<Edge>()));
    queue.push(new Cell(2, 2, new ArrayList<Edge>()));

    boolean finalConditions = t.checkExpect(stack.pop(), new Cell(2, 2, new ArrayList<Edge>()))
        && t.checkExpect(queue.pop(), new Cell(0, 0, new ArrayList<Edge>()))
        && t.checkExpect(stack.pop(), new Cell(1, 1, new ArrayList<Edge>()))
        && t.checkExpect(queue.pop(), new Cell(1, 1, new ArrayList<Edge>()))
        && t.checkExpect(stack.pop(), new Cell(0, 0, new ArrayList<Edge>()))
        && t.checkExpect(queue.pop(), new Cell(2, 2, new ArrayList<Edge>()));

    stack.push(new Cell(0, 0, new ArrayList<Edge>()));
    queue.push(new Cell(0, 0, new ArrayList<Edge>()));
    stack.push(new Cell(1, 1, new ArrayList<Edge>()));
    queue.push(new Cell(1, 1, new ArrayList<Edge>()));
    stack.push(new Cell(2, 2, new ArrayList<Edge>()));
    queue.push(new Cell(2, 2, new ArrayList<Edge>()));

    boolean testRemoveAll = t.checkExpect(stack.isEmpty(), false)
        && t.checkExpect(queue.isEmpty(), false);

    stack.removeAll();
    queue.removeAll();

    boolean realFinalTrustMe = t.checkExpect(stack.isEmpty(), true)
        && t.checkExpect(queue.isEmpty(), true);

  }

  void testGetPath(Tester t) {
    init();

    m.pathMap.put(m.board.get(1).get(0), m.board.get(0).get(0));
    m.pathMap.put(m.board.get(0).get(2), m.board.get(0).get(1));
    m.pathMap.put(m.board.get(0).get(1), m.board.get(0).get(0));
    m.pathMap.put(m.board.get(1).get(2), m.board.get(0).get(2));
    m.pathMap.put(m.board.get(2).get(2), m.board.get(1).get(2));

    m.getPath(m.board.get(2).get(2));

    boolean conditions = t.checkExpect(m.finalPath,
        new ArrayList<Cell>(Arrays.asList(m.board.get(1).get(2), m.board.get(0).get(2),
            m.board.get(0).get(1), m.board.get(0).get(0))));
  }

  void testDisplayFinalPath(Tester t) {
    init();

    m.pathMap.put(m.board.get(1).get(0), m.board.get(0).get(0));
    m.pathMap.put(m.board.get(0).get(2), m.board.get(0).get(1));
    m.pathMap.put(m.board.get(0).get(1), m.board.get(0).get(0));
    m.pathMap.put(m.board.get(1).get(2), m.board.get(0).get(2));
    m.pathMap.put(m.board.get(2).get(2), m.board.get(1).get(2));

    m.getPath(m.board.get(2).get(2));

    m.displayFinalPath();

    // The bottom-right cell's color is not set by displayFinalPath

    boolean conditions = t.checkExpect(m.board.get(1).get(2).c, Global.backtrackColor)
        && t.checkExpect(m.board.get(0).get(2).c, Global.backtrackColor)
        && t.checkExpect(m.board.get(0).get(1).c, Global.backtrackColor)
        && t.checkExpect(m.board.get(0).get(0).c, Global.backtrackColor);
  }

  void testOnKeyEvent(Tester t) {
    init();
    boolean inital = t.checkExpect(m.worldState, "none") && t.checkExpect(m.todoDeque, null)
        && t.checkExpect(m.visited, null);

    m.onKeyEvent("b");

    boolean check1 = t.checkExpect(m.worldState, "solve")
        && t.checkExpect(m.visited, new ArrayList<Cell>())
        && t.checkExpect(m.todoDeque.pop(), m.board.get(0).get(0));

    init();
    m.onKeyEvent("d");

    boolean check2 = t.checkExpect(m.worldState, "solve")
        && t.checkExpect(m.visited, new ArrayList<Cell>())
        && t.checkExpect(m.todoDeque.pop(), m.board.get(0).get(0));

    m.onKeyEvent("r");

    boolean check3 = t.checkExpect(m.worldState, "none")
        && t.checkExpect(m.visited, new ArrayList<Cell>()) && t.checkExpect(m.todoDeque, null)
        && t.checkExpect(m.pathMap, new HashMap<Cell, Cell>())
        && t.checkExpect(m.finalPath, new ArrayList<Cell>());
  }

  void testOnTick(Tester t) {
    init();
    m.onKeyEvent("b");
    boolean initial = t.checkExpect(m.todoDeque.isEmpty(), false)
        && t.checkExpect(m.visited, new ArrayList<Cell>());

    m.onTick();

    boolean finalConditions = t.checkExpect(m.visited,
        new ArrayList<Cell>(Arrays.asList(m.board.get(0).get(0))));

    m.todoDeque.removeAll();

    boolean init2 = t.checkExpect(m.worldState, "solve");

    m.onTick();

    boolean final2 = t.checkExpect(m.worldState, "backtrack");

    m.onTick();

    boolean final3 = t.checkExpect(m.worldState, "final");
  }

  void testGoNext(Tester t) {
    init();
    m.onKeyEvent("b");
    boolean initial = t.checkExpect(m.todoDeque.isEmpty(), false)
        && t.checkExpect(m.visited, new ArrayList<Cell>());

    m.goNext();

    boolean finalConditions = t.checkExpect(m.visited,
        new ArrayList<Cell>(Arrays.asList(m.board.get(0).get(0))));

    m.goNext();

    boolean finalConditions2 = t.checkExpect(m.visited,
        new ArrayList<Cell>(Arrays.asList(m.board.get(0).get(0), m.board.get(1).get(0))));
  }
  
  void testHashCodeAndEquals(Tester t) {
    init();
    
    boolean equals = t.checkExpect(m.board.get(0).get(0).equals(m.board.get(0).get(0)), true)
        && t.checkExpect(m.board.get(0).get(0).equals(m.board.get(0).get(1)), false);
    
    boolean hashCode = t.checkExpect(m.board.get(0).get(0).hashCode(), 0)
        && t.checkExpect(m.board.get(0).get(1).hashCode(), 500000);
  }

}
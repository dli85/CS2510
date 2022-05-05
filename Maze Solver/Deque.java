import java.util.ArrayList;

//Abstraction of queue and stack
interface IDeque {

  // Pushes an element on the stack/deque
  void push(Cell c);

  // Pops an element from the stack/deque
  Cell pop();

  // Returns whether the stack/deque is empty
  boolean isEmpty();

  // Removes all the elements from the deque
  void removeAll();
}

// Represents a stack of cells
class CellStack implements IDeque {

  ArrayList<Cell> cells;

  CellStack() {
    this.cells = new ArrayList<Cell>();
  }

  // Pushes a Cell onto the stack
  public void push(Cell c) {
    this.cells.add(0, c);
  }

  // Removes an Cell from the stack
  public Cell pop() {
    return this.cells.remove(0);
  }

  // Checks if the stack is empty
  public boolean isEmpty() {
    return cells.size() == 0;
  }

  // Removes all the cells from the stack
  public void removeAll() {
    this.cells.clear();
  }

}

// Represents a queue of cells
class CellQueue implements IDeque {

  ArrayList<Cell> cells;

  CellQueue() {
    this.cells = new ArrayList<Cell>();
  }

  // Pushes a cell onto the queue
  public void push(Cell c) {
    this.cells.add(0, c);
  }

  // Pops an element from the queue
  public Cell pop() {
    return this.cells.remove(this.cells.size() - 1);
  }

  // Checks if the queue is empty
  public boolean isEmpty() {
    return cells.size() == 0;
  }

  // Removes all the cells from the queue
  public void removeAll() {
    this.cells.clear();
  }
}

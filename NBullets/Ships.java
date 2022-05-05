import javalib.funworld.*;
import java.util.Random;

// Questions to ask: 
// Design of doExplosion (Ship doesn't do anything)
// Angles correct?
// Hitting two at once?

interface ILoShip {
  // Moves all the ships in a list
  ILoShip move();

  // Draws all the ships in a list
  WorldScene draw(WorldScene scene);

  // Removes all ships in a list that are out of bounds
  ILoShip removeOutOfBounds();

  // Creates new ships
  ILoShip generateShips(int numShips, Random rand);

  // Handles events when bullets and ships collide (ships need to disapear)
  ILoShip collisions(ILoBullet lob);

  // Returns how many ships a given bullet is colliding with
  int numberColliding(IGameObject b);

  // Count the total number of ships
  int countShips();
}

class MtLoShip implements ILoShip {
  MtLoShip() {

  }

  // Moves all the ships in the list
  public ILoShip move() {
    return this;
  }

  // Draw all the ships in a scene
  public WorldScene draw(WorldScene scene) {
    return scene;
  }

  // Create new ships on either end of the screen
  public ILoShip generateShips(int numShips, Random rand) {
    if (numShips > 0) {
      int side = rand.nextInt(2);
      int x;
      int xVel;
      if (side == 1) { // Basically flip a coin to see if we should put ship on left or right side.
        x = Global.width;
        xVel = -1 * Global.bulletSpeed / 2;
      }
      else {
        x = 0;
        xVel = Global.bulletSpeed / 2;
      }

      int minY = Global.minShipY;
      int maxY = Global.maxShipY;

      int y = rand.nextInt(maxY - minY + 1) + minY;

      Ship s = new Ship(Global.height / 30, xVel, 0, x, y);

      return new ConsLoShip(s, this.generateShips(numShips - 1, rand));
    }
    else {
      return this;
    }
  }

  // Removes ships that are out of bounds
  public ILoShip removeOutOfBounds() {
    return this;
  }

  // Removes ships that are colliding with bullets
  public ILoShip collisions(ILoBullet lob) {
    return this;
  }

  // Returns the number of ships that are colliding with a bullet
  public int numberColliding(IGameObject b) {
    return 0;
  }

  // Counts all the ships on the screen
  public int countShips() {
    return 0;
  }
}

class ConsLoShip implements ILoShip {
  IGameObject first;
  ILoShip rest;

  ConsLoShip(IGameObject first, ILoShip rest) {
    this.first = first;
    this.rest = rest;
  }

  // Moves all the ships in a list
  public ILoShip move() {
    return new ConsLoShip(this.first.move(), this.rest.move());
  }

  // Draws all the ships on the scene
  public WorldScene draw(WorldScene scene) {
    return this.first.draw(this.rest.draw(scene));
  }

  // Creates new ships
  public ILoShip generateShips(int numShips, Random rand) {
    return new ConsLoShip(this.first, this.rest.generateShips(numShips, rand)); 
  }

  // Removes ships that go out of bounds
  public ILoShip removeOutOfBounds() {
    if (this.first.outOfBounds()) {
      return this.rest.removeOutOfBounds();
    }
    else {
      return new ConsLoShip(this.first, this.rest.removeOutOfBounds());
    }
  }

  // Handles collisions for ships (they dissapear)
  public ILoShip collisions(ILoBullet lob) {
    if (lob.numberColliding(this.first) > 0) {
      return this.rest.collisions(lob); // Remove this ship if it is colliding
    }
    else {
      return new ConsLoShip(this.first, this.rest.collisions(lob));
    }
  }

  // Check if the distance between the centers is less than the sum of the radii
  public int numberColliding(IGameObject bullet) {
    if (this.first.isColliding(bullet)) {
      return 1 + this.rest.numberColliding(bullet);
    }
    return this.rest.numberColliding(bullet);
  }

  // Counts the total number of ships in the list
  public int countShips() {
    return 1 + this.rest.countShips();
  }

}

class Ship extends AGameObject {
  Ship(int radius, int xVelocity, int yVelocity, int x, int y) {
    super(radius, xVelocity, yVelocity, x, y, Global.shipColor);

  }

  // Moves a ship according to its x and y velocity
  public IGameObject move() {
    return new Ship(this.radius, this.xVelocity, 0, this.x + this.xVelocity, this.y);
  }

}

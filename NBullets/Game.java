import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

//TODO: Tests
// and purpose statements

interface Global {

  int width = 500;
  int height = 300;

  int tickRate = 28;

  int minShipY = Global.height / 7;
  int maxShipY = Global.height / 7 * 6;
  int minShipsPerSec = 1;
  int maxShipsPerSec = 3;
  int initBulletRadius = 2;
  int maxBulletRadius = 8;
  int bulletRadiusChange = 2;
  int bulletSpeed = 8;

  int fontSize = 13;

  Color fontColor = Color.BLACK;
  Color shipColor = Color.CYAN;
  Color bulletColor = Color.PINK;
  Color gameOverColor = Color.RED;

}

class MyGame extends World {

  ILoBullet loBullet;
  ILoShip loShip;
  int currentTick;
  int numBullets;
  int shipsDestroyed;

  Random rand;

  MyGame(ILoBullet loBullet, ILoShip loShip, int currentTick, int numBullets, int shipsDestroyed,
      Random rand) {
    this.loBullet = loBullet;
    this.loShip = loShip;
    this.currentTick = currentTick;
    this.numBullets = numBullets;
    this.shipsDestroyed = shipsDestroyed;
    this.rand = rand;
  }

  MyGame(ILoBullet loBullet, ILoShip loShip, int currentTick, int numBullets, int shipsDestroyed) {
    this(loBullet, loShip, currentTick, numBullets, shipsDestroyed, new Random());
  }

  MyGame(int numBullets) {
    this(new MtLoBullet(), new MtLoShip(), 0, numBullets, 0);
  }

  // Ontick method, (calls all the events)
  public MyGame onTick() {
    return this.generateShips().collisions().moveObjects().removeObjects().incrementTick();
  }

  // Creates the scene for the game
  @Override
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(Global.width, Global.height);

    scene = this.drawText(scene);
    scene = this.drawBullets(scene);
    scene = this.drawShips(scene);

    return scene;
  }

  // Moves all the ships and bullets
  public MyGame moveObjects() {
    return new MyGame(this.loBullet.move(), this.loShip.move(), this.currentTick, this.numBullets,
        this.shipsDestroyed, this.rand);
  }

  // Remove game objects if they go out of bounds
  public MyGame removeObjects() {
    return new MyGame(this.loBullet.removeOutOfBounds(), this.loShip.removeOutOfBounds(),
        this.currentTick, this.numBullets, this.shipsDestroyed, this.rand);
  }

  // Draw all the bullets
  public WorldScene drawBullets(WorldScene scene) {
    scene = this.loBullet.draw(scene);
    return scene;
  }

  // Draw all the ships
  public WorldScene drawShips(WorldScene scene) {
    scene = this.loShip.draw(scene);
    return scene;
  }

  // Draw the text on the screen (ships destroyed, bullets left)
  public WorldScene drawText(WorldScene scene) {
    return scene
        .placeImageXY(new TextImage("Ships destroyed: " + this.shipsDestroyed, Global.fontSize,
            Global.fontColor), Global.width / 2, 0 + Global.fontSize)
        .placeImageXY(
            new TextImage("Bullets left: " + this.numBullets, Global.fontSize, Global.fontColor),
            Global.width / 2, 0 + Global.fontSize * 2);
  }

  // Creates ships every second
  public MyGame generateShips() {
    if (this.currentTick % Global.tickRate == 0) {
      return new MyGame(this.loBullet,
          this.loShip
              .generateShips(this.rand.nextInt(Global.maxShipsPerSec - Global.minShipsPerSec + 1)
                  + Global.minShipsPerSec, this.rand),
          this.currentTick, this.numBullets, this.shipsDestroyed, this.rand);
    }
    else {
      return this;
    }
  }

  // Handles the collision events (bullets explode, ships dissapear)
  public MyGame collisions() {

    ILoShip newLoShip = this.loShip.collisions(this.loBullet);

    ILoBullet newLoBullet = this.loBullet.collisions(this.loShip);

    return new MyGame(newLoBullet, newLoShip, this.currentTick, this.numBullets,
        this.shipsDestroyed + this.loShip.countShips() - newLoShip.countShips(), this.rand);
  }

  // Handles the key pressing (space bar to fire)
  public MyGame onKeyEvent(String key) {
    if (key.equals(" ") && this.numBullets > 0) {
      return new MyGame(this.loBullet.shoot(), this.loShip, this.currentTick, this.numBullets - 1,
          this.shipsDestroyed, this.rand);
    }
    else {
      return this;
    }
  }

  // Increases the game tick by one
  public MyGame incrementTick() {
    return new MyGame(this.loBullet, this.loShip, this.currentTick + 1, this.numBullets,
        this.shipsDestroyed, this.rand);
  }

  // Checks if the game is over (no bullets on screen, no bullets left to fire)
  @Override
  public WorldEnd worldEnds() {
    if (this.loBullet.bulletCount() == 0 && this.numBullets == 0) {
      return new WorldEnd(true, this.makeEndScene());
    }
    else {
      return new WorldEnd(false, this.makeEndScene());
    }
  }

  // Creates the end game screen
  public WorldScene makeEndScene() {
    WorldScene scene = new WorldScene(Global.width, Global.height);
    scene = scene
        .placeImageXY(new TextImage("Game Over!", Global.fontSize * 2, Global.gameOverColor),
            Global.width / 2, Global.height / 2)
        .placeImageXY(new TextImage("You destroyed " + this.shipsDestroyed + " ships!",
            Global.fontSize, Global.fontColor), Global.width / 2,
            Global.height / 2 + Global.fontSize * 2);
    return scene;
  }
}

interface IGameObject {
  // Moves this object forward by one tick
  IGameObject move();

  // Checks if this object is out of the screen
  boolean outOfBounds();

  // Draws this game object
  WorldScene draw(WorldScene scene);

  // Checks if this IGameObject is colliding with the given one
  boolean isColliding(IGameObject other);

  // Helper for the IsColliding method
  boolean collidingHelper(int radius, int x, int y);

  // Creates bullets from the explosion
  ILoBullet doExplosion(ILoBullet rest, ILoShip los);
}

abstract class AGameObject implements IGameObject {
  int radius;
  int xVelocity;
  int yVelocity;
  int x;
  int y;
  Color color;

  AGameObject(int radius, int xVelocity, int yVelocity, int x, int y, Color color) {
    this.radius = radius;
    this.xVelocity = xVelocity;
    this.yVelocity = yVelocity;
    this.x = x;
    this.y = y;
    this.color = color;
  }

  // Checks if a IGameObject is out of the screen
  public boolean outOfBounds() {
    return this.x + this.radius < 0 || this.x - this.radius > Global.width
        || this.y + this.radius < 0 || this.y - this.radius > Global.height;
  }

  // Places the this object on a scene.
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new CircleImage(this.radius, OutlineMode.SOLID, this.color), this.x,
        this.y);
  }

  // Moves this object
  public abstract IGameObject move();

  // Checks if this object is colliding with the given object
  public boolean isColliding(IGameObject other) {
    return other.collidingHelper(this.radius, this.x, this.y);
  }

  // Helper method for isColliding
  public boolean collidingHelper(int radius, int x, int y) {
    return Math.hypot(Math.abs(this.x - x), Math.abs(this.y - y)) <= this.radius + radius;
  }

  // Delegation method for when a bullet explodes
  public ILoBullet doExplosion(ILoBullet rest, ILoShip los) {
    return new MtLoBullet();
  }

}

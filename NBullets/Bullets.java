import javalib.funworld.*;

interface ILoBullet {
  // Moves all the bullets in a list of bullets
  ILoBullet move();

  // Draws all the bullets in a list of bullets
  WorldScene draw(WorldScene scene);

  // Removes all bullets in a list that are out of bounds
  ILoBullet removeOutOfBounds();

  // Creates a bullet that the player shot
  ILoBullet shoot();

  // Handles collision events for a list of bullets (remove the old ones and
  // create explosion)
  ILoBullet collisions(ILoShip los);

  // Returns the number bullets that the given ship is colliding with
  int numberColliding(IGameObject ship);

  // Creates bullets from an explosion
  // n = bullets to create - 1
  ILoBullet createBullets(int i, int n, int x, int y, int radius);

  // Returns the number of bullets on the screen
  int bulletCount();
}

class MtLoBullet implements ILoBullet {
  MtLoBullet() {

  }

  // Moves all the bullets in this list
  public ILoBullet move() {
    return this;
  }

  // Draws all the bullets in this list
  public WorldScene draw(WorldScene scene) {
    return scene;
  }

  // Remove all the bullets that are out of bounds
  public ILoBullet removeOutOfBounds() {
    return this;
  }

  // Creates new bullet
  public ILoBullet shoot() {
    Bullet b = new Bullet(Global.initBulletRadius, 0, -1 * Global.bulletSpeed, Global.width / 2,
        Global.height, 2);

    return new ConsLoBullet(b, this);
  }

  // Removes bullets that have collided,
  public ILoBullet collisions(ILoShip los) {
    return this;
  }

  // Returns the number of bullets that the given ship is colliding with
  public int numberColliding(IGameObject ship) {
    return 0;
  }

  // Creates the bullets from the explosion
  public ILoBullet createBullets(int i, int n, int x, int y, int radius) {
    if (i > n) {
      return this;
    }
    else {
      double angle = i * (360.0 / (n + 1));
      IGameObject b = new Bullet(
          Math.min(radius + Global.bulletRadiusChange, Global.maxBulletRadius),
          (int) (Math.cos(Math.toRadians(angle)) * Global.bulletSpeed),
          (int) (Math.sin(Math.toRadians(angle)) * Global.bulletSpeed), x, y, n + 2);

      return new ConsLoBullet(b, this.createBullets(i + 1, n, x, y, radius));
    }

  }

  // Counts the total number of bullets
  public int bulletCount() {
    return 0;
  }
}

class ConsLoBullet implements ILoBullet {
  IGameObject first;
  ILoBullet rest;

  ConsLoBullet(IGameObject first, ILoBullet rest) {
    this.first = first;
    this.rest = rest;
  }

  // Moves all the bullets
  public ILoBullet move() {
    return new ConsLoBullet(this.first.move(), this.rest.move());
  }

  // Draws all the bullets
  public WorldScene draw(WorldScene scene) {
    scene = this.first.draw(scene);
    scene = this.rest.draw(scene);

    return scene;
  }

  // Removes the bullets that are out of bounds
  public ILoBullet removeOutOfBounds() {
    if (this.first.outOfBounds()) {
      return this.rest.removeOutOfBounds();
    }
    else {
      return new ConsLoBullet(this.first, this.rest.removeOutOfBounds());
    }
  }

  // Creates a new bullet that was shot
  public ILoBullet shoot() {
    return new ConsLoBullet(this.first, this.rest.shoot());
  }

  // Removes bullets that have collided with ships and explodes them
  public ILoBullet collisions(ILoShip los) {
    if (los.numberColliding(this.first) > 0) {
      return this.first.doExplosion(this.rest, los);

    }
    else {
      return new ConsLoBullet(this.first, this.rest.collisions(los));
    }
  }

  // Counts how many bullets the given ship is colliding with
  public int numberColliding(IGameObject ship) {
    if (this.first.isColliding(ship)) {
      return 1 + this.rest.numberColliding(ship);
    }
    return this.rest.numberColliding(ship);
  }

  // Creates new bullets for the explosion
  public ILoBullet createBullets(int i, int n, int x, int y, int radius) {
    return new ConsLoBullet(this.first, this.rest.createBullets(i, n, x, y, radius));
  }

  // Counts the total number of bullets
  public int bulletCount() {
    return 1 + this.rest.bulletCount();
  }
}

class Bullet extends AGameObject {
  // Keeps track of how many bullets this should explode into
  int bulletCount;

  Bullet(int radius, int xVelocity, int yVelocity, int x, int y, int bulletCount) {
    super(radius, xVelocity, yVelocity, x, y, Global.bulletColor);
    this.bulletCount = bulletCount;
  }

  // Moves a bullet by some amount
  public IGameObject move() {
    return new Bullet(this.radius, this.xVelocity, this.yVelocity, this.x + this.xVelocity,
        this.y + this.yVelocity, this.bulletCount);
  }

  // Delegation method for bullet explosion
  public ILoBullet doExplosion(ILoBullet rest, ILoShip los) {
    return rest.collisions(los).createBullets(0, this.bulletCount - 1, this.x, this.y, this.radius);
  }

}

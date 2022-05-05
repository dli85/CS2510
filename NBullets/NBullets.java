import java.util.Random;

import javalib.funworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import tester.Tester;

class ExamplesMyGame {
  boolean testBigBang(Tester t) {
    MyGame world = new MyGame(10);

    return world.bigBang(Global.width, Global.height, 1.0 / Global.tickRate);
  }

  int seed = 53894 + 14 + 1;

  // Nothing on the screen
  ILoBullet mtLoB = new MtLoBullet();
  ILoShip mtLoS = new MtLoShip();
  MyGame emptyGame = new MyGame(mtLoB, mtLoS, 0, 10, 0, new Random(seed));

  // Some bullets and some ships, but none are colliding
  ILoBullet LoB1 = new ConsLoBullet(
      new Bullet(Global.initBulletRadius, 0, Global.bulletSpeed, Global.width / 2, 200, 2), mtLoB);
  ILoShip LoS1 = new ConsLoShip(new Ship(Global.height / 30, 4, 0, 10, 150), mtLoS);
  MyGame notColliding = new MyGame(LoB1, LoS1, 0, 10, 0, new Random(seed));

  // One bullet touching one ship
  ILoBullet LoB2 = new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 250, 200, 2), mtLoB);
  ILoShip LoS2 = new ConsLoShip(new Ship(10, 4, 0, 250 - 12, 200), mtLoS);
  MyGame oneOnOne = new MyGame(LoB2, LoS2, 0, 10, 0, new Random(seed));

  // Two bullets touching two ships (1 on 1 and 1 on 1)
  ILoBullet LoB3 = new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 250, 200, 2),
      new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 100, 100, 2), mtLoB));
  ILoShip LoS3 = new ConsLoShip(new Ship(10, 4, 0, 250 - 12, 200),
      new ConsLoShip(new Ship(10, 4, 0, 100, 90), mtLoS));
  MyGame oneOnOneX2 = new MyGame(LoB2, LoS2, 0, 10, 0, new Random(seed));

  // One bullet touching two ships and one ship touching two bullets (1 on 2 and 1
  // on 2)
  ILoBullet LoB4 = new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 250, 200, 2),
      new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 250, 200, 2),
          new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 100, 100, 2), mtLoB)));
  ILoShip LoS4 = new ConsLoShip(new Ship(10, 4, 0, 250 - 12, 200), new ConsLoShip(
      new Ship(10, 4, 0, 100, 90), new ConsLoShip(new Ship(10, 40, 0, 100, 90), mtLoS)));
  MyGame twoOnOneX2 = new MyGame(LoB2, LoS2, 0, 10, 0, new Random(seed));

  // One bullet and one ship both out of bounds
  ILoBullet LoB5 = new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 1000, 100, 2), mtLoB);
  ILoShip LoS5 = new ConsLoShip(new Ship(10, 4, 0, 1000, 1000), mtLoS);
  MyGame outOfBounds = new MyGame(LoB5, LoS5, 0, 10, 0, new Random(seed));

  IGameObject testB = new Bullet(2, 2, Global.bulletSpeed, 250, 200, 2);
  IGameObject testS = new Ship(10, 4, 0, 100, 100);
  IGameObject testB2 = new Bullet(4, -6, -4, 250, 200, 2);
  IGameObject testS2 = new Ship(10, 4, 0, 425, 100);
  IGameObject testOutB = new Bullet(2, 0, Global.bulletSpeed, 1000, 1000, 2);
  IGameObject testOutS = new Ship(10, 4, 0, 1000, 1000);

  // Test the move method for game objects
  boolean testIGameObjectMove(Tester t) {
    return t.checkExpect(testB.move(),
        new Bullet(2, 2, Global.bulletSpeed, 252, 200 + Global.bulletSpeed, 2))
        && t.checkExpect(testS.move(), new Ship(10, 4, 0, 104, 100))
        && t.checkExpect(testB2.move(), new Bullet(4, -6, -4, 244, 196, 2))
        && t.checkExpect(testS2.move(), new Ship(10, 4, 0, 429, 100));
  }

  boolean testIGameObjectOutOfBounds(Tester t) {
    return t.checkExpect(testB.outOfBounds(), false) && t.checkExpect(testS.outOfBounds(), false)
        && t.checkExpect(testOutB.outOfBounds(), true)
        && t.checkExpect(testOutS.outOfBounds(), true);
  }

  boolean testDraw(Tester t) {
    WorldScene testExpect1 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Global.bulletColor), 250, 200);

    WorldScene testExpect2 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Global.shipColor), 425, 100);

    return t.checkExpect(testB.draw(new WorldScene(Global.width, Global.height)), testExpect1)
        && t.checkExpect(testS2.draw(new WorldScene(Global.width, Global.height)), testExpect2);
  }

  boolean testIGameObjectIsColliding(Tester t) {
    // Is colliding with TestB
    IGameObject testCollidingS = new Ship(10, 4, 0, 250 - 12, 200);
    // Is colliding with TestS
    IGameObject testCollidingB = new Bullet(2, 2, Global.bulletSpeed, 90, 100, 2);

    return t.checkExpect(testB.isColliding(testCollidingS), true)
        && t.checkExpect(testCollidingB.isColliding(testS), true)
        && t.checkExpect(testB.isColliding(testS), false);
  }

  boolean testIGameObjectIsCollidingHelper(Tester t) {

    return t.checkExpect(testB.collidingHelper(10, 250 - 12, 200), true)
        && t.checkExpect(testB.collidingHelper(10, 300, 200), false)
        && t.checkExpect(testS.collidingHelper(2, 90, 100), true);
  }

  // Maybe test where bullets == 3??
  boolean testIGameObjectDoExplosion(Tester t) {
    ILoBullet expected1 = new ConsLoBullet(new Bullet(4, 8, 0, 250, 200, 3),
        new ConsLoBullet(new Bullet(4, -8, 0, 250, 200, 3), new MtLoBullet()));
    ILoBullet expected2 = new ConsLoBullet(testB, new ConsLoBullet(new Bullet(6, 8, 0, 250, 200, 3),
        new ConsLoBullet(new Bullet(6, -8, 0, 250, 200, 3), new MtLoBullet())));

    return t.checkExpect(testB.doExplosion(new MtLoBullet(), new MtLoShip()), expected1) && t
        .checkExpect(testB2.doExplosion(new ConsLoBullet(testB, new MtLoBullet()), new MtLoShip()),
            expected2);
  }

  boolean testILoBulletMove(Tester t) {
    ILoBullet expected1 = new ConsLoBullet(new Bullet(2, 0, 8, 250 + 0, 200 + 8, 2),
        new MtLoBullet());
    ILoBullet expected2 = new ConsLoBullet(new Bullet(2, 0, 8, 250 + 0, 200 + 8, 2),
        new ConsLoBullet(new Bullet(2, 0, 8, 100 + 0, 100 + 8, 2), new MtLoBullet()));

    return t.checkExpect(mtLoB.move(), mtLoB) && t.checkExpect(LoB1.move(), expected1)
        && t.checkExpect(LoB3.move(), expected2);
  }

  boolean testILoBulletDraw(Tester t) {
    WorldScene expected = new WorldScene(Global.width, Global.height);
    WorldScene expected2 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Global.bulletColor), 250, 200)
        .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Global.bulletColor), 100, 100);

    return t.checkExpect(mtLoB.draw(new WorldScene(Global.width, Global.height)), expected)
        && t.checkExpect(LoB3.draw(new WorldScene(Global.width, Global.height)), expected2);
  }

  boolean testILoBulletRemoveOutOfBounds(Tester t) {

    ILoBullet test = new ConsLoBullet(new Bullet(2, 0, Global.bulletSpeed, 1000, 100, 2),
        new ConsLoBullet(
            new Bullet(Global.initBulletRadius, 0, Global.bulletSpeed, Global.width / 2, 200, 2),
            mtLoB));

    /*
     * Test1: remove out of bounds from empty Test2-4: check description of
     * bulletLists
     */

    return t.checkExpect(mtLoB.removeOutOfBounds(), mtLoB)
        && t.checkExpect(LoB1.removeOutOfBounds(), LoB1)
        && t.checkExpect(LoB5.removeOutOfBounds(), mtLoB)
        && t.checkExpect(test.removeOutOfBounds(), LoB1);
  }

  boolean testILoBulletShoot(Tester t) {
    ILoBullet expected1 = new ConsLoBullet(new Bullet(2, 0, -1 * Global.bulletSpeed, 250, 300, 2),
        mtLoB);
    ILoBullet expected2 = new ConsLoBullet(
        new Bullet(Global.initBulletRadius, 0, Global.bulletSpeed, Global.width / 2, 200, 2),
        new ConsLoBullet(new Bullet(2, 0, -1 * Global.bulletSpeed, 250, 300, 2), mtLoB));

    return t.checkExpect(mtLoB.shoot(), expected1) && t.checkExpect(LoB1.shoot(), expected2);
  }

  boolean testILoBulletCollisions(Tester t) {
    ILoBullet expected1 = new ConsLoBullet(new Bullet(4, 8, 0, 250, 200, 3),
        new ConsLoBullet(new Bullet(4, -8, 0, 250, 200, 3), mtLoB)); // Explosion from 1 bullet

    // Explosion from 1 bullet on 2 ships collision
    ILoBullet expected2 = new ConsLoBullet(new Bullet(4, 8, 0, 100, 100, 3),
        new ConsLoBullet(new Bullet(4, -8, 0, 100, 100, 3),
            new ConsLoBullet(new Bullet(4, 8, 0, 250, 200, 3),
                new ConsLoBullet(new Bullet(4, -8, 0, 250, 200, 3),
                    new ConsLoBullet(new Bullet(4, 8, 0, 250, 200, 3),
                        new ConsLoBullet(new Bullet(4, -8, 0, 250, 200, 3), mtLoB))))));

    /*
     * Test1: mtbullets colliding with mt ships Test2: mtBullets colliding with some
     * ships Test3: Some bullets colliding with mt ships Test4: Bullets and Ships
     * colliding (and exploding) colliding Test5: 2 bullets colliding with 1 ship
     */

    return t.checkExpect(mtLoB.collisions(mtLoS), mtLoB)
        && t.checkExpect(mtLoB.collisions(LoS3), mtLoB)
        && t.checkExpect(LoB4.collisions(mtLoS), LoB4)
        && t.checkExpect(LoB2.collisions(LoS2), expected1) // See LoB2 definition for description
        && t.checkExpect(LoB4.collisions(LoS4), expected2);

  }

  boolean testILoBulletNumberColliding(Tester t) {

    /*
     * Test1: mtbullets colliding with ship Test2: Bullet not colliding with ship
     * Test3: Bullet actually colliding with ship Test4: Two bullets colliding with
     * ship
     */

    return t.checkExpect(mtLoB.numberColliding(new Ship(10, 4, 0, 250 - 12, 200)), 0)
        && t.checkExpect(LoB1.numberColliding(new Ship(Global.height / 30, 4, 0, 10, 150)), 0)
        && t.checkExpect(LoB2.numberColliding(new Ship(10, 4, 0, 250 - 12, 200)), 1)
        && t.checkExpect(LoB4.numberColliding(new Ship(10, 4, 0, 250 - 12, 200)), 2);
  }

  boolean testILoBulletCreateBullets(Tester t) {
    ILoBullet expected1 = new ConsLoBullet(new Bullet(6, 8, 0, 200, 150, 3),
        new ConsLoBullet(new Bullet(6, -8, 0, 200, 150, 3), mtLoB));

    ILoBullet expected2 = new ConsLoBullet(new Bullet(2, 0, 8, 250, 200, 2),
        new ConsLoBullet(new Bullet(8, 8, 0, 100, 100, 4),
            new ConsLoBullet(new Bullet(8, -3, 6, 100, 100, 4),
                new ConsLoBullet(new Bullet(8, -4, -6, 100, 100, 4), mtLoB))));
    return t.checkExpect(mtLoB.createBullets(0, 1, 200, 150, 4), expected1)
        && t.checkExpect(LoB1.createBullets(0, 2, 100, 100, 6), expected2);
  }

  boolean testILoBulletBulletCount(Tester t) {
    return t.checkExpect(mtLoB.bulletCount(), 0) && t.checkExpect(LoB4.bulletCount(), 3);
  }

  boolean testILoShipMove(Tester t) {
    ILoShip expected1 = new ConsLoShip(new Ship(10, 4, 0, 14, 150), mtLoS);
    ILoShip expected2 = new ConsLoShip(new Ship(10, 4, 0, 242, 200),
        new ConsLoShip(new Ship(10, 4, 0, 104, 90), mtLoS));

    return t.checkExpect(mtLoS.move(), mtLoS) && t.checkExpect(LoS1.move(), expected1)
        && t.checkExpect(LoS3.move(), expected2);
  }

  boolean testILoShipDraw(Tester t) {
    WorldScene expected1 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Global.shipColor), 250 - 12, 200)
        .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Global.shipColor), 100, 90);

    return t.checkExpect(mtLoS.draw(new WorldScene(Global.width, Global.height)),
        new WorldScene(Global.width, Global.height))
        && t.checkExpect(LoS3.draw(new WorldScene(Global.width, Global.height)), expected1);
  }

  boolean testILoShipRemoveOutOfBounds(Tester t) {
    return t.checkExpect(mtLoS.removeOutOfBounds(), mtLoS)
        && t.checkExpect(LoS1.removeOutOfBounds(), LoS1)
        && t.checkExpect(LoS5.removeOutOfBounds(), mtLoS);
  }

  // Test the generate ships method using a random seed
  boolean testILoShipGenerateShips(Tester t) {
    ILoShip expected1 = new ConsLoShip(new Ship(10, -4, 0, 500, 48), mtLoS);
    ILoShip expected2 = new ConsLoShip(new Ship(10, 4, 0, 10, 150), new ConsLoShip(
        new Ship(10, -4, 0, 500, 48), new ConsLoShip(new Ship(10, 4, 0, 0, 119), mtLoS)));

    return t.checkExpect(mtLoS.generateShips(1, new Random(seed)), expected1)
        && t.checkExpect(LoS1.generateShips(2, new Random(seed)), expected2);
  }

  boolean testILoShipCollisions(Tester t) {

    /*
     * Test1: mt list of ship collide against mtofbullet Test2: mt loShip collide
     * against some bullets Test3: Some ships collide against mtLoBullets Test4: 1
     * ship not colliding against 1 bullet Test5: 1 ship colliding against 1 bullet
     * Test6: 2 Ships colliding against 1 bullet
     */

    return t.checkExpect(mtLoS.collisions(mtLoB), mtLoS)
        && t.checkExpect(mtLoS.collisions(LoB3), mtLoS)
        && t.checkExpect(LoS1.collisions(mtLoB), LoS1) && t.checkExpect(LoS1.collisions(LoB1), LoS1)
        && t.checkExpect(LoS2.collisions(LoB2), mtLoS)
        && t.checkExpect(LoS4.collisions(LoB4), mtLoS);
  }

  boolean testILoShipNumberColliding(Tester t) {
    return t.checkExpect(mtLoS.numberColliding(testB), 0)
        && t.checkExpect(LoS1.numberColliding(
            new Bullet(Global.initBulletRadius, 0, Global.bulletSpeed, 10, 140, 2)), 1)
        && t.checkExpect(LoS4.numberColliding(new Bullet(2, 0, Global.bulletSpeed, 100, 100, 2)),
            2);
  }

  boolean testILoShipCountShips(Tester t) {
    return t.checkExpect(mtLoS.countShips(), 0) && t.checkExpect(LoS1.countShips(), 1)
        && t.checkExpect(LoS4.countShips(), 3);
  }

  boolean testMyGameOnTick(Tester t) {
    ILoShip example1Ships = new ConsLoShip(new Ship(10, -4, 0, 496, 101),
        new ConsLoShip(new Ship(10, -4, 0, 496, 86), mtLoS));
    MyGame example1 = new MyGame(mtLoB, example1Ships, 1, 10, 0, new Random(seed));

    ILoShip example2Ships = new ConsLoShip(new Ship(10, 4, 0, 14, 150), new ConsLoShip(
        new Ship(10, -4, 0, 496, 101), new ConsLoShip(new Ship(10, -4, 0, 496, 86), mtLoS)));
    ILoBullet example2Bullets = new ConsLoBullet(new Bullet(2, 0, 8, 250, 208, 2), mtLoB);
    MyGame example2 = new MyGame(example2Bullets, example2Ships, 1, 10, 0, new Random(seed));
    
    // First test is equal to emptyGame, we are just reseting the random
    // Second test is equal to not colliding, we are just reseting the random
    return t.checkExpect(new MyGame(mtLoB, mtLoS, 0, 10, 0, new Random(seed)).onTick(), example1)
        && t.checkExpect(new MyGame(LoB1, LoS1, 0, 10, 0, new Random(seed)).onTick(), example2);
  }

  boolean testMyGameMakeScene(Tester t) {
    WorldScene result = new WorldScene(Global.width, Global.height)
        .placeImageXY(new TextImage("Ships destroyed: 0", Global.fontSize, Global.fontColor),
            Global.width / 2, 0 + Global.fontSize)
        .placeImageXY(new TextImage("Bullets left: 10", Global.fontSize, Global.fontColor),
            Global.width / 2, 0 + Global.fontSize * 2);

    return t.checkExpect(emptyGame.makeScene(), result);
  }

  boolean testMyGameMoveObjects(Tester t) {
    MyGame example1 = new MyGame(mtLoB, mtLoS, 0, 10, 0, new Random(seed));
    ILoBullet example2Bullets = new ConsLoBullet(new Bullet(2, 0, 8, 250, 208, 2), mtLoB);
    ILoShip example2Ships = new ConsLoShip(new Ship(10, 4, 0, 14, 150), mtLoS);
    MyGame example2 = new MyGame(example2Bullets, example2Ships, 0, 10, 0, new Random(seed));

    return t.checkExpect(emptyGame.moveObjects(), example1)
        && t.checkExpect(notColliding.moveObjects(), example2);
  }

  boolean testMyGameRemoveObejcts(Tester t) {
    MyGame example1 = new MyGame(mtLoB, mtLoS, 0, 10, 0, new Random(seed));

    return t.checkExpect(emptyGame.removeObjects(), emptyGame)
        && t.checkExpect(outOfBounds.removeObjects(), example1)
        && t.checkExpect(notColliding.removeObjects(), notColliding);
  }

  boolean testMyGameDrawBullets(Tester t) {
    WorldScene result = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Global.bulletColor), 250, 200);

    return t.checkExpect(oneOnOne.drawBullets(new WorldScene(Global.width, Global.height)), result);
  }

  boolean testMyGameDrawShips(Tester t) {
    WorldScene result = new WorldScene(Global.width, Global.height)
        .placeImageXY(new CircleImage(10, OutlineMode.SOLID, Global.shipColor), 250 - 12, 200);

    return t.checkExpect(oneOnOne.drawShips(new WorldScene(Global.width, Global.height)), result);
  }

  boolean testMyGameDrawText(Tester t) {
    WorldScene result = new WorldScene(Global.width, Global.height)
        .placeImageXY(new TextImage("Ships destroyed: 0", Global.fontSize, Global.fontColor),
            Global.width / 2, 0 + Global.fontSize)
        .placeImageXY(new TextImage("Bullets left: 10", Global.fontSize, Global.fontColor),
            Global.width / 2, 0 + Global.fontSize * 2);

    return t.checkExpect(notColliding.drawText(new WorldScene(Global.width, Global.height)),
        result);
  }

  boolean testMyGameGenerateShips(Tester t) {
    ILoShip example1Ships = new ConsLoShip(new Ship(10, -4, 0, 500, 101), new ConsLoShip(
        new Ship(10, -4, 0, 500, 86),  mtLoS));
    MyGame example1 = new MyGame(mtLoB, example1Ships, 0, 10, 0, new Random(seed));

    return t.checkExpect(new MyGame(mtLoB, mtLoS, 0, 10, 0, new Random(seed)).generateShips(),
        example1);
  }
  
  boolean testMyGameCollisions(Tester t) {
    ILoBullet example1Bullets = new ConsLoBullet(new Bullet(4, 8, 0, 250, 200, 3),
        new ConsLoBullet(new Bullet(4, -8, 0, 250, 200, 3), mtLoB));
    MyGame example1 = new MyGame(example1Bullets, mtLoS, 0, 10, 1, new Random(seed));

    return t.checkExpect(emptyGame.collisions(), emptyGame)
        && t.checkExpect(notColliding.collisions(), notColliding)
        && t.checkExpect(oneOnOne.collisions(), example1);
  }
  
  boolean testMyGameOneKeyEvent(Tester t) {
    ILoBullet example1Bullets = new ConsLoBullet(new Bullet(2, 0, -8, 250, 300, 2), mtLoB);
    MyGame example1 = new MyGame(example1Bullets, mtLoS, 0, 9, 0, new Random(seed));
    
    ILoBullet example2Bullets = new ConsLoBullet(new Bullet(2, 0, 8, 250, 200, 2),
        new ConsLoBullet(new Bullet(2, 0, -8, 250, 300, 2), mtLoB));
    MyGame example2 = new MyGame(example2Bullets, LoS1, 0, 9, 0, new Random(seed));
    
    return t.checkExpect(emptyGame.onKeyEvent(" "), example1)
        && t.checkExpect(notColliding.onKeyEvent(" "), example2);
  }
  
  boolean testMyGameIncrementTick(Tester t) {
    MyGame example1 = new MyGame(mtLoB, mtLoS, 1, 10, 0, new Random(seed));
    MyGame example2 = new MyGame(LoB1, LoS1, 1, 10, 0, new Random(seed));
    
    return t.checkExpect(emptyGame.incrementTick(), example1)
        && t.checkExpect(notColliding.incrementTick(), example2);
  }
  
  boolean testMyGameWorldEnds(Tester t) {
    MyGame ended = new MyGame(mtLoB, LoS4, 4000, 0, 40, new Random(seed));
    
    
    return t.checkExpect(ended.worldEnds(), new WorldEnd(true, ended.makeEndScene()))
        && t.checkExpect(emptyGame.worldEnds(), new WorldEnd(false, emptyGame.makeEndScene()));
  }
  
  boolean testMakeEndScene(Tester t) {
    MyGame ended1 = new MyGame(mtLoB, LoS4, 4000, 0, 40, new Random(seed));
    MyGame ended2 = new MyGame(mtLoB, LoS4, 2398, 0, 10, new Random(seed));
    
    WorldScene expected1 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new TextImage("Game Over!", Global.fontSize * 2, Global.gameOverColor),
            Global.width / 2, Global.height / 2)
        .placeImageXY(new TextImage("You destroyed 40 ships!", Global.fontSize, Global.fontColor),
            Global.width / 2, Global.height / 2 + Global.fontSize * 2);

    WorldScene expected2 = new WorldScene(Global.width, Global.height)
        .placeImageXY(new TextImage("Game Over!", Global.fontSize * 2, Global.gameOverColor),
            Global.width / 2, Global.height / 2)
        .placeImageXY(new TextImage("You destroyed 10 ships!", Global.fontSize, Global.fontColor),
            Global.width / 2, Global.height / 2 + Global.fontSize * 2);
    
    return t.checkExpect(ended1.makeEndScene(), expected1)
        && t.checkExpect(ended2.makeEndScene(), expected2);
  }
  

}
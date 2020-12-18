package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import edu.lewisu.cs.cpsc41000.common.Boundary;
import edu.lewisu.cs.cpsc41000.common.EdgeHandler;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObjectDrawer;
import edu.lewisu.cs.cpsc41000.common.MobileImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;
import edu.lewisu.cs.cpsc41000.common.labels.SoundLabel;
import edu.lewisu.cs.cpsc41000.common.motioncontrollers.Tracker;

public class DungeonBasic extends ApplicationAdapter {
	SpriteBatch batch;
	MobileImageBasedScreenObject enemy;
	Player player;
	ImageBasedScreenObject topDoor, bottomDoor, leftDoor, rightDoor, topLock, bottomLock, leftLock, rightLock, topBD, bottomBD, leftBD, rightBD;
	ImageBasedScreenObject key, chest, campfire, shop, stairs, potion, bomb, amulet;
	ImageBasedScreenObjectDrawer artist;
	ArrayList<ImageBasedScreenObject> walls;
	EdgeHandler edge;
	OrthographicCamera cam, titleCam, pauseCam;
	float WIDTH, HEIGHT;
	SoundLabel sound;
	int scene; //0-title, 1-pause, 2-play
	ActionLabel title, playPos, mousePos;
	ArrayList<Boundary> boundaries;
	Texture background, doorTex, lockTex, wallTex, img;
	Texture keyTex, chestTex, campfireTex, shopTex, stairsTex, potionTex, bombTex, amuletTex;
	TextureRegion wall, door, lock;
	Tracker tracker;
	boolean seeking, inCombat;
	
	StatTracker game;
	
	@Override
	public void create () {
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		background = new Texture("textures/floor.png");
		wallTex = new Texture("textures/wall.png");
		wall = new TextureRegion();
		wall.setRegion(wallTex);
		doorTex = new Texture("textures/door.png");
		door = new TextureRegion();
		door.setRegion(doorTex);
		lockTex = new Texture("textures/lockeddoor.png");
		lock = new TextureRegion();
		lock.setRegion(lockTex);
		img = new Texture("textures/playerstandin.png");
		keyTex = new Texture("textures/key.png");
		chestTex = new Texture("textures/chest.png");
		campfireTex = new Texture("textures/campfire.png");
		shopTex = new Texture("textures/shop.png");
		stairsTex = new Texture("textures/stairs.png");
		potionTex = new Texture("textures/potion.png");
		bombTex = new Texture("textures/bomb.png");
		amuletTex = new Texture("textures/amulet.png");
		batch = new SpriteBatch();
		
		amulet = new ImageBasedScreenObject(amuletTex,(int) WIDTH/2,(int) HEIGHT/2, amuletTex.getWidth()/2, amuletTex.getHeight()/2, 0, 5, 5, false, false);
		key = new ImageBasedScreenObject(keyTex,(int) WIDTH/2,(int) HEIGHT/2, keyTex.getWidth()/2, keyTex.getHeight()/2, 0, 5, 5, false, false);
		chest = new ImageBasedScreenObject(chestTex,(int) WIDTH/2,(int) HEIGHT/2, chestTex.getWidth()/2, chestTex.getHeight()/2, 0, 5, 5, false, false);
		campfire = new ImageBasedScreenObject(campfireTex, (int) WIDTH/2,(int) HEIGHT/2, campfireTex.getWidth()/2, campfireTex.getHeight()/2, 0, 5, 5, false, false);
		shop = new ImageBasedScreenObject(shopTex,(int) WIDTH/2,(int) HEIGHT/2, shopTex.getWidth()/2, shopTex.getHeight()/2, 0, 5, 5, false, false);
		stairs = new ImageBasedScreenObject(stairsTex,(int) WIDTH/2,(int) HEIGHT/2, stairsTex.getWidth()/2, stairsTex.getHeight()/2, 0, 5, 5, false, false);
		
		player = new Player(img,300,300,true);
		player.setMaxSpeed(200);
		player.setAcceleration(1600);
		player.setDeceleration(1600);
		player.scale(3,3);
		
		game = new StatTracker(player);
		
		enemy = new MobileImageBasedScreenObject(img,(int) WIDTH/2, (int) HEIGHT/2,true);
		enemy.setMaxSpeed(100);
		enemy.setAcceleration(400);
		enemy.setDeceleration(100);
		enemy.scale(3,3);
		
		//Each of the four doors has an unlocked, locked, and battle locked variant
		topDoor = new ImageBasedScreenObject(doorTex,(int) WIDTH/2-90,(int) HEIGHT-51,0,0,0,5,5,false,false);
		topLock = new ImageBasedScreenObject(lockTex,(int) WIDTH/2-90,(int) HEIGHT-51,0,0,0,5,5,false,false);
		topBD = new ImageBasedScreenObject(lockTex,(int) WIDTH/2-90,(int) HEIGHT-51,0,0,0,5,5,false,false);
		bottomDoor = new ImageBasedScreenObject(doorTex,(int) WIDTH/2-90,(int) 1,0,0,0,5,5,false,true);
		bottomLock = new ImageBasedScreenObject(lockTex,(int) WIDTH/2-90,(int) 1,0,0,0,5,5,false,true);
		bottomBD = new ImageBasedScreenObject(lockTex,(int) WIDTH/2-90,(int) 1,0,0,0,5,5,false,true);
		leftDoor = new ImageBasedScreenObject(doorTex,(int) 51,(int) HEIGHT/2-90,0,0,90,5,5,false,false);
		leftLock = new ImageBasedScreenObject(lockTex,(int) 51,(int) HEIGHT/2-90,0,0,90,5,5,false,false);
		leftBD = new ImageBasedScreenObject(lockTex,(int) 51,(int) HEIGHT/2-90,0,0,90,5,5,false,false);
		rightDoor = new ImageBasedScreenObject(doorTex,(int) WIDTH-1,(int) HEIGHT/2-90,0,0,90,5,5,true,true);
		rightLock = new ImageBasedScreenObject(lockTex,(int) WIDTH-1,(int) HEIGHT/2-90,0,0,90,5,5,true,true);
		rightBD = new ImageBasedScreenObject(lockTex,(int) WIDTH-1,(int) HEIGHT/2-90,0,0,90,5,5,true,true);
		
		boundaries = new ArrayList<Boundary>();
		boundaries.add(new Boundary(0,0,50,HEIGHT));
		boundaries.add(new Boundary(WIDTH-50,0,WIDTH,HEIGHT));
		boundaries.add(new Boundary(0,0,WIDTH,50));
		boundaries.add(new Boundary(0,HEIGHT-50,WIDTH,HEIGHT));
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		
		titleCam = new OrthographicCamera(WIDTH,HEIGHT);
		titleCam.translate(WIDTH/2,HEIGHT/2);
		titleCam.update();
		
		pauseCam = new OrthographicCamera(WIDTH,HEIGHT);
		pauseCam.translate(WIDTH/2,HEIGHT/2);
		pauseCam.update();
		
		artist = new ImageBasedScreenObjectDrawer(batch);
		batch.setProjectionMatrix(cam.combined);
		
		edge = new EdgeHandler(player,cam,batch,0,WIDTH,0,HEIGHT,0,
		EdgeHandler.EdgeConstants.LOCK,
		EdgeHandler.EdgeConstants.LOCK);
		
		scene = 0;
		title = new ActionLabel("Welcome to Dungeon Basic", 200, 400, "fonts/arial.fnt");
		mousePos = new ActionLabel(Gdx.input.getX()+ ", " + Gdx.input.getY(), 10, 10, "fonts/arial.fnt");
		playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
		
		//sound = new SoundLabel("hey", 200, 400, "fonts/arial.fnt", "audio/heartbeat.wav");
		tracker = new Tracker(enemy,player,0.5f);
		seeking = false;
		
		hideRoomAssets();
	}
	
	public void renderMainScene() {
		mousePos = new ActionLabel(Gdx.input.getX()+ ", " + Gdx.input.getY(), 10, 10, "fonts/arial.fnt");
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 0;
			return;
		}
		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			seeking = !seeking;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			player.accelerateAtAngle(0);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			player.accelerateAtAngle(180);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			player.accelerateAtAngle(90);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			player.accelerateAtAngle(270);
		}
		if (Gdx.input.isKeyPressed(Keys.P)) {
			enemy.hide();
			game.endBattle();
		}
		
		
		player.applyPhysics(dt);
		if (player.getSpeed() > 0) {
			player.setRotation(player.getMotionAngle()-90f);
		}
		if (seeking) {
			tracker.track(dt); // applies physics to the chaser
			if (enemy.getSpeed() > 0) {
				enemy.setRotation(enemy.getMotionAngle()-90f);
			}
		}
		if (enemy.overlaps(player) && enemy.isVisible()) {
			System.out.println("Collision");
		}
//		if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
//			if (sound.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY())) {
//				sound.stopSound();
//				sound.playSound();
//			}
//		}
		
		Vector2 bounce;

		for (Boundary b : boundaries) {
			if (player.overlaps(b)) {
				bounce = player.preventOverlap(b);
				player.rebound(bounce.angle(),0f);
			}
			if (enemy.overlaps(b)) {
				bounce = enemy.preventOverlap(b);
				enemy.move(10*bounce.x,10*bounce.y);
				tracker.avoid(b.getParallel(),dt);
				if (enemy.getSpeed() > 0) {
					enemy.setRotation(enemy.getMotionAngle()-90f);
				}	
			}
		}

		edge.enforceEdges();
		batch.setProjectionMatrix(cam.combined); // game scene camera
		batch.begin();
		batch.draw(background, 0, 0, WIDTH, HEIGHT);
		batch.draw(wall, 30, 0, 10, 10, HEIGHT, 50, 2, 1, 90);
		batch.draw(wall, WIDTH-20, 0, 10, 10, HEIGHT, 50, 2, 1, 90);
		batch.draw(wall, 0, 0, 10, 10, WIDTH, 50, 2, 1, 0);
		batch.draw(wall, 0, HEIGHT-50, 10, 10, WIDTH, 50, 2, 1, 0);
		playPos.draw(batch, 1f);
		mousePos.draw(batch, 1f);
		
		if (game.setBottomDoor() && !game.inCombat) {
			if (game.bottomLock()) {
				bottomDoor.hide();
				bottomLock.show();
				if (bottomLock.overlaps(player)) {
					System.out.println("Lock!");
					if (player.getKeys() >= 1) {
						game.moveBottom(HEIGHT-90);
						playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
						player.useKey();
					}
				}
			} else {
				bottomDoor.show();
				bottomLock.hide();				
				if (bottomDoor.overlaps(player)) {
					System.out.println("Door!");
					game.moveBottom(HEIGHT-90);
					playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
				}
			}
		} else {
			bottomDoor.hide();
			bottomLock.hide();
		}
		
		if (game.setTopDoor() && !game.inCombat) {
			if (game.topLock()) {
				topDoor.hide();
				topLock.show();
				if (topLock.overlaps(player)) {
					System.out.println("Lock!");
					if (player.getKeys() >= 1) {
						game.moveTop(70);
						playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
						player.useKey();
					}
				}
			} else {
				topDoor.show();
				topLock.hide();				
				if (topDoor.overlaps(player)) {
					System.out.println("Door!");
					game.moveTop(70);
					playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
				}
			}
		} else {
			topDoor.hide();
			topLock.hide();
		}
		
		if (game.setRightDoor() && !game.inCombat) {
			if (game.rightLock()) {
				rightDoor.hide();
				rightLock.show();
				if (rightLock.overlaps(player)) {
					System.out.println("Lock!");
					if (player.getKeys() >= 1) {
						game.moveRight(70);
						playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
						player.useKey();
					}
				}
			} else {
				rightDoor.show();
				rightLock.hide();				
				if (rightDoor.overlaps(player)) {
					System.out.println("Door!");
					game.moveRight(70);
					playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
				}
			}
		} else {
			rightDoor.hide();
			rightLock.hide();
		}
		
		if (game.setLeftDoor() && !game.inCombat) {
			if (game.leftLock()) {
				leftDoor.hide();
				leftLock.show();
				if (leftLock.overlaps(player)) {
					System.out.println("Lock!");
					if (player.getKeys() >= 1) {
						game.moveLeft(WIDTH-90);
						playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
						player.useKey();
					}
				}
			} else {
				leftDoor.show();
				leftLock.hide();
				if (leftDoor.overlaps(player)) {
					System.out.println("Door!");
					game.moveLeft(WIDTH-90);
					playPos = new ActionLabel(game.xString() + ", " + game.yString(), 10, 440, "fonts/arial.fnt");
				}
			}
		} else {
			leftDoor.hide();
			leftLock.hide();
		}
		
		if(game.inCombat) {
			if (!inCombat) {
				initBattle();
				inCombat = true;
			}
		} else {
			topBD.hide();
			bottomBD.hide();
			leftBD.hide();
			rightBD.hide();
			enemy.hide();
			inCombat = false;
		}
		
		if (game.showAmulet) {
			hideRoomAssets();
			amulet.show();
		} else if (game.showCampfire) {
			hideRoomAssets();
			campfire.show();
		} else if (game.showChest && !game.inCombat) {
			hideRoomAssets();
			chest.show();
		} else if (game.showKey && !game.inCombat) {
			hideRoomAssets();
			key.show();
		} else if (game.showShop) {
			hideRoomAssets();
			shop.show();
		} else if (game.showStairs) {
			hideRoomAssets();
			stairs.show();
		} else if (game.showNull) {
			hideRoomAssets();
		}
		
		if (player.overlaps(key) && key.isVisible()) {
			player.lootKey();
			key.hide();
			game.wipeItem();
		}
		if (player.overlaps(chest) && chest.isVisible()) {
			chest.hide();
			game.wipeItem();
		}
		if (player.overlaps(amulet) && amulet.isVisible()) {
			amulet.hide();
			game.wipeItem();
		}
		if (player.overlaps(stairs) && stairs.isVisible()) {
			game.advanceFloor();
			playPos = new ActionLabel(game.xString() + ", " + game.yString() + " Floor: " + game.getLevel(), 10, 440, "fonts/arial.fnt");
			hideRoomAssets();
		}
		
		
		if (player.getYPos()>=HEIGHT/2) {
			artist.draw(player);
			artist.draw(enemy);
		}
		
		artist.draw(topDoor);
		artist.draw(bottomDoor);
		artist.draw(leftDoor);
		artist.draw(rightDoor);
		
		artist.draw(topLock);
		artist.draw(bottomLock);
		artist.draw(leftLock);
		artist.draw(rightLock);
		
		artist.draw(topBD);
		artist.draw(bottomBD);
		artist.draw(leftBD);
		artist.draw(rightBD);
		
		artist.draw(amulet);
		artist.draw(key);
		artist.draw(chest);
		artist.draw(campfire);
		artist.draw(shop);
		artist.draw(stairs);
		
		if (player.getYPos()<HEIGHT/2) {
			artist.draw(player);
			artist.draw(enemy);
		}
		
/*		for (ImageBasedScreenObject wall : walls) {
			artist.draw(wall);
		}
*/
//		label.draw(batch,1f);
		batch.end();
	}
	
	public void hideRoomAssets() {
		amulet.hide();
		campfire.hide();
		chest.hide();
		key.hide();
		shop.hide();
		stairs.hide();
	}
	
	public void initBattle() {
		hideRoomAssets();
		System.out.println("Battle sequence initiated");
		topBD.show();
		bottomBD.show();
		leftBD.show();
		rightBD.show();
		enemy = new MobileImageBasedScreenObject(img,(int) WIDTH/2, (int) HEIGHT/2,true);
		enemy.setMaxSpeed(100);
		enemy.setAcceleration(400);
		enemy.setDeceleration(100);
		enemy.scale(3,3);
		tracker = new Tracker(enemy,player,0.5f);
	}
	
	public void renderTitleScene() {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 1;
		} else {
			batch.setProjectionMatrix(titleCam.combined);
			batch.begin();
			title.draw(batch,1f);
			batch.end();
		}
	}
	@Override
	public void render () {
		if (scene == 1) {
			renderMainScene();
		} else {
			renderTitleScene();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}

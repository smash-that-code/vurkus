package vurkus.concept;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import vurkus.concept.data.CircleEntity;
import vurkus.concept.data.InputState;
import vurkus.concept.data.Point;
import vurkus.concept.geometry.Angle;
import vurkus.concept.geometry.Direction;
import vurkus.concept.geometry.Line;

import java.util.ArrayDeque;

import static vurkus.concept.input.Keyboard.*;

//Actual code for interactive-multimedia-concept!
//It reacts to WASD and arrow keys (even Dualshock 4 if you have one attached).
public class Vurkus extends ApplicationAdapter {

	public static class EngineStaff {
		SpriteBatch batch;
		Texture playerTexture;
		Sprite playerSprite;
		BitmapFont font;
		ShapeRenderer shapeRenderer;
		OrthographicCamera camera;
		Controller controller;

		public void initialize(GameState state, final InputState inputState) {
			batch = new SpriteBatch();
			playerTexture = new Texture("player.png");
			playerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

			playerSprite = new Sprite(playerTexture);

			font = new BitmapFont();
			shapeRenderer = new ShapeRenderer();

			//no hot plug/unplug logic
			controller = Controllers.getControllers().get(0);

			camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


			Gdx.input.setInputProcessor(new InputAdapter() {

				@Override
				public boolean keyDown (int keyCode) {
					if (keyCode == Input.Keys.ESCAPE) {
						inputState.keyboardKeyState.put(ESCAPE, Boolean.TRUE);
					}
					else if (keyCode == Input.Keys.SPACE) {
						inputState.keyboardKeyState.put(SPACE, Boolean.TRUE);
					}
					return true;
				}

				@Override
				public boolean keyUp(int keyCode) {
					if (keyCode == Input.Keys.SPACE) {
						inputState.keyboardKeyState.put(SPACE, Boolean.FALSE);
					}
					return true;
				}
			});

			Controllers.addListener(new ControllerAdapter() {
				@Override
				public boolean buttonUp(Controller controller, int buttonIndex) {
					System.out.println(buttonIndex);
					return true;
				}

				@Override
				public boolean axisMoved(Controller controller, int axisIndex, float value) {
//				System.out.println(axisIndex + " : " + value);
					return true;
				}
			});

			centerCamera(camera, state);
		}

		public void dispose() {
			batch.dispose();
			playerTexture.dispose();
			font.dispose();
			shapeRenderer.dispose();
		}

		public static void centerCamera(OrthographicCamera camera, GameState state) {
			state.cameraX = state.screenMaxWidth / 2.0f;
			state.cameraY = state.screenMaxHeight / 2.0f;
			camera.position.set(state.cameraX, state.cameraY, 0);
			camera.update();
		}

	}

	public static float randomizeWithRadiusInRange(float radius, float left, float right) {
		return left+radius + (float) Math.random()*(right - left - radius*2);
	}

	public static class GameState {
		public int screenMaxWidth;
		public int screenMaxHeight;

		public GameState(int screenMaxWidth, int screenMaxHeight) {
			this.screenMaxWidth = screenMaxWidth;
			this.screenMaxHeight = screenMaxHeight;
		}

		public float cameraX = 0;
		public float cameraY = 0;
		public float cameraSpeed = 100;

		public CircleEntity targetCircle = new CircleEntity(400, 400, 70, 45, 85*4);
		public ArrayDeque<Point> tail = new ArrayDeque<>();
		public int allowedTailLength = 0;
		public int gapLimit = 5;
		public int gapCounter = 0;
		public float lastCircleX = targetCircle.x;
		public float lastCircleY = targetCircle.y;

		public CircleEntity player = new CircleEntity(150, 150, 100, 0, 1000);

		public Color backgroundColor = new Color(0.1f, 0.5f, 1f, 1);

		public void randomizeTargetCircleState() {
			targetCircle.x = randomizeWithRadiusInRange(targetCircle.radius, 0, screenMaxWidth);
			targetCircle.y = randomizeWithRadiusInRange(targetCircle.radius, 0, screenMaxHeight);
			targetCircle.rotation = (float) Math.random()* 359;
			//tail specific staff
			lastCircleX = targetCircle.x;
			lastCircleY = targetCircle.y;
		}

		public void randomizePlayerPosition() {
			player.x = randomizeWithRadiusInRange(player.radius, 0, screenMaxWidth);
			player.y = randomizeWithRadiusInRange(player.radius, 0, screenMaxHeight);
		}

		public int pointsCounter = 0;
	}

	private GameState state;
	private InputState inputState;
	private EngineStaff engineStaff;

	@Override
	public void create() {
		state = new GameState(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		inputState = new InputState();

		engineStaff = new EngineStaff();
		engineStaff.initialize(state, inputState);
	}

	public static void handleKeyboardInput(Input input, InputState inputState) {
		inputState.keyboardKeyState.put(W, Boolean.FALSE);
		inputState.keyboardKeyState.put(A, Boolean.FALSE);
		inputState.keyboardKeyState.put(S, Boolean.FALSE);
		inputState.keyboardKeyState.put(D, Boolean.FALSE);
		inputState.keyboardKeyState.put(UP, Boolean.FALSE);
		inputState.keyboardKeyState.put(DOWN, Boolean.FALSE);
		inputState.keyboardKeyState.put(LEFT, Boolean.FALSE);
		inputState.keyboardKeyState.put(RIGHT, Boolean.FALSE);

		if (input.isKeyPressed(Input.Keys.W)) {
			inputState.keyboardKeyState.put(W, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.S)) {
			inputState.keyboardKeyState.put(S, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.A)) {
			inputState.keyboardKeyState.put(A, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.D)) {
			inputState.keyboardKeyState.put(D, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.LEFT)) {
			inputState.keyboardKeyState.put(LEFT, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.RIGHT)) {
			inputState.keyboardKeyState.put(RIGHT, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.UP)) {
			inputState.keyboardKeyState.put(UP, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.DOWN)) {
			inputState.keyboardKeyState.put(DOWN, Boolean.TRUE);
		}
	}

	public static void checkExit(InputState inputState) {
		if (inputState.keyboardKeyState.get(ESCAPE) == Boolean.TRUE) {
			Gdx.app.exit();
			System.exit(0);
		}
	}
	public static void calculateState(GameState state, float delta) {
		if (state.allowedTailLength > 0) {
			if (state.gapCounter == state.gapLimit) {
				Point newPiece = new Point(state.lastCircleX, state.lastCircleY);
				state.lastCircleX = state.targetCircle.x;
				state.lastCircleY = state.targetCircle.y;

				if (state.tail.size() == state.allowedTailLength) {
					state.tail.removeFirst();
				}

				state.tail.addLast(newPiece);

				state.gapCounter = 0;
			} else {
				state.gapCounter += 1;
			}
		}

		CircleEntity targetCircle = state.targetCircle;
		targetCircle.x += targetCircle.speed * Math.cos(targetCircle.rotation*Math.PI/180) * delta;
		targetCircle.y += targetCircle.speed * Math.sin(targetCircle.rotation*Math.PI/180) * delta;
	}

	public static float limit(float value) {
		return value < 0.2 && value > -0.2 ?
			0 :
			value;
	}

	//tested only on Dualshock 4!!!
	public static void handleControllerInput(Controller controller, InputState inputState) {
		float leftJoypadX = limit(controller.getAxis(3));
		float leftJoypadY = -limit(controller.getAxis(2)); //flip Y-axis value
//		float rightJoypadX = limit(controller.getAxis(1));
//		float rightJoypadY = limit(controller.getAxis(0));
//		System.out.println("3: " + leftJoypadX + "\n2: " + leftJoypadY + "\n1: " + rightJoypadX + "\n0: " + rightJoypadY);


		if (leftJoypadX == 0 && leftJoypadY == 0) {
			//keep everything in the same place
			inputState.stickMoved = false;
		}
		else {
			inputState.directionAngle = Direction.getDirection(leftJoypadX, leftJoypadY).degreeAngle;
			inputState.stickMoved = true;
		}
	}

	public static void moveCamera(OrthographicCamera camera, InputState inputState, GameState state, float delta) {
		if (inputState.keyboardKeyState.get(SPACE) == Boolean.TRUE) {
			EngineStaff.centerCamera(camera, state);
			return;
		}

		int cameraDirectionX = 0;
		int cameraDirectionY = 0;

		if (inputState.keyboardKeyState.get(W) == Boolean.TRUE) {
			cameraDirectionY = 1;
		}
		else if (inputState.keyboardKeyState.get(S) == Boolean.TRUE) {
			cameraDirectionY = -1;
		}

		if (inputState.keyboardKeyState.get(A) == Boolean.TRUE) {
			cameraDirectionX = -1;
		}
		else if (inputState.keyboardKeyState.get(D) == Boolean.TRUE) {
			cameraDirectionX = 1;
		}

		if (cameraDirectionX != 0 || cameraDirectionY != 0) {
			float step = state.cameraSpeed * delta;
			state.cameraX += cameraDirectionX * step;
			state.cameraY += cameraDirectionY * step;
			camera.position.set(state.cameraX, state.cameraY, 0);
			camera.update();
		}
	}

	public static void applyPlayerInput(InputState inputState, GameState state, float delta) {
		if (!inputState.stickMoved
				&& inputState.keyboardKeyState.get(LEFT) != Boolean.TRUE
				&& inputState.keyboardKeyState.get(RIGHT) != Boolean.TRUE
				&& inputState.keyboardKeyState.get(UP) != Boolean.TRUE
				&& inputState.keyboardKeyState.get(DOWN) != Boolean.TRUE) {
			//no input
			return;
		}

		float rotation = 0;
		if (inputState.stickMoved) {
			rotation = inputState.directionAngle;
		}
		else {
			float x = 0;
			if (inputState.keyboardKeyState.get(LEFT) == Boolean.TRUE) {
				x = -1;
			}
			else if (inputState.keyboardKeyState.get(RIGHT) == Boolean.TRUE) {
				x = 1;
			}

			float y = 0;
			if (inputState.keyboardKeyState.get(UP) == Boolean.TRUE) {
				y = 1;
			}
			else if (inputState.keyboardKeyState.get(DOWN) == Boolean.TRUE) {
				y = -1;
			}

			rotation = Direction.getDirection(x, y).degreeAngle;
		}

		float directionX = (float) Math.cos(Math.PI / 180 * rotation);
		float directionY = (float) Math.sin(Math.PI / 180 * rotation);

		float step = state.player.speed * delta;
		state.player.x += directionX * step;
		state.player.y += directionY * step;
		state.player.rotation = rotation;
	}

	public static void calculateWorldCollisions(GameState state) {
		CircleEntity targetCircle = state.targetCircle;

		float rightLimit = state.screenMaxWidth - targetCircle.radius;
		float leftLimit = targetCircle.radius;
		if (targetCircle.x >= rightLimit) {
			targetCircle.rotation = Angle.bounceFromVertical(targetCircle.rotation);

			targetCircle.x = rightLimit - (targetCircle.x - rightLimit);
		}
		else if (targetCircle.x<= leftLimit) {
			targetCircle.rotation = Angle.bounceFromVertical(targetCircle.rotation);

			targetCircle.x = leftLimit - (targetCircle.x - leftLimit);
		}

		float topLimit = state.screenMaxHeight - targetCircle.radius;
		float bottomLimit = targetCircle.radius;
		if (targetCircle.y >= topLimit) {
			targetCircle.rotation = Angle.bounceFromHorizontal(targetCircle.rotation);

			targetCircle.y = topLimit - (targetCircle.y - topLimit);
		}
		else if (targetCircle.y <= bottomLimit) {
			targetCircle.rotation = Angle.bounceFromHorizontal(targetCircle.rotation);

			targetCircle.y = bottomLimit - (targetCircle.y - bottomLimit);
		}
	}

	public static void randomizeColorMostlyBlue(Color color) {
		color.r = (float)Math.random()/10;
		color.g = (float)Math.random()/4+0.25f;
		color.b = (float)Math.random()/2+0.5f;
	}

	public static void calculatePlayerCollisions(GameState state) {
		CircleEntity player = state.player;

		//against borders
		if (state.player.x > state.screenMaxWidth - state.player.radius) {
			state.player.x = state.screenMaxWidth - state.player.radius;
		}
		else if (state.player.x < state.player.radius) {
			state.player.x = state.player.radius;
		}

		if (state.player.y > state.screenMaxHeight - state.player.radius) {
			state.player.y = state.screenMaxHeight - state.player.radius;
		}
		else if (state.player.y < state.player.radius) {
			state.player.y = state.player.radius;
		}

		// against tail
		for (Point point : state.tail) {
			float distanceToHarm = Line.distance(player.x, player.y, point.x, point.y);

			if (distanceToHarm < player.radius + state.targetCircle.radius * 3 / 4.0f) {
				state.pointsCounter -= 3;
				state.allowedTailLength -= 3;
				state.tail.removeFirst();
				state.tail.removeFirst();
				state.tail.removeFirst();
				state.randomizePlayerPosition();
				return;
			}
		}
		//against target
		float playerCircleDistance = Line.distance(player.x, player.y, state.targetCircle.x, state.targetCircle.y);
		if (playerCircleDistance <= player.radius + state.targetCircle.radius) {
			randomizeColorMostlyBlue(state.backgroundColor);
			state.randomizeTargetCircleState();
			state.tail.clear();
			state.gapCounter = 0;
			state.allowedTailLength += 3;
			state.pointsCounter += 3;
//				state.circleSpeed *= 1.05;
		}
	}

	public final int MAX_UPDATE_ITERATIONS = 3;
	public final float FIXED_TIMESTAMP = 1/60f;
	private float internalTimeTracker = 0;

	@Override
	public void render() {
		OrthographicCamera camera = engineStaff.camera;
		Controller controller = engineStaff.controller;
		ShapeRenderer shapeRenderer = engineStaff.shapeRenderer;
		SpriteBatch batch = engineStaff.batch;
		Sprite playerSprite = engineStaff.playerSprite;
		BitmapFont font = engineStaff.font;

		//input handling
		handleKeyboardInput(Gdx.input, inputState);
		checkExit(inputState);
		handleControllerInput(controller, inputState);

		//fixed-timestamp logic handling
		float delta = Gdx.graphics.getDeltaTime();
		internalTimeTracker += delta;
		int iterations = 0;

		while(internalTimeTracker > FIXED_TIMESTAMP && iterations < MAX_UPDATE_ITERATIONS) {
			//apply input
			moveCamera(camera, inputState, state, FIXED_TIMESTAMP);
			applyPlayerInput(inputState, state, FIXED_TIMESTAMP);

			//world state change
			calculateState(state, FIXED_TIMESTAMP);

			//collision detection
			calculateWorldCollisions(state);
			calculatePlayerCollisions(state);

			//time tracking logic
			internalTimeTracker -= FIXED_TIMESTAMP;
			iterations++;
		}

		//render
		Gdx.gl.glClearColor(state.backgroundColor.r, state.backgroundColor.g, state.backgroundColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		shapeRenderer.setProjectionMatrix(camera.combined);

		//tail
		for (Point point : state.tail) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(0.75f + state.backgroundColor.g, 0, 0, 1);
			shapeRenderer.circle(point.x, point.y, state.targetCircle.radius * 3 / 4.0f + 4);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(0.25f + state.backgroundColor.g, 0, 0, 1);
			shapeRenderer.circle(point.x, point.y, state.targetCircle.radius * 3 / 4.0f);
			shapeRenderer.end();
		}
		//target
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.25f+state.backgroundColor.g, 0.25f+state.backgroundColor.g, 0, 1);
		shapeRenderer.circle(state.targetCircle.x, state.targetCircle.y, state.targetCircle.radius);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0.25f+state.backgroundColor.g, 0, 1);
		shapeRenderer.circle(state.targetCircle.x, state.targetCircle.y, state.targetCircle.radius-4);
		shapeRenderer.end();


		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		CircleEntity player = state.player;
		playerSprite.setBounds(player.x-player.radius, player.y-player.radius, player.radius *2, player.radius *2);
		playerSprite.setOriginCenter();
		playerSprite.setRotation(player.rotation);
		playerSprite.draw(batch);

		font.draw(batch, "POINTS: "+state.pointsCounter, 20, state.screenMaxHeight -20);

		batch.end();
	}

	@Override
	public void dispose () {
		engineStaff.dispose();
	}

}
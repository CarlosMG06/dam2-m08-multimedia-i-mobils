package cmedina.websockets;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

public class Main extends ApplicationAdapter {
    private static final int IDLE_FRAME_COLS = 8, IDLE_FRAME_ROWS = 1;
    private static final int WALK_FRAME_COLS = 16, WALK_FRAME_ROWS = 8;
    private static final String WS_URL = "ws://localhost:8080/input";

    int screenWidth;
    int screenHeight;

    SpriteBatch spriteBatch;
    Animation<TextureRegion>[] walkAnimations;
    TextureRegion[] idleSprites;
    Texture walkSpriteSheet;
    Texture idleSpriteSheet;
    Sprite characterSprite;
    Texture backgroundTexture;
    Texture virtualJoystickOverlay;
    int idleCol;
    int walkRow;
    boolean isIdle;
    float stateTime;

    // WebSocket fields
    private WebSocket webSocket;
    private boolean wsConnected = false;
    private String lastSentInput = "";

    // Joystick virtual
    Rectangle upRect, downRect, leftRect, rightRect;
    final int IDLE=0, UP=1, DOWN=2, LEFT=3, RIGHT=4;

    @Override
    public void create() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        idleCol = 0;
        walkRow = 0;
        isIdle = true;

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        virtualJoystickOverlay = new Texture(Gdx.files.internal("virtual_joystick_overlay.png"));

        walkSpriteSheet = new Texture(Gdx.files.internal("BIS_Bowser_Overworld_Walk.png"));
        TextureRegion[][] tmp_walk = TextureRegion.split(walkSpriteSheet,
            walkSpriteSheet.getWidth() / WALK_FRAME_COLS,
            walkSpriteSheet.getHeight() / WALK_FRAME_ROWS);

        walkAnimations = new Animation[WALK_FRAME_ROWS];
        for (int i = 0; i < WALK_FRAME_ROWS; i++) {
            TextureRegion[] walkFrames = tmp_walk[i];
            walkAnimations[i] = new Animation<>(0.04f, walkFrames);
        }

        idleSpriteSheet = new Texture(Gdx.files.internal("BIS_Bowser_Overworld_Idle.png"));
        TextureRegion[][] tmp_idle = TextureRegion.split(idleSpriteSheet,
            idleSpriteSheet.getWidth() / IDLE_FRAME_COLS,
            idleSpriteSheet.getHeight() / IDLE_FRAME_ROWS);
        idleSprites = new TextureRegion[IDLE_FRAME_COLS * IDLE_FRAME_ROWS];
        int index_idle = 0;
        for (int i = 0; i < IDLE_FRAME_ROWS; i++) {
            for (int j = 0; j < IDLE_FRAME_COLS; j++) {
                idleSprites[index_idle++] = tmp_idle[i][j];
            }
        }

        spriteBatch = new SpriteBatch();
        stateTime = 0f;

        characterSprite = new Sprite(idleSprites[0]);
        characterSprite.setSize(64, 64);
        characterSprite.setOriginCenter();
        characterSprite.setPosition(screenWidth / 2f - 32, screenHeight / 2f - 32);

        setupTouchRects();

        connectWebSocket();
    }

    private void setupTouchRects() {
        float thirdHeight = screenHeight / 3f;
        float thirdWidth = screenWidth / 3f;

        // facilities per calcular el "touch"
        downRect = new Rectangle(0, 0, screenWidth, thirdHeight);
        upRect = new Rectangle(0, screenHeight - thirdHeight, screenWidth, thirdHeight);
        leftRect = new Rectangle(0, 0, thirdWidth, screenHeight);
        rightRect = new Rectangle(screenWidth - thirdWidth, 0, thirdWidth, screenHeight);
    }

    private void connectWebSocket() {
        webSocket = WebSockets.newSocket(WS_URL);
        webSocket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                wsConnected = true;
                Gdx.app.log("WebSocket", "Connected to " + WS_URL);
                return FULLY_HANDLED;
            }

            @Override
            public boolean onClose(WebSocket webSocket, WebSocketCloseCode closeCode, String reason) {
                wsConnected = false;
                Gdx.app.log("WebSocket", "Closed: " + reason);
                return FULLY_HANDLED;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                Gdx.app.log("WebSocket", "Message received: " + packet);
                return FULLY_HANDLED;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                return FULLY_HANDLED;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                wsConnected = false;
                Gdx.app.error("WebSocket", "Error: " + error.getMessage());
                return FULLY_HANDLED;
            }
        });
        webSocket.connect();
    }

    private void sendInput(String direction) {
        if (!wsConnected) return;
        // Only send if the input actually changed
        if (direction.equals(lastSentInput)) return;
        lastSentInput = direction;

        String message = "{\"direction\":\"" + direction + "\","
            + "\"x\":" + characterSprite.getX() + ","
            + "\"y\":" + characterSprite.getY() + "}";

        webSocket.send(message);
        Gdx.app.log("WebSocket", "Sent: " + message);
    }

    @Override
    public void render() {
//        if (screenWidth != Gdx.graphics.getWidth() || screenHeight != Gdx.graphics.getHeight()) {
//            screenWidth = Gdx.graphics.getWidth();
//            screenHeight = Gdx.graphics.getHeight();
//            setupTouchRects();
//        }

        input();
        animate();
        draw();
    }

    protected int virtual_joystick_control() {
        // iterar per multitouch
        // cada "i" és un possible "touch" d'un dit a la pantalla
        for (int i = 0; i < 10; i++) {
            if (Gdx.input.isTouched(i)) {
                float touchX = Gdx.input.getX(i);
                float touchY = Gdx.input.getY(i);

                if (touchY < screenHeight * 0.33f) return UP;
                if (touchY > screenHeight * 0.66f) return DOWN;
                if (touchX < screenWidth * 0.33f) return LEFT;
                if (touchX > screenWidth * 0.66f) return RIGHT;
            }
        }
        return IDLE;
    }

    private void input() {
        boolean up = false, down = false, left = false, right = false;
        String direction = "IDLE";

        // Control teclat
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) up = true;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) down = true;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) left = true;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) right = true;

        // Control tàctil (joystick virtual)
        if (!up && !down && !left && !right) {
            int touchControl = virtual_joystick_control();
            switch (touchControl) {
                case UP: up = true; break;
                case DOWN: down = true; break;
                case LEFT: left = true; break;
                case RIGHT: right = true; break;
                default: break;
            }
        }

        if (up && down)   { up = false;    down = false; }
        if (right && left){ right = false; left = false; }

        if (up || right || down || left) {
            isIdle = false;

            if      (up && !right && !left)   { walkRow = 0; direction = "UP"; }
            else if (up && right)             { walkRow = 1; direction = "UP_RIGHT"; }
            else if (right && !up && !down)   { walkRow = 2; direction = "RIGHT"; }
            else if (down && right)           { walkRow = 3; direction = "DOWN_RIGHT"; }
            else if (down && !right && !left) { walkRow = 4; direction = "DOWN"; }
            else if (down && left)            { walkRow = 5; direction = "DOWN_LEFT"; }
            else if (left && !up && !down)    { walkRow = 6; direction = "LEFT"; }
            else                              { walkRow = 7; direction = "UP_LEFT"; }

            sendInput(direction);
            move(direction);
        } else if (!isIdle) {
            isIdle = true;
            idleCol = walkRow;
            sendInput("IDLE");
        }
    }

    private void move(String direction) {
        float speed = 100f;
        float delta = Gdx.graphics.getDeltaTime();

        Vector2 movementVector = new Vector2(0, 0);
        if (direction.contains("UP"))    movementVector.y =  1;
        else if (direction.contains("DOWN"))  movementVector.y = -1;
        if (direction.contains("RIGHT")) movementVector.x =  1;
        else if (direction.contains("LEFT"))  movementVector.x = -1;
        movementVector.nor();
        characterSprite.translate(movementVector.x * speed * delta, movementVector.y * speed * delta);

        float x = Math.max(0, Math.min(screenWidth - characterSprite.getWidth(), characterSprite.getX()));
        float y = Math.max(0, Math.min(screenHeight - characterSprite.getHeight(), characterSprite.getY()));
        characterSprite.setPosition(x, y);
    }

    private void animate() {
        TextureRegion frame;
        if (!isIdle) {
            stateTime += Gdx.graphics.getDeltaTime();
            frame = walkAnimations[walkRow].getKeyFrame(stateTime, true);
        } else {
            stateTime = 0;
            frame = idleSprites[idleCol];
        }
        characterSprite.setRegion(frame);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        characterSprite.draw(spriteBatch);
        spriteBatch.draw(virtualJoystickOverlay, 0, 0, screenWidth, screenHeight);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        if (webSocket != null) webSocket.close();
        spriteBatch.dispose();
        walkSpriteSheet.dispose();
        idleSpriteSheet.dispose();
        backgroundTexture.dispose();
        virtualJoystickOverlay.dispose();
    }
}

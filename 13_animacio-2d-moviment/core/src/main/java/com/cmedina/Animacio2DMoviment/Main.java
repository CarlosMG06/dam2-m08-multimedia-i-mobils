package com.cmedina.Animacio2DMoviment;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final int IDLE_FRAME_COLS = 8, IDLE_FRAME_ROWS = 1;
    private static final int WALK_FRAME_COLS = 16, WALK_FRAME_ROWS = 8;
    SpriteBatch spriteBatch;
    Animation<TextureRegion>[] walkAnimations;
    TextureRegion[] idleSprites;
    Texture walkSpriteSheet;
    Texture idleSpriteSheet;
    Sprite characterSprite;
    int idleCol;
    int walkRow;
    boolean isIdle;

    // A variable for tracking elapsed time for the animation
    float stateTime;

    @Override
    public void create() {
        idleCol = 0;
        walkRow = 0;
        isIdle = true;

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

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        spriteBatch = new SpriteBatch();
        stateTime = 0f;

        characterSprite = new Sprite(idleSprites[0]);
        characterSprite.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }

    @Override
    public void render() {
        input();
        animate();
        draw();
    }

    private void input() {
        float speed = 100f;
        float delta = Gdx.graphics.getDeltaTime();

        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);

        if (up && down) {
            up = false;
            down = false;
        } if (right && left) {
            right = false;
            left = false;
        }

        if (up || right || down || left) {
            isIdle = false;
            if (up && !right && !left) walkRow = 0;
            else if (up && right) walkRow = 1;
            else if (right && !up && !down) walkRow = 2;
            else if (down && right) walkRow = 3;
            else if (down && !right && !left) walkRow = 4;
            else if (down && left) walkRow = 5;
            else if (left && !up && !down) walkRow = 6;
            else if (up && left) walkRow = 7;

            Vector2 movementVector = new Vector2(0, 0);
            if (up) movementVector.y = 1;
            else if (down) movementVector.y = -1;
            if (right) movementVector.x = 1;
            else if (left) movementVector.x = -1;
            movementVector.nor();
            characterSprite.translate(movementVector.x * speed * delta, movementVector.y * speed * delta);
        } else {
            if (!isIdle) {
                isIdle = true;
                idleCol = walkRow;
            }
        }
    }

    private void animate() {
        TextureRegion frame;
        if (!isIdle) {
            stateTime += Gdx.graphics.getDeltaTime();
            Animation<TextureRegion> walkAnim = walkAnimations[walkRow];
            frame = walkAnim.getKeyFrame(stateTime, true);
        } else {
            stateTime = 0;
            frame = idleSprites[idleCol];
        }
        characterSprite.setRegion(frame);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        spriteBatch.begin();
        characterSprite.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        walkSpriteSheet.dispose();
        idleSpriteSheet.dispose();
    }
}

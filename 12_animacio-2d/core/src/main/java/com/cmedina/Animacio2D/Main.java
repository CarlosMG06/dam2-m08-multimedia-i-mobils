package com.cmedina.Animacio2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    // Constant rows and columns of the sprite sheet
    private static final int IDLE_FRAME_COLS = 19, IDLE_FRAME_ROWS = 2;
    private static final int WALK_FRAME_COLS = 6, WALK_FRAME_ROWS = 5;

    // Objects used
    Animation<TextureRegion> idleAnimation; // Must declare frame type (TextureRegion)
    Texture idleSpriteSheet;
    Animation<TextureRegion> walkAnimation;
    Texture walkSpriteSheet;
    SpriteBatch spriteBatch;

    // A variable for tracking elapsed time for the animation
    float stateTime;

    @Override
    public void create() {

        // Load the sprite sheet as a Texture
        walkSpriteSheet = new Texture(Gdx.files.internal("animation_sheet.gif"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp_walk = TextureRegion.split(walkSpriteSheet,
            walkSpriteSheet.getWidth() / WALK_FRAME_COLS,
            walkSpriteSheet.getHeight() / WALK_FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[WALK_FRAME_COLS * WALK_FRAME_ROWS];
        int index_walk = 0;
        for (int i = 0; i < WALK_FRAME_ROWS; i++) {
            for (int j = 0; j < WALK_FRAME_COLS; j++) {
                walkFrames[index_walk++] = tmp_walk[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);


        // ------ spritesheet personalitzada
        idleSpriteSheet = new Texture(Gdx.files.internal("BIS_Mario_Battle_Idle_2x.png"));
        TextureRegion[][] tmp_idle = TextureRegion.split(idleSpriteSheet,
            idleSpriteSheet.getWidth() / IDLE_FRAME_COLS,
            idleSpriteSheet.getHeight() / IDLE_FRAME_ROWS);
        TextureRegion[] idleFrames = new TextureRegion[IDLE_FRAME_COLS * IDLE_FRAME_ROWS];
        int index_idle = 0;
        for (int i = 0; i < IDLE_FRAME_ROWS; i++) {
            for (int j = 0; j < IDLE_FRAME_COLS; j++) {
                idleFrames[index_idle++] = tmp_idle[i][j];
            }
        }
        idleAnimation = new Animation<TextureRegion>(0.0625f, idleFrames);
        // ------

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        // Get current frame of animation for the current stateTime
        TextureRegion walkCurrentFrame = walkAnimation.getKeyFrame(stateTime, true);
        // Frame d'animació personalitzada
        TextureRegion idleCurrentFrame = idleAnimation.getKeyFrame(stateTime, true);

        spriteBatch.begin();
        spriteBatch.draw(walkCurrentFrame, 50, 50); // Draw current frame at (50, 50)
        spriteBatch.draw(idleCurrentFrame, 200, 50); // Dibuixar a (200, 50)
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        idleSpriteSheet.dispose();
    }
}

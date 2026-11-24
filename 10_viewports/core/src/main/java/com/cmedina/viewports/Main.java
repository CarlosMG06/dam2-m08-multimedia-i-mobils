package com.cmedina.viewports;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
// Exemple amb ApplicationAdapter, però podria ser una Screen
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    public FitViewport viewport;

    // Texture és l'objecte adequat per a pintar al batch
    // es pot crear a partir d'imatges o de formes dibuixades
    Texture pilotaTexture;

    // les dades mínimes son posició i velocitat de la pilota
    float posx, posy, velx, vely;
    int pilotaWidth, pilotaHeight, pilotaWidthPx, pilotaHeightPx;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5); // 8x5 unitats (amplada x alçada)

        // Creem la Texture de la pilota (format en pixels! , de moment)
        pilotaWidthPx = pilotaHeightPx = 100;
        Pixmap pilotaPixmap = new Pixmap(pilotaWidthPx,pilotaHeightPx,Pixmap.Format.RGBA8888);
        pilotaPixmap.setColor(Color.RED);
        pilotaPixmap.fillCircle(50,50,50); // posx, posy, radi
        pilotaTexture = new Texture(pilotaPixmap);
        pilotaWidth = pilotaWidthPx/100;
        pilotaHeight = pilotaHeightPx/100;

        posx = posy = 0f;
        velx = vely = 1f; // velocitat d'1 m/s
    }

    @Override
    public void render() {
        // 1- INPUTS (controls usuari)...

        // 2- CALCULEM
        float delta = Gdx.graphics.getDeltaTime();
        posx = posx + velx * delta;
        // ex1 (vely)
        posy = posy + vely * delta;
        // ex2 i ex3 (rebots)
        if (posx + pilotaWidth >= viewport.getWorldWidth() || posx < 0) {
            velx = -velx;
        }
        if (posy + pilotaHeight >= viewport.getWorldHeight() || posy < 0) {
            vely = -vely;
        }


        // 3- PINTEM
        // pintem background de color
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // PUNT CLAU: ajustem càmera per traduir resolucions quan pintem
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(pilotaTexture,posx,posy,1f,1f); // tamany pilota = 1x1
        batch.end();
    }

    // SUPER IMPORTANT, sense resize no funciona el Viewport
    // és un error típic oblidar aquest mètode, esteu avisats
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

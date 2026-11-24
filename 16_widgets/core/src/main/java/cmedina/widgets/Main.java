package cmedina.widgets;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private FitViewport viewport;
    private Table taskList;

    @Override
    public void create() {
        viewport = new FitViewport(480, 800);
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("La meva Tasklist", skin);
        final TextField inputTask = new TextField("", skin);
        inputTask.setMessageText("Escriu una nova tasca..."); // Placeholder
        TextButton addButton = new TextButton("Afegir", skin);

        taskList = new Table();
        taskList.top();
        ScrollPane scrollPane = new ScrollPane(taskList, skin);

        table.add(title).colspan(2).pad(10).row();
        table.add(inputTask).width(300).pad(10);
        table.add(addButton).pad(10).row();
        table.add(scrollPane).colspan(2).expand().fill().pad(10);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String taskText = inputTask.getText().trim();
                if (!taskText.isEmpty()) {
                    addTask(taskText);
                    inputTask.setText("");
                    stage.setKeyboardFocus(null);
                }
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    private void addTask(String text) {
        final Table TaskRow = new Table();

        Label taskLabel = new Label("- " + text, skin);
        TextButton deleteButton = new TextButton("X", skin);
        TaskRow.add(taskLabel).expandX().left().pad(5);
        TaskRow.add(deleteButton).pad(5);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TaskRow.remove();
            }
        });
        taskList.add(TaskRow).expandX().fillX().row();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}

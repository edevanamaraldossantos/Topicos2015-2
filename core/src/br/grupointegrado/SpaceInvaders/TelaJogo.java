package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by Edevan on 03/08/2015.
 */
public class TelaJogo extends TelaBase {
    /**
     * Essa é uma camera 2D
     */
    private OrthographicCamera camera;

    /**
     * Ele desenha a imagem
     */
    private SpriteBatch batch;

    private Stage palco;

    private BitmapFont fonte;
    private Label lbpontuoacao;
    /**
     * Construtor padrão da tela de jogo
     * @param game Referência para a classe Principal
     */
    public TelaJogo(MainGame game){
        super(game);
    }

    /**
     * Chamado quando a tela é exibida
     */
    @Override
    public void show() {
      camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
      batch = new SpriteBatch();
      palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        initfonte();
        initInformacao();

    }

    private void initInformacao() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbpontuoacao = new Label(" 0 pontos", lbEstilo);
        palco.addActor(lbpontuoacao);
    }

    private void initfonte(){
        fonte = new BitmapFont();
    }

    /**
     * Chamado a todo quadro de atualização do jogo (FPS)
     * @param delta Tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbpontuoacao.setPosition(10,camera.viewportHeight - 20);

        palco.act(delta);
        palco.draw();
    }

    /**
     * É chamado sempre que há uma alteração no tamanho da tela
     * @param width novo valor de lagura da tela
     * @param height novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width, height);
        camera.update();
    }

    /**
     * É chamado sempre sempre que o jogo for minimizado
     *
     */
    @Override
    public void pause() {

    }

    /**
     * É chamado  sempre que o jogo volta para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * É chamado  quando  a tela é destruida
     */
    @Override
    public void dispose() {
     batch.dispose();
     palco.dispose();
     fonte.dispose();
    }
}

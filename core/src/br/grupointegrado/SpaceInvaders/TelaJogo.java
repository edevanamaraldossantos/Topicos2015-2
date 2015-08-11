package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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
    private Image jogador;
    private Texture textureJogadorDireita;
    private Texture textureJogadorEsquerda;
    private Texture textureJogador;
    private boolean indoDireita;
    private boolean indoEsquerda;
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
        initiJogador();

    }

    private void initiJogador() {

        textureJogador = new Texture("sprites/player.png");
        textureJogadorDireita = new Texture("sprites/player-right.png");
        textureJogadorEsquerda = new Texture("sprites/player-left.png");

        jogador = new Image(textureJogador);
        float x= camera.viewportHeight/2-jogador.getWidth()/2;
        float y= 20;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
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

        lbpontuoacao.setPosition(10, camera.viewportHeight - 20);
        capturaTeclas();
        atualizarJogador(delta);
        palco.act(delta);
        palco.draw();
    }

    private void atualizarJogador(float delta) {
     float velocidade = 200;//velocidade de movimento do jogador

        if(indoDireita){
            if( jogador.getX() < camera.viewportWidth-jogador.getWidth()) {
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if(indoEsquerda){
            if (jogador.getX() > 0) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if(indoDireita){
            //trocar imagem direita
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogadorDireita)));

        }else if (indoDireita){
            //trocar imagem esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogadorEsquerda)));
        }else {
            //trocar imagem centro
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogador)));

        }
    }

    /**
     * verifica se as tecla estao pressionadas
     */
    private void capturaTeclas() {
        indoDireita=false;
        indoEsquerda=false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
        }
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
     textureJogador.dispose();
     textureJogadorDireita.dispose();
     textureJogadorEsquerda.dispose();

    }
}

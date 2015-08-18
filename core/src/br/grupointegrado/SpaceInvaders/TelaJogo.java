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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
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
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture textureTiro;
    private Texture textureMeteoro1;
    private Texture textureMeteoro2;
    private Array<Image> meteoro1 = new Array<Image>();
    private Array<Image> meteoro2 = new Array<Image>();

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

        initTextura();
        initfonte();
        initInformacao();
        initiJogador();

    }

    private void initTextura() {
        textureTiro = new Texture("sprites/shot.png");
        textureMeteoro1 = new Texture("sprites/enemie-1.png");
        textureMeteoro2 = new Texture("sprites/enemie-2.png");

    }

   // instancia os objetos do jogadr e adiciona noa palco
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
        atualizartiros(delta);
        atualizarMeteoros(delta);

        // atualiza a situção do palco
        palco.act(delta);

        // desenha o palco na tela
        palco.draw();

    }

    private void atualizarMeteoros(float delta) {

        int tipo = MathUtils.random(1,3);

         if(tipo ==1){
        // criar meteoro 1
             Image meteoro  =new Image(textureMeteoro1);
             float x=MathUtils.random(0, camera.viewportHeight - meteoro.getMinWidth());
             float y=MathUtils.random(camera.viewportHeight, camera.viewportHeight*2);
             meteoro.setPosition(x,y);
             meteoro1.add(meteoro);
             palco.addActor(meteoro);
        }else{
        // criar meteoro 2
        }
        float velocidade = 200;
        for (Image  meteoro: meteoro1){
            float x= meteoro.getX();
            float y=meteoro.getY()- velocidade * delta;
            meteoro.setPosition(x,y);
        }
    }

    private  final float MIN_INTERVALO_TIROS =0.4f;//minimo de tempo entre os tiros
    private float intervaloTiros=0;// TEMPO ACUMULADO ENTRE TIROS

    private void atualizartiros(float delta){

        intervaloTiros= intervaloTiros+delta;// ACUMULA O TEMPO PERCORRIDO
         //CRIA UM NOVO TIRO SE NECESSARIO
        if (atirando) {

            if (intervaloTiros>= MIN_INTERVALO_TIROS) {
                Image tiro = new Image(textureTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth()/2;
                float y = jogador.getImageY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros=0;
            }
        }
        float velocidade = 200;// VELOCIDADE DE MOVIMENTO DO TIRO
        // PERCORRE TODOS OS TIROS EXISTENTES
        for (Image tiro : tiros){
            float x = tiro.getX();
            float y = tiro.getY()+velocidade * delta;
            tiro.setPosition(x,y);
            //remove os tiro que sairam da tela
            if (tiro.getImageY()> camera.viewportHeight){

                tiros.removeValue(tiro,true);
                tiro.remove();//remove do palco
            }
        }
    }

    /**
     * Atualiza o posiçao do jogador
     */

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

        }else if (indoEsquerda){
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
        atirando= false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            atirando=true;
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
     textureTiro.dispose();
     textureMeteoro1.dispose();
     textureMeteoro2.dispose();
    }
}

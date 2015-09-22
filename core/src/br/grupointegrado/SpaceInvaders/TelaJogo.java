package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.util.Vector;


/**
 * Created by Edevan on 03/08/2015.
 */
public class TelaJogo extends TelaBase {
    /**
     * Essa � uma camera 2D
     */
    private OrthographicCamera camera;

    /**
     * Ele desenha a imagem
     */
    private SpriteBatch batch;
    private Stage palco;
    private Stage PalcoInformacoes;
    private BitmapFont fonte;
    private Label lbpontuoacao;
    private Label lbgameOver;
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

    private Array<Texture>texturasExplosao= new Array<Texture>();
    private Array<Explosao>explosoes=new  Array<Explosao>();

    private Sound somTiros;
    private Sound somExplosao;
    private Sound somGAmeOver;
    private Music musicaFundo;

    /**
     * Construtor padr�o da tela de jogo
     * @param game Refer�ncia para a classe Principal
     */
    public TelaJogo(MainGame game){
        super(game);
    }

    /**
     * Chamado quando a tela � exibida
     */
    @Override
    public void show() {
      camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
      batch = new SpriteBatch();
      palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        PalcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initiSons();
        initTextura();
        initfonte();
        initInformacao();
        initiJogador();

    }



    private void initiSons() { //Declarando som do tiro e da explosao
        somTiros = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGAmeOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);

    }

    private void initTextura() {
        textureTiro = new Texture("sprites/shot.png");
        textureMeteoro1 = new Texture("sprites/enemie-1.png");
        textureMeteoro2 = new Texture("sprites/enemie-2.png");

        for (int i=1; i<=17; i++){
            Texture text = new Texture("sprites/explosion-"+i+".png");
            texturasExplosao.add(text);

        }

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
        PalcoInformacoes.addActor(lbpontuoacao);

        lbgameOver = new Label(" Game Over", lbEstilo);
        lbgameOver.setVisible(false);
        PalcoInformacoes.addActor(lbgameOver);

    }


    private void initfonte(){
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        param.shadowColor = Color.BLUE;

        fonte = generator.generateFont(param);
        generator.dispose();
    }

    /**
     * Chamado a todo quadro de atualiza��o do jogo (FPS)
     * @param delta Tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbpontuoacao.setPosition(10, camera.viewportHeight - lbpontuoacao.getPrefHeight() - 20);
        lbpontuoacao.setText(pontuacao + " pontos ");


        lbgameOver.setPosition(camera.viewportWidth / 2 - lbgameOver.getPrefWidth() / 2,
                camera.viewportHeight / 2);

        lbgameOver.setVisible(gameOver == true);
        atualizarExplosoes(delta);

        if (gameOver==false) {
            if (!musicaFundo.isPlaying())
                musicaFundo.play();

            capturaTeclas();
            atualizarJogador(delta);
            atualizartiros(delta);
            atualizarMeteoros(delta);
            detectarColisoes(meteoro1, 5);
            detectarColisoes(meteoro2, 15);
        }else{
            if(musicaFundo.isPlaying())
                musicaFundo.stop();
            reiniciarJogo();
        }
        // atualiza a situ��o do palco
        palco.act(delta);

        // desenha o palco na tela
        palco.draw();
        PalcoInformacoes.act(delta);
        PalcoInformacoes.draw();

    }

    /**
     * verifica se o usuario pressionou Enter para reiniciar o jogo
     */

    private void reiniciarJogo() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            // recupera o obejto de preferencias
            Preferences preferences = Gdx.app.getPreferences("SpaceInvaders");
            int pontuacaoMaxima = preferences.getInteger("pontuacaoMaxima");
            //Verifica se minha nova Pontuação é maoir que pontuação maxima
            if (pontuacao > pontuacaoMaxima){
                preferences.putInteger("pontuacaoMaxima",pontuacao);
                preferences.flush();
            }
            game.setScreen(new TelaJogo(game));
        }
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes) {
            // verifica se a aplica��o
            if (explosao.getEstagio() >= 16) {
                explosoes.removeValue(explosao, true);//remove a explosao do array
                explosao.getAtor().remove();// remove o ator do palco

            } else {
                explosao.atualizar(delta);

            }
        }
    }
    private Rectangle recJogador= new Rectangle();
    private Rectangle recTiro= new Rectangle();
    private Rectangle recMeteoro= new Rectangle();
    private int pontuacao=0;
    private boolean gameOver=false;

    private void detectarColisoes(Array<Image>meteoros,int valePonto) {
        recJogador.set(jogador.getX(),jogador.getImageY(),jogador.getWidth(),jogador.getImageHeight());

        for (Image meteoro: meteoros){
            recMeteoro.set(meteoro.getX(),meteoro.getY(),meteoro.getWidth(),meteoro.getHeight());
            //decta colisao com os tiro
            for(Image tiro:tiros){
               recTiro.set(tiro.getX(),tiro.getY(),tiro.getWidth(),tiro.getHeight());

               if (recMeteoro.overlaps(recTiro)){
                   //aqui ocorre uma colis�o do tiro com o meteoro1
                   pontuacao+=5;
                   tiro.remove();//remove do palco
                   tiros.removeValue(tiro, true);
                   meteoro.remove();
                   meteoros.removeValue(meteoro, true);
                   criarExplosao(meteoro.getX()+ meteoro.getWidth()/2,meteoro.getY()+meteoro.getHeight()/2);
               }

            }
            //detecta  colisao com o player
            if(recJogador.overlaps(recMeteoro)){
            //ocorreu colisao do jogador com o meteoro
             gameOver=true;
                somGAmeOver.play();
            }
        }
    }
     // cria a explos�o na posi��o x e y

    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth()/2, y - ator.getHeight()/2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosoes.add(explosao);
    }

    private void atualizarMeteoros(float delta) {
        int qtMeteoros = meteoro1.size+meteoro2.size;//retorna a qtd meteoros criados

        if (qtMeteoros < 10) {
            int tipo = MathUtils.random(1, 4);//retorna 1ou 2 aleatoriamente
            if (tipo == 1) {
                // criar meteoro 1
                Image meteoro = new Image(textureMeteoro1);
                float x = MathUtils.random(0, camera.viewportHeight - meteoro.getMinWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro1.add(meteoro);
                palco.addActor(meteoro);
            } else if(tipo==2) {
                // criar meteoro 2
                Image meteoro = new Image(textureMeteoro2);
                float x = MathUtils.random(0, camera.viewportHeight - meteoro.getMinWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro2.add(meteoro);
                palco.addActor(meteoro);
            }
        }

            float velocidade1 = 90;//90 pixes por segundo
            for (Image meteoro : meteoro1) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade1 * delta;
                meteoro.setPosition(x, y);

                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove();
                    meteoro1.removeValue(meteoro, true);//remove da lista
                    pontuacao=pontuacao-30;
                }
            }

            float velocidade2 = 130;//130 pixes por segundo
            for (Image meteoro : meteoro2) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade2 * delta;
                meteoro.setPosition(x, y);

                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove();
                    meteoro2.removeValue(meteoro, true);//remove da lista
                    pontuacao=pontuacao -60;
                }
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
                somTiros.play();
            }
        }
        float velocidade = 350;// VELOCIDADE DE MOVIMENTO DO TIRO
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
     * Atualiza o posi�ao do jogador
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

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)|| clicouEsquerda()){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)|| clicouDireita()){
            indoDireita = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.app.getType() == Application.ApplicationType.Android){
            atirando=true;
        }
    }

    private boolean clicouEsquerda() {
        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();

            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getX();

            posicao = camera.unproject(posicao);
            float meio = camera.viewportHeight / 2;
            if (posicao.x < meio) {
                return true;
            }

        }
        return false;
    }
    private boolean clicouDireita() {
        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();

            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getX();

            posicao = camera.unproject(posicao);
            float meio = camera.viewportHeight / 2;
            if (posicao.x > meio) {
                return true;
            }


        }
        return false;
    }
    /**
     * � chamado sempre que h� uma altera��o no tamanho da tela
     * @param width novo valor de lagura da tela
     * @param height novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width, height);
        camera.update();
    }

    /**
     * � chamado sempre sempre que o jogo for minimizado
     *
     */
    @Override
    public void pause() {

    }

    /**
     * � chamado  sempre que o jogo volta para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * � chamado  quando  a tela � destruida
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

        for(Texture text: texturasExplosao){
            text.dispose();
        }
        PalcoInformacoes.dispose();
        somTiros.dispose();
        somExplosao.dispose();
        somGAmeOver.dispose();
    }
}

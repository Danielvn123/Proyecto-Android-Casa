package com.dani.mijuego.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public final class Assets {

    public final AssetManager manager = new AssetManager();

    //FONDOS
    public static final String FONDO_TRABAJO    = "fondos/fondotrabajo.png";
    public static final String RUINAS           = "fondos/ruinas.png";
    public static final String FONDO_NARANJA       = "fondos/fondonaranja.png";
    public static final String FONDO_AZUL       = "fondos/fondoazul.png";
    public static final String FONDO_LILA = "fondos/fondolila.png";
    public static final String FONDO_AMARILLO = "fondos/fondoamarillo.png";
    public static final String FONDO_MENU       = "fondos/fondomenu.png";
    public static final String NUBES = "fondos/nubes.png";
    public static final String ESTRELLAS = "fondos/estrellas.png";
    public static final String GAMEOVER   = "fondos/gameover.png";
    public static final String VICTORY   = "fondos/victory.png";


    //PLATAFORMAS
    public static final String PLAT_RUINAS      = "plataformas/plataformaruinas.png";
    public static final String PLAT_MEDIA       = "plataformas/plataformamedia.png";
    public static final String PLAT_MODERNA     = "plataformas/plataformamoderna.png";
    public static final String PLAT_ROTA        = "plataformas/plataformaruinasrota.png";
    public static final String PLAT_MEDIAROTA   = "plataformas/plataformamediarota.png";
    public static final String PLAT_COLORES   = "plataformas/plataformacolores.png";
    public static final String PLAT_COLORES_ROTA = "plataformas/plataformacoloresrota.png";


    //PERSONAJE
    public static final String PLAYER_IDLE = "personaje/personaje.png";
    public static final String PLAYER_IZQ  = "personaje/personajeizquierda.png";
    public static final String PLAYER_DER  = "personaje/personajederecha.png";
    public static final String PLAYER_ESCUDO_IDLE = "personaje/personajeescudo.png";
    public static final String PLAYER_ESCUDO_IZQ  = "personaje/personajeescudoizquierda.png";
    public static final String PLAYER_ESCUDO_DER  = "personaje/personajeescudoderecha.png";
    public static final String PLAYER_SETA_IDLE = "personaje/personajeseta.png";
    public static final String PLAYER_SETA_IZQ  = "personaje/personajesetaizquierda.png";
    public static final String PLAYER_SETA_DER  = "personaje/personajesetaderecha.png";


    //BICHOS
    public static final String BICHOAZUL    = "bichos/Bichoazul.png";
    public static final String BICHOLILA    = "bichos/BichoLila.png";
    public static final String BICHOVERDE   = "bichos/BichoVerde.png";
    public static final String BICHOROJO   = "bichos/BichoRojo.png";

    //BOTONES
    public static final String BOTONMENU1 = "botones/botonmenu1.png";
    public static final String BOTONMENU2 = "botones/botonmenu2.png";
    public static final String BOTONMENU3 = "botones/botonmenu3.png";
    public static final String BOTONMENU4 = "botones/botonmenu4.png";
    public static final String BTN_PAUSE  = "botones/pause.png";
    public static final String BTN_ON  = "botones/botonon.png";
    public static final String BTN_OFF  = "botones/botonoff.png";

    //OBJETOS
    public static final String MONEDA     = "objetos/moneda.png";
    public static final String ZAPATOS     = "objetos/tenis.png";
    public static final String ESCUDO     = "objetos/escudo.png";
    public static final String SETA     = "objetos/seta.png";
    public static final String BANDERA     = "objetos/bandera.png";

    public void queue() {
        //FONDOS
        loadTex(FONDO_TRABAJO);
        loadTex(RUINAS);
        loadTex(FONDO_NARANJA);
        loadTex(FONDO_AZUL);
        loadTex(FONDO_LILA);
        loadTex(FONDO_AMARILLO);
        loadTex(FONDO_MENU);
        loadTex(NUBES);
        loadTex(ESTRELLAS);
        loadTex(GAMEOVER);
        loadTex(VICTORY);


        //PLATAFORMAS
        loadTex(PLAT_RUINAS);
        loadTex(PLAT_MEDIA);
        loadTex(PLAT_MODERNA);
        loadTex(PLAT_ROTA);
        loadTex(PLAT_MEDIAROTA);
        loadTex(PLAT_COLORES);
        loadTex(PLAT_COLORES_ROTA);


        //PERSONAJE
        loadTex(PLAYER_IDLE);
        loadTex(PLAYER_IZQ);
        loadTex(PLAYER_DER);
        loadTex(PLAYER_ESCUDO_IDLE);
        loadTex(PLAYER_ESCUDO_DER);
        loadTex(PLAYER_ESCUDO_IZQ);
        loadTex(PLAYER_SETA_IDLE);
        loadTex(PLAYER_SETA_IZQ);
        loadTex(PLAYER_SETA_DER);

        //BICHOS
        loadTex(BICHOAZUL);
        loadTex(BICHOLILA);
        loadTex(BICHOVERDE);
        loadTex(BICHOROJO);

        //BOTONES
        loadTex(BOTONMENU1);
        loadTex(BOTONMENU2);
        loadTex(BOTONMENU3);
        loadTex(BOTONMENU4);
        loadTex(BTN_PAUSE);
        loadTex(BTN_ON);
        loadTex(BTN_OFF);

        //OBJETOS
        loadTex(MONEDA);
        loadTex(ZAPATOS);
        loadTex(ESCUDO);
        loadTex(SETA);
        loadTex(BANDERA);
    }

    private void loadTex(String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("ASSETS", "NO EXISTE: " + path);
            return;
        }
        manager.load(path, Texture.class);
    }

    public boolean update() {
        return manager.update();
    }

    public float progress() {
        return manager.getProgress();
    }

    public void dispose() {
        manager.dispose();
    }
}

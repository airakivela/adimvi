package com.application.adimviandroid.utils;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.ExploreModel;
import com.application.adimviandroid.models.GuideModel;
import com.application.adimviandroid.models.InAppFeatureModel;
import com.application.adimviandroid.models.InAppProductModel;
import com.application.adimviandroid.models.OnboardingModel;
import com.application.adimviandroid.models.RoomBGModel;
import com.application.adimviandroid.models.RoomModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConstant {
    static final public int SHOW_BANNER_TIME = 3000;
    static final public String LOADING = "Cargando...";
    static final public String INTERNET_ERROR = "Error de Internet";
    static final public String SERVER_ERROR = "Error del Servidor";
    static final public String PERMISSION_DENIED = "Permiso denegado";
    static final public String NATIVE_AD_ID = "ca-app-pub-6706097279662517/8446582331";
    static final public String INTERSTITIAL_AD_ID = "ca-app-pub-6706097279662517/5848339150";
    public static final int IMAGE_WIDTH = 2000;
    public static final int IMAGE_HEIGHT = 2000;

    static final public List<OnboardingModel> ONBOARDINLIST = new ArrayList<>(
            Arrays.asList(new OnboardingModel(R.drawable.onboarding1, R.string.onBoardingTitle1, R.string.onBoardingContent1),
                new OnboardingModel(R.drawable.onboarding2, R.string.onBoardingTitle2, R.string.onBoardingContent2),
                new OnboardingModel(R.drawable.onboarding3, R.string.onBoardingTitle3, R.string.onBoardingContent3)
            )
    );

    static final public List<ExploreModel> EXPLORELIST = new ArrayList<>(
            Arrays.asList(new ExploreModel("Tendencias", "Descubre novedades y las publicaciones más populares que están circulando por toda la comunidad.", R.drawable.explor1),
                new ExploreModel("Recientes", "Entérate de lo último que se ha publicado y mantente siempre al día de lo que está pasando a tu alrededor.", R.drawable.explor2),
                new ExploreModel("Más votados", "¿Te gusta o no te gusta? Sumérgete entre las publicaciones que más cariño y likes han conseguido.", R.drawable.explor3),
                new ExploreModel("Más visitados", "Explora entre los posts que más interés y views han generado entre personas de todo el mundo.", R.drawable.explor4),
                new ExploreModel("Más comentados", "Hay publicaciones que han dado mucho de qué hablar últimamente, ¿te animas a comentar tú también?", R.drawable.explor5)
            )
    );

    static final public List<String> PROFILETABS = new ArrayList<>(
            Arrays.asList("Perfil", "Muro", "Posts", "Seguidores", "Siguiendo")
    );

    static final public List<String> FOLLOWTABS = new ArrayList<>(
            Arrays.asList("Publicaciones", "Muros", "Etiquetas")
    );

    static final public List<String> MENU_MORE = new ArrayList<>(
            Arrays.asList("Mi cuenta", "Mensajes", "Mis favoritos", "Notas", "Cerrar sesión")
    );

    static final public List<InAppProductModel> INAPPPURCASE = new ArrayList<>(
            Arrays.asList(new InAppProductModel("bdc548", "5.49"), new InAppProductModel("bdc998", "9.99"),
                    new InAppProductModel("bcc2498", "24.99"), new InAppProductModel("bdc4998", "49.99"))
    );

    static final public List<InAppFeatureModel> INAPPFEATURES = new ArrayList<>(
            Arrays.asList(new InAppFeatureModel("pd1d099", "Día", "0.99€", "Básico", "1"),
                    new InAppFeatureModel("pd2d199", "Días", "1.99€", "Estándar", "2"),
                    new InAppFeatureModel("pd5d499", "Días", "4.99€", "Premium", "5"),
                    new InAppFeatureModel("pd7d699", "Días", "6.99€", "Platino", "7"),
                    new InAppFeatureModel("pd14d1399", "Días", "13.99€", "Oro", "14"))
    );

    static final public List<RoomBGModel> GROUPBG = new ArrayList<>(
            Arrays.asList(new RoomBGModel(R.drawable.background_1, true),
                    new RoomBGModel(R.drawable.background_2, false),
                    new RoomBGModel(R.drawable.background_3, false),
                    new RoomBGModel(R.drawable.background_4, false),
                    new RoomBGModel(R.drawable.bacground_5, false),
                    new RoomBGModel(R.drawable.background_6, false)
            )
    );

    static final public List<GuideModel> GUDIES = new ArrayList<>(
            Arrays.asList(new GuideModel(R.drawable.guide_0, "¡Bienvenid@ a Adimvi!", "¿Tu primera vez por aquí? Esta guía rápida te ayudará a descubrir algunos aspectos importantes de la plataforma.", ""),
                    new GuideModel(R.drawable.guide_1, "La comunidad", "Adimvi cuenta con una comunidad increíble de lectores y escritores con los que podrás compartir todo tipo de ideas y opiniones mediante posts, chats en vivo, muros o mensajes entre otros.", ""),
                    new GuideModel(R.drawable.guide_2, "Todo lo que publiques está monetizado", "Todos tus posts te generarán ingresos desde el primer momento. Sin registros externos, sin más configuraciones ni complicaciones.", "Fácil y rápido."),
                    new GuideModel(R.drawable.guide_3, "Los créditos", "En Adimvi los escritores tienen la opción de compartir sus posts de manera gratuita o de pago. Los créditos te ayudarán a desbloquear estos posts de pago para acceder a su contenido.", "Puedes añadir créditos fácilmente desde tu perfil."),
                    new GuideModel(R.drawable.guide_4, "¡Ya estaría!", "Con esto estás list@ para sumergirte y descubrir lo mejor del blogging con Adimvi.", "Gracias por formar parte de esta gran comunidad.")
            )

    );
}

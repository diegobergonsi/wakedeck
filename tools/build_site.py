#!/usr/bin/env python3
"""Builds the wakedeck.app pages in docs/ for every language. No dependencies, no scripts in the output.

Run from the repo root: python3 tools/build_site.py
Languages: the folder name (empty for English) maps to the html lang code and the native name shown in the picker.
"""
import hashlib
import html
import pathlib
import re
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parent.parent / "docs"
SITE = "https://wakedeck.app"
KOFI = "https://ko-fi.com/diegobergonsi"
UPDATED = "2026-10-11"
# Changes with the stylesheet, so browsers do not keep serving an old copy.
CSS_VERSION = hashlib.sha1((ROOT / "assets" / "site.css").read_bytes()).hexdigest()[:8]

LANGS = [
    ("", "en", "English"),
    ("pt-br", "pt-BR", "Português (Brasil)"),
    ("pt-pt", "pt-PT", "Português (Portugal)"),
    ("fr", "fr", "Français"),
    ("de", "de", "Deutsch"),
    ("es", "es", "Español"),
    ("zh", "zh-Hans", "简体中文"),
    ("ja", "ja", "日本語"),
]

# ---------------------------------------------------------------- landing page text
INDEX = {
    "en": dict(
        title="WakeDeck: Wake-on-LAN widgets for Android",
        desc="Wake your PCs from your Android home screen. Customizable widgets, shortcuts and Quick Settings tiles. No ads, no analytics, no accounts.",
        ogdesc="Wake your devices from your smartphone. Customizable home-screen widgets. No ads, no analytics, no accounts.",
        skip="Skip to content", language="Language", features="Features", how="How it works", privacy="Privacy", contact="Contact",
        h1=("Wake your devices from your ", "smartphone", ""),
        lead="WakeDeck turns on your PCs with a Wake-on-LAN magic packet, straight from widgets you design yourself. One tap on your home screen, and the app does not even open.",
        play="Google Play Store: coming soon", soon="Free for Android 8.0 and newer. No ads, no analytics, no accounts.",
        feat_h="Built around the widget", feat_sub="Your home screen is the remote control. Pick how it looks and where it sits.",
        cards=[
            ("Widgets you design", "A single device, a list or a grid. Choose colors, transparency, corner radius, border, icon, text size and layout. Online and offline status updates in the background."),
            ("One tap, no app", "Tap Wake in the widget itself. You can also wake from launcher shortcuts and Quick Settings tiles."),
            ("Know what is on", "See which devices are online. Refresh them all with a button, or pull the list down."),
            ("Groups and Wake all", "Put devices in groups, such as Office or Lab, and wake a whole group with one button."),
            ("Remote shutdown over SSH", "Optional. WakeDeck signs in with a key kept in the Android Keystore. It never asks for or stores a password, and it pins each PC's host key."),
            ("Private by design", "No ads, no analytics, no accounts. Your devices and settings stay on your phone, and the app only talks to the addresses you add."),
        ],
        how_h="Three steps", how_sub="An in-app guide walks you through the PC side, for Windows and Linux.",
        steps=[
            ("Turn on Wake-on-LAN", "Enable it in your PC's BIOS or UEFI and in the network card settings. Wired Ethernet works best."),
            ("Add your device", "Enter a name and the MAC address. Add the IP address if you want to see its online status."),
            ("Add a widget", "Long-press your home screen, add a WakeDeck widget, and style it your way."),
        ],
        priv_h="Your data stays yours", priv_p="WakeDeck has no server and no account. It does not collect or share personal data. Read the short privacy policy to see exactly what is stored on your phone.", priv_btn="Privacy policy",
        away_h="Away from home?", away_p="Use your own VPN and set the device's broadcast address to your subnet's, for example 192.168.1.255. Your VPN and router must forward it. The in-app help explains the details and the common error messages.",
    ),
    "pt-br": dict(
        title="WakeDeck: widgets Wake-on-LAN para Android",
        desc="Ligue seus PCs pela tela inicial do Android. Widgets personalizáveis, atalhos e botões de Configurações rápidas. Sem anúncios, sem análise de uso, sem contas.",
        ogdesc="Ligue seus dispositivos pelo celular. Widgets personalizáveis na tela inicial. Sem anúncios, sem análise de uso, sem contas.",
        skip="Ir para o conteúdo", language="Idioma", features="Recursos", how="Como funciona", privacy="Privacidade", contact="Contato",
        h1=("Ligue seus dispositivos pelo ", "celular", ""),
        lead="O WakeDeck liga seus PCs com um pacote mágico Wake-on-LAN, direto de widgets que você mesmo desenha. Um toque na tela inicial e o app nem precisa abrir.",
        play="Google Play Store: em breve", soon="Grátis para Android 8.0 ou superior. Sem anúncios, sem análise de uso, sem contas.",
        feat_h="Feito em torno do widget", feat_sub="A tela inicial é o seu controle remoto. Escolha como ele fica e onde fica.",
        cards=[
            ("Widgets do seu jeito", "Um dispositivo, uma lista ou uma grade. Escolha cores, transparência, cantos arredondados, borda, ícone, tamanho do texto e layout. O status online e offline é atualizado em segundo plano."),
            ("Um toque, sem abrir o app", "Toque em Ligar no próprio widget. Também dá para ligar por atalhos do launcher e botões de Configurações rápidas."),
            ("Saiba o que está ligado", "Veja quais dispositivos estão online. Atualize todos com um botão ou puxando a lista para baixo."),
            ("Grupos e Ligar todos", "Reúna dispositivos em grupos, como Escritório ou Laboratório, e ligue um grupo inteiro com um botão."),
            ("Desligamento remoto por SSH", "Opcional. O WakeDeck entra com uma chave guardada no Android Keystore. Ele nunca pede nem guarda senha e fixa a chave de host de cada PC."),
            ("Privado por princípio", "Sem anúncios, sem análise de uso, sem contas. Seus dispositivos e ajustes ficam no seu celular, e o app só fala com os endereços que você adiciona."),
        ],
        how_h="Três passos", how_sub="Um guia no app explica o lado do PC, para Windows e Linux.",
        steps=[
            ("Ative o Wake-on-LAN", "Ative-o na BIOS ou UEFI do PC e nas configurações da placa de rede. A conexão por cabo Ethernet funciona melhor."),
            ("Adicione seu dispositivo", "Informe um nome e o endereço MAC. Adicione o endereço IP se quiser ver o status online."),
            ("Adicione um widget", "Toque e segure a tela inicial, adicione um widget do WakeDeck e deixe-o do seu jeito."),
        ],
        priv_h="Seus dados são seus", priv_p="O WakeDeck não tem servidor nem conta. Ele não coleta nem compartilha dados pessoais. Leia a política de privacidade, que é curta, para ver exatamente o que fica no seu celular.", priv_btn="Política de privacidade",
        away_h="Longe de casa?", away_p="Use a sua própria VPN e defina o endereço de broadcast do dispositivo como o da sua sub-rede, por exemplo 192.168.1.255. A VPN e o roteador precisam repassá-lo. A ajuda no app explica os detalhes e as mensagens de erro mais comuns.",
    ),
    "pt-pt": dict(
        title="WakeDeck: widgets Wake-on-LAN para Android",
        desc="Ligue os seus PCs a partir do ecrã inicial do Android. Widgets personalizáveis, atalhos e mosaicos das Definições rápidas. Sem anúncios, sem análise de utilização, sem contas.",
        ogdesc="Ligue os seus dispositivos a partir do telemóvel. Widgets personalizáveis no ecrã inicial. Sem anúncios, sem análise de utilização, sem contas.",
        skip="Ir para o conteúdo", language="Idioma", features="Funcionalidades", how="Como funciona", privacy="Privacidade", contact="Contacto",
        h1=("Ligue os seus dispositivos a partir do ", "telemóvel", ""),
        lead="A WakeDeck liga os seus PCs com um pacote mágico Wake-on-LAN, diretamente a partir de widgets que você mesmo desenha. Um toque no ecrã inicial e a aplicação nem precisa de abrir.",
        play="Google Play Store: brevemente", soon="Gratuita para Android 8.0 ou superior. Sem anúncios, sem análise de utilização, sem contas.",
        feat_h="Pensada em torno do widget", feat_sub="O ecrã inicial é o seu telecomando. Escolha o aspeto e o sítio onde fica.",
        cards=[
            ("Widgets ao seu gosto", "Um dispositivo, uma lista ou uma grelha. Escolha cores, transparência, cantos arredondados, contorno, ícone, tamanho do texto e esquema. O estado online e offline é atualizado em segundo plano."),
            ("Um toque, sem abrir a aplicação", "Toque em Ligar no próprio widget. Também pode ligar através de atalhos do launcher e mosaicos das Definições rápidas."),
            ("Saiba o que está ligado", "Veja que dispositivos estão online. Atualize-os todos com um botão ou ao puxar a lista para baixo."),
            ("Grupos e Ligar todos", "Junte dispositivos em grupos, como Escritório ou Laboratório, e ligue um grupo inteiro com um botão."),
            ("Encerramento remoto por SSH", "Opcional. A WakeDeck autentica-se com uma chave guardada no Android Keystore. Nunca pede nem guarda palavras-passe e fixa a chave de anfitrião de cada PC."),
            ("Privada por princípio", "Sem anúncios, sem análise de utilização, sem contas. Os seus dispositivos e definições ficam no telemóvel, e a aplicação só comunica com os endereços que adicionar."),
        ],
        how_h="Três passos", how_sub="Um guia na aplicação explica o lado do PC, para Windows e Linux.",
        steps=[
            ("Ative o Wake-on-LAN", "Ative-o na BIOS ou UEFI do PC e nas definições da placa de rede. A ligação por cabo Ethernet funciona melhor."),
            ("Adicione o seu dispositivo", "Indique um nome e o endereço MAC. Adicione o endereço IP se quiser ver o estado online."),
            ("Adicione um widget", "Mantenha premido o ecrã inicial, adicione um widget da WakeDeck e personalize-o ao seu gosto."),
        ],
        priv_h="Os seus dados são seus", priv_p="A WakeDeck não tem servidor nem conta. Não recolhe nem partilha dados pessoais. Leia a política de privacidade, que é curta, para ver exatamente o que fica no seu telemóvel.", priv_btn="Política de privacidade",
        away_h="Longe de casa?", away_p="Use a sua própria VPN e defina o endereço de broadcast do dispositivo como o da sua sub-rede, por exemplo 192.168.1.255. A VPN e o router têm de o reencaminhar. A ajuda na aplicação explica os detalhes e as mensagens de erro mais comuns.",
    ),
    "fr": dict(
        title="WakeDeck : widgets Wake-on-LAN pour Android",
        desc="Allumez vos PC depuis l’écran d’accueil d’Android. Widgets personnalisables, raccourcis et tuiles des réglages rapides. Sans publicité, sans analyse, sans compte.",
        ogdesc="Allumez vos appareils depuis votre smartphone. Widgets personnalisables sur l’écran d’accueil. Sans publicité, sans analyse, sans compte.",
        skip="Aller au contenu", language="Langue", features="Fonctionnalités", how="Comment ça marche", privacy="Confidentialité", contact="Contact",
        h1=("Allumez vos appareils depuis votre ", "smartphone", ""),
        lead="WakeDeck allume vos PC avec un paquet magique Wake-on-LAN, directement depuis des widgets que vous concevez vous-même. Un toucher sur l’écran d’accueil, et l’application ne s’ouvre même pas.",
        play="Google Play Store : bientôt disponible", soon="Gratuit pour Android 8.0 et versions ultérieures. Sans publicité, sans analyse, sans compte.",
        feat_h="Conçu autour du widget", feat_sub="Votre écran d’accueil est la télécommande. Choisissez son apparence et son emplacement.",
        cards=[
            ("Des widgets à votre façon", "Un seul appareil, une liste ou une grille. Choisissez les couleurs, la transparence, les coins arrondis, la bordure, l’icône, la taille du texte et la mise en page. L’état en ligne ou hors ligne se met à jour en arrière-plan."),
            ("Un toucher, sans ouvrir l’appli", "Touchez Allumer dans le widget lui-même. Vous pouvez aussi allumer depuis les raccourcis du lanceur et les tuiles des réglages rapides."),
            ("Sachez ce qui est allumé", "Voyez quels appareils sont en ligne. Actualisez-les tous avec un bouton ou en tirant la liste vers le bas."),
            ("Groupes et Tout allumer", "Rangez vos appareils dans des groupes, comme Bureau ou Labo, et allumez un groupe entier d’un seul bouton."),
            ("Arrêt à distance par SSH", "Facultatif. WakeDeck se connecte avec une clé conservée dans l’Android Keystore. Il ne demande ni ne stocke jamais de mot de passe, et il épingle la clé d’hôte de chaque PC."),
            ("Confidentiel par conception", "Sans publicité, sans analyse, sans compte. Vos appareils et vos réglages restent sur votre téléphone, et l’application ne contacte que les adresses que vous ajoutez."),
        ],
        how_h="Trois étapes", how_sub="Un guide intégré à l’appli vous accompagne côté PC, pour Windows et Linux.",
        steps=[
            ("Activez le Wake-on-LAN", "Activez-le dans le BIOS ou l’UEFI du PC et dans les réglages de la carte réseau. Une connexion Ethernet filaire donne les meilleurs résultats."),
            ("Ajoutez votre appareil", "Saisissez un nom et l’adresse MAC. Ajoutez l’adresse IP si vous voulez voir son état en ligne."),
            ("Ajoutez un widget", "Appuyez longuement sur l’écran d’accueil, ajoutez un widget WakeDeck et personnalisez-le."),
        ],
        priv_h="Vos données restent à vous", priv_p="WakeDeck n’a ni serveur ni compte. Il ne collecte ni ne partage de données personnelles. Lisez la courte politique de confidentialité pour savoir exactement ce qui est stocké sur votre téléphone.", priv_btn="Politique de confidentialité",
        away_h="Loin de chez vous ?", away_p="Utilisez votre propre VPN et réglez l’adresse de diffusion de l’appareil sur celle de votre sous-réseau, par exemple 192.168.1.255. Votre VPN et votre routeur doivent la transmettre. L’aide intégrée détaille la procédure et les messages d’erreur courants.",
    ),
    "de": dict(
        title="WakeDeck: Wake-on-LAN-Widgets für Android",
        desc="Wecke deine PCs vom Android-Startbildschirm. Anpassbare Widgets, Verknüpfungen und Schnelleinstellungs-Kacheln. Keine Werbung, keine Analyse, keine Konten.",
        ogdesc="Wecke deine Geräte mit deinem Smartphone. Anpassbare Widgets für den Startbildschirm. Keine Werbung, keine Analyse, keine Konten.",
        skip="Zum Inhalt springen", language="Sprache", features="Funktionen", how="So funktioniert’s", privacy="Datenschutz", contact="Kontakt",
        h1=("Wecke deine Geräte mit deinem ", "Smartphone", ""),
        lead="WakeDeck schaltet deine PCs mit einem Wake-on-LAN-Magic-Packet ein, direkt aus Widgets, die du selbst gestaltest. Ein Tipp auf dem Startbildschirm, und die App muss nicht einmal geöffnet werden.",
        play="Google Play Store: bald verfügbar", soon="Kostenlos für Android 8.0 und neuer. Keine Werbung, keine Analyse, keine Konten.",
        feat_h="Rund um das Widget gebaut", feat_sub="Dein Startbildschirm ist die Fernbedienung. Bestimme, wie sie aussieht und wo sie sitzt.",
        cards=[
            ("Widgets nach deinem Geschmack", "Ein einzelnes Gerät, eine Liste oder ein Raster. Wähle Farben, Transparenz, Eckenradius, Rahmen, Symbol, Textgröße und Layout. Der Online- und Offline-Status wird im Hintergrund aktualisiert."),
            ("Ein Tipp, ohne die App zu öffnen", "Tippe im Widget selbst auf Wecken. Du kannst auch über Launcher-Verknüpfungen und Schnelleinstellungs-Kacheln wecken."),
            ("Sieh, was an ist", "Sieh, welche Geräte online sind. Aktualisiere alle mit einer Schaltfläche oder indem du die Liste nach unten ziehst."),
            ("Gruppen und Alle wecken", "Fasse Geräte in Gruppen zusammen, etwa Büro oder Labor, und wecke eine ganze Gruppe mit einer Schaltfläche."),
            ("Fernabschaltung per SSH", "Optional. WakeDeck meldet sich mit einem Schlüssel an, der im Android Keystore liegt. Es fragt nie nach einem Passwort, speichert keines und merkt sich den Host-Schlüssel jedes PCs."),
            ("Privat von Grund auf", "Keine Werbung, keine Analyse, keine Konten. Deine Geräte und Einstellungen bleiben auf deinem Telefon, und die App spricht nur mit den Adressen, die du hinzufügst."),
        ],
        how_h="Drei Schritte", how_sub="Eine Anleitung in der App führt dich durch die PC-Seite, für Windows und Linux.",
        steps=[
            ("Wake-on-LAN aktivieren", "Aktiviere es im BIOS oder UEFI deines PCs und in den Einstellungen der Netzwerkkarte. Kabelgebundenes Ethernet funktioniert am besten."),
            ("Gerät hinzufügen", "Gib einen Namen und die MAC-Adresse ein. Füge die IP-Adresse hinzu, wenn du den Online-Status sehen möchtest."),
            ("Widget hinzufügen", "Halte den Startbildschirm gedrückt, füge ein WakeDeck-Widget hinzu und gestalte es nach deinem Geschmack."),
        ],
        priv_h="Deine Daten gehören dir", priv_p="WakeDeck hat keinen Server und kein Konto. Es erhebt und teilt keine personenbezogenen Daten. Lies die kurze Datenschutzerklärung, um genau zu sehen, was auf deinem Telefon gespeichert wird.", priv_btn="Datenschutzerklärung",
        away_h="Unterwegs?", away_p="Nutze dein eigenes VPN und stelle die Broadcast-Adresse des Geräts auf die deines Subnetzes ein, zum Beispiel 192.168.1.255. Dein VPN und dein Router müssen sie weiterleiten. Die Hilfe in der App erklärt die Einzelheiten und die häufigsten Fehlermeldungen.",
    ),
    "es": dict(
        title="WakeDeck: widgets Wake-on-LAN para Android",
        desc="Enciende tus PC desde la pantalla de inicio de Android. Widgets personalizables, accesos directos y mosaicos de Ajustes rápidos. Sin anuncios, sin analítica, sin cuentas.",
        ogdesc="Enciende tus dispositivos desde tu móvil. Widgets personalizables en la pantalla de inicio. Sin anuncios, sin analítica, sin cuentas.",
        skip="Saltar al contenido", language="Idioma", features="Funciones", how="Cómo funciona", privacy="Privacidad", contact="Contacto",
        h1=("Enciende tus dispositivos desde tu ", "móvil", ""),
        lead="WakeDeck enciende tus PC con un paquete mágico Wake-on-LAN, directamente desde widgets que diseñas tú mismo. Un toque en la pantalla de inicio y la app ni siquiera se abre.",
        play="Google Play Store: próximamente", soon="Gratis para Android 8.0 y posteriores. Sin anuncios, sin analítica, sin cuentas.",
        feat_h="Pensado en torno al widget", feat_sub="Tu pantalla de inicio es el mando a distancia. Elige cómo se ve y dónde se coloca.",
        cards=[
            ("Widgets a tu manera", "Un solo dispositivo, una lista o una cuadrícula. Elige colores, transparencia, radio de las esquinas, borde, icono, tamaño del texto y diseño. El estado en línea o sin conexión se actualiza en segundo plano."),
            ("Un toque, sin abrir la app", "Toca Encender en el propio widget. También puedes encender desde accesos directos del launcher y mosaicos de Ajustes rápidos."),
            ("Sabe qué está encendido", "Mira qué dispositivos están en línea. Actualízalos todos con un botón o deslizando la lista hacia abajo."),
            ("Grupos y Encender todos", "Reúne dispositivos en grupos, como Oficina o Laboratorio, y enciende un grupo entero con un botón."),
            ("Apagado remoto por SSH", "Opcional. WakeDeck inicia sesión con una clave guardada en el Android Keystore. Nunca pide ni guarda contraseñas, y fija la clave de host de cada PC."),
            ("Privado por diseño", "Sin anuncios, sin analítica, sin cuentas. Tus dispositivos y ajustes se quedan en tu teléfono, y la app solo se comunica con las direcciones que añades."),
        ],
        how_h="Tres pasos", how_sub="Una guía dentro de la app te acompaña en el lado del PC, para Windows y Linux.",
        steps=[
            ("Activa Wake-on-LAN", "Actívalo en la BIOS o UEFI del PC y en los ajustes de la tarjeta de red. La conexión Ethernet por cable funciona mejor."),
            ("Añade tu dispositivo", "Introduce un nombre y la dirección MAC. Añade la dirección IP si quieres ver su estado en línea."),
            ("Añade un widget", "Mantén pulsada la pantalla de inicio, añade un widget de WakeDeck y personalízalo a tu gusto."),
        ],
        priv_h="Tus datos son tuyos", priv_p="WakeDeck no tiene servidor ni cuenta. No recopila ni comparte datos personales. Lee la breve política de privacidad para ver exactamente qué se guarda en tu teléfono.", priv_btn="Política de privacidad",
        away_h="¿Lejos de casa?", away_p="Usa tu propia VPN y establece la dirección de difusión del dispositivo como la de tu subred, por ejemplo 192.168.1.255. Tu VPN y tu router deben reenviarla. La ayuda de la app explica los detalles y los mensajes de error más comunes.",
    ),
    "zh": dict(
        title="WakeDeck：Android 网络唤醒（Wake-on-LAN）小组件",
        desc="在 Android 主屏幕上唤醒你的电脑。可自定义的小组件、快捷方式和快速设置磁贴。无广告、无统计、无需账号。",
        ogdesc="用手机唤醒你的设备。可自定义的主屏幕小组件。无广告、无统计、无需账号。",
        skip="跳到正文", language="语言", features="功能", how="使用方法", privacy="隐私", contact="联系",
        h1=("用", "手机", "唤醒你的设备"),
        lead="WakeDeck 通过 Wake-on-LAN 魔术包唤醒你的电脑，直接在你自己设计的小组件上操作。在主屏幕上轻点一下，连应用都不用打开。",
        play="Google Play 商店：即将上架", soon="适用于 Android 8.0 及更高版本，免费。无广告、无统计、无需账号。",
        feat_h="围绕小组件而设计", feat_sub="主屏幕就是你的遥控器。自己决定它的外观和位置。",
        cards=[
            ("由你设计的小组件", "单个设备、列表或网格。可选择颜色、透明度、圆角、边框、图标、文字大小和布局。在线和离线状态在后台自动更新。"),
            ("轻点一下，无需打开应用", "直接在小组件上点按“唤醒”。也可以通过启动器快捷方式和快速设置磁贴来唤醒。"),
            ("随时掌握开机状态", "查看哪些设备在线。用一个按钮或下拉列表即可全部刷新。"),
            ("分组与全部唤醒", "把设备放进分组，例如“办公室”或“实验室”，一个按钮即可唤醒整个分组。"),
            ("通过 SSH 远程关机", "可选功能。WakeDeck 使用保存在 Android Keystore 中的密钥登录，从不索取也不保存密码，并会固定每台电脑的主机密钥。"),
            ("隐私优先", "无广告、无统计、无需账号。你的设备和设置只保存在手机上，应用只会连接你添加的地址。"),
        ],
        how_h="三个步骤", how_sub="应用内的指南会带你完成电脑端的设置，支持 Windows 和 Linux。",
        steps=[
            ("开启 Wake-on-LAN", "在电脑的 BIOS 或 UEFI 以及网卡设置中启用。有线以太网效果最好。"),
            ("添加你的设备", "输入名称和 MAC 地址。若想查看在线状态，请再添加 IP 地址。"),
            ("添加小组件", "长按主屏幕，添加 WakeDeck 小组件，并按你的喜好设置样式。"),
        ],
        priv_h="你的数据归你所有", priv_p="WakeDeck 没有服务器，也没有账号。它不会收集或分享个人数据。阅读简短的隐私政策，了解手机上究竟保存了什么。", priv_btn="隐私政策",
        away_h="不在家？", away_p="使用你自己的 VPN，并把设备的广播地址设为你所在子网的广播地址，例如 192.168.1.255。你的 VPN 和路由器必须转发该广播。应用内的帮助会说明详细步骤和常见错误信息。",
    ),
    "ja": dict(
        title="WakeDeck：Android 用 Wake-on-LAN ウィジェット",
        desc="Android のホーム画面から PC を起動。カスタマイズできるウィジェット、ショートカット、クイック設定タイル。広告なし、分析なし、アカウント不要。",
        ogdesc="スマートフォンからデバイスを起動。カスタマイズできるホーム画面ウィジェット。広告なし、分析なし、アカウント不要。",
        skip="本文へ移動", language="言語", features="機能", how="使い方", privacy="プライバシー", contact="お問い合わせ",
        h1=("あなたの", "スマートフォン", "からデバイスを起動"),
        lead="WakeDeck は、自分で設計したウィジェットから Wake-on-LAN のマジックパケットを送り、PC を起動します。ホーム画面でタップするだけで、アプリを開く必要もありません。",
        play="Google Play ストア：近日公開", soon="Android 8.0 以降で無料。広告なし、分析なし、アカウント不要。",
        feat_h="ウィジェットを中心に設計", feat_sub="ホーム画面がリモコンです。見た目も置き場所も自由に決められます。",
        cards=[
            ("自分好みのウィジェット", "1 台用、リスト、グリッド。色、透明度、角の丸み、枠線、アイコン、文字サイズ、レイアウトを選べます。オンライン／オフライン状態はバックグラウンドで更新されます。"),
            ("タップだけ、アプリは不要", "ウィジェットの「起動」をタップするだけ。ランチャーのショートカットやクイック設定タイルからも起動できます。"),
            ("起動状態がひと目でわかる", "どのデバイスがオンラインか確認できます。ボタンまたはリストを下に引いて、すべてを更新できます。"),
            ("グループと「すべて起動」", "デバイスを「オフィス」や「ラボ」などのグループにまとめ、ボタン 1 つでグループ全体を起動できます。"),
            ("SSH によるリモートシャットダウン", "任意の機能です。WakeDeck は Android Keystore に保管した鍵でログインします。パスワードを求めることも保存することもなく、各 PC のホスト鍵を固定します。"),
            ("プライバシー重視の設計", "広告なし、分析なし、アカウント不要。デバイスと設定はスマートフォンの中に残り、アプリは追加したアドレスにしか接続しません。"),
        ],
        how_h="3 つのステップ", how_sub="PC 側の設定は、Windows と Linux に対応したアプリ内ガイドが案内します。",
        steps=[
            ("Wake-on-LAN を有効にする", "PC の BIOS または UEFI と、ネットワークカードの設定で有効にします。有線 LAN での接続がおすすめです。"),
            ("デバイスを追加する", "名前と MAC アドレスを入力します。オンライン状態を見たい場合は IP アドレスも追加します。"),
            ("ウィジェットを追加する", "ホーム画面を長押しして WakeDeck ウィジェットを追加し、好みのスタイルに整えます。"),
        ],
        priv_h="あなたのデータはあなたのもの", priv_p="WakeDeck にはサーバーもアカウントもありません。個人データの収集も共有もしません。短いプライバシーポリシーで、スマートフォンに何が保存されるかを確認できます。", priv_btn="プライバシーポリシー",
        away_h="外出先から？", away_p="ご自身の VPN を使い、デバイスのブロードキャストアドレスをサブネットのもの（例：192.168.1.255）に設定します。VPN とルーターが転送できる必要があります。詳しい手順とよくあるエラーメッセージは、アプリ内のヘルプで説明しています。",
    ),
}

# ---------------------------------------------------------------- privacy page text
# Blocks: ("h2", text), ("p", text), ("ul", [items]). Items may carry <strong> markup.
PRIVACY = {
    "en": dict(
        title="Privacy policy", meta_title="Privacy policy | WakeDeck", date_label="Last updated: 11 October 2026",
        desc="WakeDeck does not collect, share or sell personal data. It has no ads, analytics or tracking. This policy explains what stays on your phone.",
        ogdesc="No ads, no analytics, no accounts. Your data stays on your phone.", note="",
        summary="<strong>In short:</strong> WakeDeck does not collect, share or sell any personal data, and it has no ads, analytics or tracking. Everything you enter stays on your phone.",
        intro="WakeDeck is a Wake-on-LAN app for Android made by Diego Bergonsi. This policy explains what the app does with your data.",
        blocks=[
            ("h2", "What stays on your phone"),
            ("p", "Everything you enter in WakeDeck is stored only on your phone:"),
            ("ul", [
                "The devices you add: name, MAC address, IP address or host name, broadcast address, ports, operating system, group, and the optional SSH user name and port.",
                "Your widget, shortcut and Quick Settings tile settings.",
                "The last known online or offline status of your devices.",
                "For the optional remote shutdown feature, an SSH key pair. The private key is created inside the Android Keystore and cannot be read or copied out of it. The app never asks for or stores a password. The app also stores the fingerprint of each PC's SSH host key that you chose to trust.",
            ]),
            ("p", "None of this is sent to the developer or to any server run by the developer. WakeDeck has no account, no server and no backend. App data is excluded from Android cloud backup."),
            ("h2", "Network traffic"),
            ("p", "WakeDeck only connects to the addresses you configure:"),
            ("ul", [
                "A Wake-on-LAN magic packet (UDP) to the broadcast address you set for a device.",
                "Online or offline checks: a TCP connection attempt or a ping to the device's host.",
                "Remote shutdown, only when you tap it and confirm: an SSH connection to the PC you set up.",
            ]),
            ("p", "This traffic goes directly from your phone to your own devices, over your Wi-Fi or over a VPN that you run. It does not pass through the developer."),
            ("h2", "Files you choose to create"),
            ("p", "The export function writes your device list to a file you pick. The file does not contain your SSH key or trusted host keys. WakeDeck does not upload it anywhere. What you do with the file is up to you."),
            ("p", "The import function reads a file you pick. Before it adds anything, WakeDeck shows you the devices in the file and warns you about any that point outside your home network."),
            ("h2", "Permissions"),
            ("ul", [
                "<strong>Internet and network state:</strong> to send the packet and check device status.",
                "<strong>Wake lock:</strong> to finish a wake or a status check while the screen is off.",
                "<strong>Notifications:</strong> only so the system lets the app show its short confirmation message after a wake from a launcher shortcut. WakeDeck does not send notifications, reminders or promotions.",
            ]),
            ("h2", "Third parties"),
            ("p", "WakeDeck contains no advertising, analytics, crash reporting or other third-party services that collect data. It uses open-source libraries that run on your phone."),
            ("h2", "This website"),
            ("p", "This website uses no cookies, no analytics and no scripts, and it loads nothing from other sites. It is hosted on GitHub Pages, and its domain is managed through Cloudflare. As with any web host, they may keep standard server logs under their own privacy policies. The developer does not receive or use that data."),
            ("h2", "Children"),
            ("p", "WakeDeck is not directed at children and collects no data from anyone."),
            ("h2", "Changes"),
            ("p", "If this policy changes, the new version will be published at this address with a new date."),
            ("h2", "Contact"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "pt-br": dict(
        title="Política de privacidade", meta_title="Política de privacidade | WakeDeck", date_label="Última atualização: 11 de outubro de 2026",
        desc="O WakeDeck não coleta, compartilha nem vende dados pessoais. Não tem anúncios, análise de uso nem rastreamento. Esta política explica o que fica no seu celular.",
        ogdesc="Sem anúncios, sem análise de uso, sem contas. Seus dados ficam no seu celular.",
        note="Esta tradução é oferecida por conveniência. Se houver diferença em relação à versão em inglês, vale a versão em inglês.",
        summary="<strong>Em resumo:</strong> o WakeDeck não coleta, compartilha nem vende dados pessoais e não tem anúncios, análise de uso nem rastreamento. Tudo o que você informa fica no seu celular.",
        intro="O WakeDeck é um app Wake-on-LAN para Android criado por Diego Bergonsi. Esta política explica o que o app faz com os seus dados.",
        blocks=[
            ("h2", "O que fica no seu celular"),
            ("p", "Tudo o que você informa no WakeDeck é guardado somente no seu celular:"),
            ("ul", [
                "Os dispositivos que você adiciona: nome, endereço MAC, endereço IP ou nome do host, endereço de broadcast, portas, sistema operacional, grupo e, opcionalmente, usuário e porta SSH.",
                "Os ajustes dos seus widgets, atalhos e botões de Configurações rápidas.",
                "O último status online ou offline conhecido dos seus dispositivos.",
                "Para o desligamento remoto opcional, um par de chaves SSH. A chave privada é criada dentro do Android Keystore e não pode ser lida nem copiada de lá. O app nunca pede nem guarda senhas. Ele também guarda a impressão digital da chave de host SSH de cada PC em que você escolheu confiar.",
            ]),
            ("p", "Nada disso é enviado ao desenvolvedor nem a qualquer servidor do desenvolvedor. O WakeDeck não tem conta, servidor nem back-end. Os dados do app ficam fora do backup na nuvem do Android."),
            ("h2", "Tráfego de rede"),
            ("p", "O WakeDeck só se conecta aos endereços que você configura:"),
            ("ul", [
                "Um pacote mágico Wake-on-LAN (UDP) para o endereço de broadcast que você definiu para o dispositivo.",
                "Verificações online ou offline: uma tentativa de conexão TCP ou um ping ao host do dispositivo.",
                "Desligamento remoto, somente quando você toca e confirma: uma conexão SSH com o PC que você configurou.",
            ]),
            ("p", "Esse tráfego vai direto do seu celular para os seus próprios dispositivos, pelo seu Wi-Fi ou por uma VPN que você mantém. Ele não passa pelo desenvolvedor."),
            ("h2", "Arquivos que você escolhe criar"),
            ("p", "A função de exportar grava a lista de dispositivos em um arquivo que você escolhe. O arquivo não contém a sua chave SSH nem as chaves de host confiáveis. O WakeDeck não o envia para lugar nenhum. O que fazer com o arquivo fica por sua conta."),
            ("p", "A função de importar lê um arquivo que você escolhe. Antes de adicionar qualquer coisa, o WakeDeck mostra os dispositivos do arquivo e avisa sobre os que apontam para fora da sua rede doméstica."),
            ("h2", "Permissões"),
            ("ul", [
                "<strong>Internet e estado da rede:</strong> para enviar o pacote e verificar o status dos dispositivos.",
                "<strong>Wake lock:</strong> para concluir um comando de ligar ou uma verificação de status com a tela apagada.",
                "<strong>Notificações:</strong> apenas para que o sistema deixe o app mostrar a breve mensagem de confirmação após ligar por um atalho do launcher. O WakeDeck não envia notificações, lembretes nem promoções.",
            ]),
            ("h2", "Terceiros"),
            ("p", "O WakeDeck não contém publicidade, análise de uso, relatório de falhas nem outros serviços de terceiros que coletem dados. Ele usa bibliotecas de código aberto que rodam no seu celular."),
            ("h2", "Este site"),
            ("p", "Este site não usa cookies, análise de uso nem scripts e não carrega nada de outros sites. Ele está hospedado no GitHub Pages e o domínio é gerenciado pela Cloudflare. Como qualquer servidor web, eles podem manter registros padrão sob as próprias políticas de privacidade. O desenvolvedor não recebe nem usa esses dados."),
            ("h2", "Crianças"),
            ("p", "O WakeDeck não é voltado a crianças e não coleta dados de ninguém."),
            ("h2", "Alterações"),
            ("p", "Se esta política mudar, a nova versão será publicada neste endereço com uma nova data."),
            ("h2", "Contato"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "pt-pt": dict(
        title="Política de privacidade", meta_title="Política de privacidade | WakeDeck", date_label="Última atualização: 11 de outubro de 2026",
        desc="A WakeDeck não recolhe, partilha nem vende dados pessoais. Não tem anúncios, análise de utilização nem rastreio. Esta política explica o que fica no seu telemóvel.",
        ogdesc="Sem anúncios, sem análise de utilização, sem contas. Os seus dados ficam no seu telemóvel.",
        note="Esta tradução é disponibilizada por conveniência. Se houver diferenças em relação à versão em inglês, prevalece a versão em inglês.",
        summary="<strong>Em resumo:</strong> a WakeDeck não recolhe, partilha nem vende dados pessoais e não tem anúncios, análise de utilização nem rastreio. Tudo o que introduz fica no seu telemóvel.",
        intro="A WakeDeck é uma aplicação Wake-on-LAN para Android criada por Diego Bergonsi. Esta política explica o que a aplicação faz com os seus dados.",
        blocks=[
            ("h2", "O que fica no seu telemóvel"),
            ("p", "Tudo o que introduz na WakeDeck é guardado apenas no seu telemóvel:"),
            ("ul", [
                "Os dispositivos que adiciona: nome, endereço MAC, endereço IP ou nome do anfitrião, endereço de broadcast, portas, sistema operativo, grupo e, opcionalmente, utilizador e porta SSH.",
                "As definições dos seus widgets, atalhos e mosaicos das Definições rápidas.",
                "O último estado online ou offline conhecido dos seus dispositivos.",
                "Para o encerramento remoto opcional, um par de chaves SSH. A chave privada é criada dentro do Android Keystore e não pode ser lida nem copiada de lá. A aplicação nunca pede nem guarda palavras-passe. Guarda também a impressão digital da chave de anfitrião SSH de cada PC em que escolheu confiar.",
            ]),
            ("p", "Nada disto é enviado ao programador nem a qualquer servidor do programador. A WakeDeck não tem conta, servidor nem back-end. Os dados da aplicação ficam fora da cópia de segurança na nuvem do Android."),
            ("h2", "Tráfego de rede"),
            ("p", "A WakeDeck só se liga aos endereços que configurar:"),
            ("ul", [
                "Um pacote mágico Wake-on-LAN (UDP) para o endereço de broadcast que definiu para o dispositivo.",
                "Verificações online ou offline: uma tentativa de ligação TCP ou um ping ao anfitrião do dispositivo.",
                "Encerramento remoto, apenas quando toca e confirma: uma ligação SSH ao PC que configurou.",
            ]),
            ("p", "Este tráfego vai diretamente do seu telemóvel para os seus próprios dispositivos, através do seu Wi-Fi ou de uma VPN que gere. Não passa pelo programador."),
            ("h2", "Ficheiros que escolhe criar"),
            ("p", "A função de exportar grava a lista de dispositivos num ficheiro que escolher. O ficheiro não contém a sua chave SSH nem as chaves de anfitrião de confiança. A WakeDeck não o envia para lado nenhum. O que fazer com o ficheiro fica ao seu critério."),
            ("p", "A função de importar lê um ficheiro que escolher. Antes de adicionar seja o que for, a WakeDeck mostra os dispositivos do ficheiro e avisa sobre os que apontam para fora da sua rede doméstica."),
            ("h2", "Autorizações"),
            ("ul", [
                "<strong>Internet e estado da rede:</strong> para enviar o pacote e verificar o estado dos dispositivos.",
                "<strong>Wake lock:</strong> para concluir um comando de ligar ou uma verificação de estado com o ecrã desligado.",
                "<strong>Notificações:</strong> apenas para que o sistema deixe a aplicação mostrar a breve mensagem de confirmação após ligar através de um atalho do launcher. A WakeDeck não envia notificações, lembretes nem promoções.",
            ]),
            ("h2", "Terceiros"),
            ("p", "A WakeDeck não contém publicidade, análise de utilização, relatórios de falhas nem outros serviços de terceiros que recolham dados. Usa bibliotecas de código aberto que correm no seu telemóvel."),
            ("h2", "Este sítio web"),
            ("p", "Este sítio não usa cookies, análise de utilização nem scripts e não carrega nada de outros sítios. Está alojado no GitHub Pages e o domínio é gerido pela Cloudflare. Como qualquer servidor web, podem manter registos padrão ao abrigo das suas próprias políticas de privacidade. O programador não recebe nem usa esses dados."),
            ("h2", "Crianças"),
            ("p", "A WakeDeck não se destina a crianças e não recolhe dados de ninguém."),
            ("h2", "Alterações"),
            ("p", "Se esta política mudar, a nova versão será publicada neste endereço com uma nova data."),
            ("h2", "Contacto"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "fr": dict(
        title="Politique de confidentialité", meta_title="Politique de confidentialité | WakeDeck", date_label="Dernière mise à jour : 11 octobre 2026",
        desc="WakeDeck ne collecte, ne partage et ne vend aucune donnée personnelle. Pas de publicité, d’analyse ni de suivi. Cette politique explique ce qui reste sur votre téléphone.",
        ogdesc="Sans publicité, sans analyse, sans compte. Vos données restent sur votre téléphone.",
        note="Cette traduction est fournie à titre indicatif. En cas de différence avec la version anglaise, la version anglaise prévaut.",
        summary="<strong>En bref :</strong> WakeDeck ne collecte, ne partage et ne vend aucune donnée personnelle, et n’a ni publicité, ni analyse, ni suivi. Tout ce que vous saisissez reste sur votre téléphone.",
        intro="WakeDeck est une application Wake-on-LAN pour Android créée par Diego Bergonsi. Cette politique explique ce que l’application fait de vos données.",
        blocks=[
            ("h2", "Ce qui reste sur votre téléphone"),
            ("p", "Tout ce que vous saisissez dans WakeDeck est stocké uniquement sur votre téléphone :"),
            ("ul", [
                "Les appareils que vous ajoutez : nom, adresse MAC, adresse IP ou nom d’hôte, adresse de diffusion, ports, système d’exploitation, groupe, ainsi que l’utilisateur et le port SSH facultatifs.",
                "Les réglages de vos widgets, raccourcis et tuiles des réglages rapides.",
                "Le dernier état connu, en ligne ou hors ligne, de vos appareils.",
                "Pour la fonction facultative d’arrêt à distance, une paire de clés SSH. La clé privée est créée dans l’Android Keystore et ne peut ni en être lue ni en être copiée. L’application ne demande ni ne stocke jamais de mot de passe. Elle conserve aussi l’empreinte de la clé d’hôte SSH de chaque PC auquel vous avez choisi de faire confiance.",
            ]),
            ("p", "Rien de tout cela n’est envoyé au développeur ni à un serveur géré par le développeur. WakeDeck n’a ni compte, ni serveur, ni back-end. Les données de l’application sont exclues de la sauvegarde cloud d’Android."),
            ("h2", "Trafic réseau"),
            ("p", "WakeDeck se connecte uniquement aux adresses que vous configurez :"),
            ("ul", [
                "Un paquet magique Wake-on-LAN (UDP) vers l’adresse de diffusion définie pour un appareil.",
                "Les vérifications en ligne ou hors ligne : une tentative de connexion TCP ou un ping vers l’hôte de l’appareil.",
                "L’arrêt à distance, uniquement lorsque vous le touchez et le confirmez : une connexion SSH vers le PC que vous avez configuré.",
            ]),
            ("p", "Ce trafic va directement de votre téléphone à vos propres appareils, par votre Wi-Fi ou par un VPN que vous gérez. Il ne passe pas par le développeur."),
            ("h2", "Fichiers que vous choisissez de créer"),
            ("p", "La fonction d’exportation écrit votre liste d’appareils dans un fichier que vous choisissez. Le fichier ne contient ni votre clé SSH ni les clés d’hôte approuvées. WakeDeck ne l’envoie nulle part. Ce que vous faites du fichier vous appartient."),
            ("p", "La fonction d’importation lit un fichier que vous choisissez. Avant d’ajouter quoi que ce soit, WakeDeck vous montre les appareils du fichier et vous avertit de ceux qui pointent hors de votre réseau domestique."),
            ("h2", "Autorisations"),
            ("ul", [
                "<strong>Internet et état du réseau :</strong> pour envoyer le paquet et vérifier l’état des appareils.",
                "<strong>Wake lock :</strong> pour terminer un réveil ou une vérification d’état lorsque l’écran est éteint.",
                "<strong>Notifications :</strong> uniquement pour que le système autorise l’application à afficher son bref message de confirmation après un réveil depuis un raccourci du lanceur. WakeDeck n’envoie ni notifications, ni rappels, ni promotions.",
            ]),
            ("h2", "Tiers"),
            ("p", "WakeDeck ne contient aucune publicité, analyse, rapport de plantage ni autre service tiers collectant des données. Il utilise des bibliothèques open source qui s’exécutent sur votre téléphone."),
            ("h2", "Ce site web"),
            ("p", "Ce site n’utilise ni cookies, ni analyse, ni scripts, et ne charge rien depuis d’autres sites. Il est hébergé sur GitHub Pages et son domaine est géré par Cloudflare. Comme tout hébergeur, ils peuvent conserver des journaux de serveur standard selon leurs propres politiques de confidentialité. Le développeur ne reçoit ni n’utilise ces données."),
            ("h2", "Enfants"),
            ("p", "WakeDeck ne s’adresse pas aux enfants et ne collecte de données de personne."),
            ("h2", "Modifications"),
            ("p", "Si cette politique change, la nouvelle version sera publiée à cette adresse avec une nouvelle date."),
            ("h2", "Contact"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "de": dict(
        title="Datenschutzerklärung", meta_title="Datenschutzerklärung | WakeDeck", date_label="Zuletzt aktualisiert: 11. Oktober 2026",
        desc="WakeDeck erhebt, teilt und verkauft keine personenbezogenen Daten. Keine Werbung, keine Analyse, kein Tracking. Diese Erklärung zeigt, was auf deinem Telefon bleibt.",
        ogdesc="Keine Werbung, keine Analyse, keine Konten. Deine Daten bleiben auf deinem Telefon.",
        note="Diese Übersetzung dient nur der Orientierung. Weicht sie von der englischen Fassung ab, gilt die englische Fassung.",
        summary="<strong>Kurz gesagt:</strong> WakeDeck erhebt, teilt und verkauft keine personenbezogenen Daten und hat keine Werbung, keine Analyse und kein Tracking. Alles, was du eingibst, bleibt auf deinem Telefon.",
        intro="WakeDeck ist eine Wake-on-LAN-App für Android von Diego Bergonsi. Diese Erklärung beschreibt, was die App mit deinen Daten macht.",
        blocks=[
            ("h2", "Was auf deinem Telefon bleibt"),
            ("p", "Alles, was du in WakeDeck eingibst, wird nur auf deinem Telefon gespeichert:"),
            ("ul", [
                "Die Geräte, die du hinzufügst: Name, MAC-Adresse, IP-Adresse oder Hostname, Broadcast-Adresse, Ports, Betriebssystem, Gruppe sowie optional SSH-Benutzername und -Port.",
                "Deine Einstellungen für Widgets, Verknüpfungen und Schnelleinstellungs-Kacheln.",
                "Der zuletzt bekannte Online- oder Offline-Status deiner Geräte.",
                "Für die optionale Fernabschaltung ein SSH-Schlüsselpaar. Der private Schlüssel wird im Android Keystore erzeugt und kann dort weder gelesen noch kopiert werden. Die App fragt nie nach einem Passwort und speichert keines. Sie speichert außerdem den Fingerabdruck des SSH-Host-Schlüssels jedes PCs, dem du vertraut hast.",
            ]),
            ("p", "Nichts davon wird an den Entwickler oder an einen vom Entwickler betriebenen Server gesendet. WakeDeck hat kein Konto, keinen Server und kein Backend. App-Daten sind von der Android-Cloud-Sicherung ausgeschlossen."),
            ("h2", "Netzwerkverkehr"),
            ("p", "WakeDeck verbindet sich nur mit den Adressen, die du einrichtest:"),
            ("ul", [
                "Ein Wake-on-LAN-Magic-Packet (UDP) an die Broadcast-Adresse, die du für ein Gerät festlegst.",
                "Online- oder Offline-Prüfungen: ein TCP-Verbindungsversuch oder ein Ping an den Host des Geräts.",
                "Fernabschaltung, nur wenn du sie antippst und bestätigst: eine SSH-Verbindung zum PC, den du eingerichtet hast.",
            ]),
            ("p", "Dieser Verkehr läuft direkt von deinem Telefon zu deinen eigenen Geräten, über dein WLAN oder über ein VPN, das du selbst betreibst. Er läuft nicht über den Entwickler."),
            ("h2", "Dateien, die du erstellst"),
            ("p", "Die Exportfunktion schreibt deine Geräteliste in eine Datei, die du auswählst. Die Datei enthält weder deinen SSH-Schlüssel noch vertraute Host-Schlüssel. WakeDeck lädt sie nirgendwohin hoch. Was du mit der Datei tust, bleibt dir überlassen."),
            ("p", "Die Importfunktion liest eine Datei, die du auswählst. Bevor etwas hinzugefügt wird, zeigt WakeDeck dir die Geräte der Datei und warnt dich vor solchen, die außerhalb deines Heimnetzwerks liegen."),
            ("h2", "Berechtigungen"),
            ("ul", [
                "<strong>Internet und Netzwerkstatus:</strong> um das Paket zu senden und den Gerätestatus zu prüfen.",
                "<strong>Wake-Lock:</strong> um einen Weckvorgang oder eine Statusprüfung bei ausgeschaltetem Bildschirm abzuschließen.",
                "<strong>Benachrichtigungen:</strong> nur, damit das System der App die kurze Bestätigungsmeldung nach dem Wecken über eine Launcher-Verknüpfung erlaubt. WakeDeck sendet keine Benachrichtigungen, Erinnerungen oder Werbung.",
            ]),
            ("h2", "Dritte"),
            ("p", "WakeDeck enthält keine Werbung, Analyse, Absturzberichte oder andere Dienste von Dritten, die Daten erheben. Es nutzt Open-Source-Bibliotheken, die auf deinem Telefon laufen."),
            ("h2", "Diese Website"),
            ("p", "Diese Website verwendet keine Cookies, keine Analyse und keine Skripte und lädt nichts von anderen Seiten nach. Sie wird auf GitHub Pages gehostet, und ihre Domain wird über Cloudflare verwaltet. Wie jeder Webhost können sie nach ihren eigenen Datenschutzerklärungen übliche Server-Protokolle führen. Der Entwickler erhält diese Daten nicht und nutzt sie nicht."),
            ("h2", "Kinder"),
            ("p", "WakeDeck richtet sich nicht an Kinder und erhebt von niemandem Daten."),
            ("h2", "Änderungen"),
            ("p", "Wenn sich diese Erklärung ändert, wird die neue Fassung unter dieser Adresse mit neuem Datum veröffentlicht."),
            ("h2", "Kontakt"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "es": dict(
        title="Política de privacidad", meta_title="Política de privacidad | WakeDeck", date_label="Última actualización: 11 de octubre de 2026",
        desc="WakeDeck no recopila, comparte ni vende datos personales. No tiene anuncios, analítica ni seguimiento. Esta política explica qué se queda en tu teléfono.",
        ogdesc="Sin anuncios, sin analítica, sin cuentas. Tus datos se quedan en tu teléfono.",
        note="Esta traducción se ofrece a modo informativo. Si difiere de la versión en inglés, prevalece la versión en inglés.",
        summary="<strong>En resumen:</strong> WakeDeck no recopila, comparte ni vende datos personales, y no tiene anuncios, analítica ni seguimiento. Todo lo que introduces se queda en tu teléfono.",
        intro="WakeDeck es una app Wake-on-LAN para Android creada por Diego Bergonsi. Esta política explica qué hace la app con tus datos.",
        blocks=[
            ("h2", "Qué se queda en tu teléfono"),
            ("p", "Todo lo que introduces en WakeDeck se guarda solo en tu teléfono:"),
            ("ul", [
                "Los dispositivos que añades: nombre, dirección MAC, dirección IP o nombre de host, dirección de difusión, puertos, sistema operativo, grupo y, opcionalmente, usuario y puerto SSH.",
                "Los ajustes de tus widgets, accesos directos y mosaicos de Ajustes rápidos.",
                "El último estado conocido, en línea o sin conexión, de tus dispositivos.",
                "Para el apagado remoto opcional, un par de claves SSH. La clave privada se crea dentro del Android Keystore y no se puede leer ni copiar desde allí. La app nunca pide ni guarda contraseñas. También guarda la huella de la clave de host SSH de cada PC en el que decidiste confiar.",
            ]),
            ("p", "Nada de esto se envía al desarrollador ni a ningún servidor del desarrollador. WakeDeck no tiene cuenta, servidor ni back-end. Los datos de la app quedan excluidos de la copia de seguridad en la nube de Android."),
            ("h2", "Tráfico de red"),
            ("p", "WakeDeck solo se conecta a las direcciones que configuras:"),
            ("ul", [
                "Un paquete mágico Wake-on-LAN (UDP) a la dirección de difusión que estableces para un dispositivo.",
                "Comprobaciones en línea o sin conexión: un intento de conexión TCP o un ping al host del dispositivo.",
                "El apagado remoto, solo cuando lo tocas y confirmas: una conexión SSH al PC que configuraste.",
            ]),
            ("p", "Este tráfico va directamente de tu teléfono a tus propios dispositivos, por tu Wi-Fi o por una VPN que gestionas tú. No pasa por el desarrollador."),
            ("h2", "Archivos que decides crear"),
            ("p", "La función de exportar escribe tu lista de dispositivos en un archivo que eliges. El archivo no contiene tu clave SSH ni las claves de host de confianza. WakeDeck no lo sube a ningún sitio. Lo que hagas con el archivo depende de ti."),
            ("p", "La función de importar lee un archivo que eliges. Antes de añadir nada, WakeDeck te muestra los dispositivos del archivo y te avisa de los que apuntan fuera de tu red doméstica."),
            ("h2", "Permisos"),
            ("ul", [
                "<strong>Internet y estado de la red:</strong> para enviar el paquete y comprobar el estado de los dispositivos.",
                "<strong>Wake lock:</strong> para terminar un encendido o una comprobación de estado con la pantalla apagada.",
                "<strong>Notificaciones:</strong> solo para que el sistema permita a la app mostrar su breve mensaje de confirmación tras encender desde un acceso directo del launcher. WakeDeck no envía notificaciones, recordatorios ni promociones.",
            ]),
            ("h2", "Terceros"),
            ("p", "WakeDeck no contiene publicidad, analítica, informes de errores ni otros servicios de terceros que recopilen datos. Usa bibliotecas de código abierto que se ejecutan en tu teléfono."),
            ("h2", "Este sitio web"),
            ("p", "Este sitio no usa cookies, analítica ni scripts, y no carga nada de otros sitios. Está alojado en GitHub Pages y su dominio se gestiona a través de Cloudflare. Como cualquier alojamiento web, pueden conservar registros estándar del servidor según sus propias políticas de privacidad. El desarrollador no recibe ni usa esos datos."),
            ("h2", "Menores"),
            ("p", "WakeDeck no está dirigida a menores y no recopila datos de nadie."),
            ("h2", "Cambios"),
            ("p", "Si esta política cambia, la nueva versión se publicará en esta dirección con una nueva fecha."),
            ("h2", "Contacto"),
            ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "zh": dict(
        title="隐私政策", meta_title="隐私政策 | WakeDeck", date_label="最后更新：2026 年 10 月 11 日",
        desc="WakeDeck 不收集、不分享、不出售个人数据，没有广告、统计或跟踪。本政策说明哪些内容只保存在你的手机上。",
        ogdesc="无广告、无统计、无需账号。你的数据只保存在手机上。",
        note="本译文仅供参考。如与英文版本有差异，以英文版本为准。",
        summary="<strong>简而言之：</strong>WakeDeck 不收集、不分享、不出售任何个人数据，没有广告、统计或跟踪。你输入的所有内容都只保存在你的手机上。",
        intro="WakeDeck 是由 Diego Bergonsi 开发的 Android 版 Wake-on-LAN 应用。本政策说明应用如何处理你的数据。",
        blocks=[
            ("h2", "保存在你手机上的内容"),
            ("p", "你在 WakeDeck 中输入的所有内容都只保存在你的手机上："),
            ("ul", [
                "你添加的设备：名称、MAC 地址、IP 地址或主机名、广播地址、端口、操作系统、分组，以及可选的 SSH 用户名和端口。",
                "你的小组件、快捷方式和快速设置磁贴的设置。",
                "设备最近一次已知的在线或离线状态。",
                "可选的远程关机功能所用的 SSH 密钥对。私钥在 Android Keystore 内创建，无法从中读取或复制。应用从不索取也不保存密码。应用还会保存你选择信任的每台电脑的 SSH 主机密钥指纹。",
            ]),
            ("p", "以上内容都不会发送给开发者，也不会发送到开发者运营的任何服务器。WakeDeck 没有账号、服务器或后端。应用数据不会参与 Android 云备份。"),
            ("h2", "网络流量"),
            ("p", "WakeDeck 只会连接你配置的地址："),
            ("ul", [
                "向你为设备设置的广播地址发送 Wake-on-LAN 魔术包（UDP）。",
                "在线或离线检测：对设备主机发起 TCP 连接尝试或 ping。",
                "远程关机，仅在你点按并确认后进行：与你设置的电脑建立 SSH 连接。",
            ]),
            ("p", "这些流量直接从你的手机发往你自己的设备，经由你的 Wi-Fi 或你自己运行的 VPN，不会经过开发者。"),
            ("h2", "你选择创建的文件"),
            ("p", "导出功能会把你的设备列表写入你选择的文件。该文件不包含你的 SSH 密钥或已信任的主机密钥。WakeDeck 不会上传该文件。如何处理该文件由你决定。"),
            ("p", "导入功能会读取你选择的文件。在添加任何内容之前，WakeDeck 会向你显示文件中的设备，并对指向家庭网络之外的设备发出警告。"),
            ("h2", "权限"),
            ("ul", [
                "<strong>互联网和网络状态：</strong>用于发送数据包和检测设备状态。",
                "<strong>唤醒锁：</strong>用于在屏幕关闭时完成唤醒或状态检测。",
                "<strong>通知：</strong>仅用于让系统允许应用在通过启动器快捷方式唤醒后显示简短的确认消息。WakeDeck 不会发送通知、提醒或推广信息。",
            ]),
            ("h2", "第三方"),
            ("p", "WakeDeck 不含任何会收集数据的广告、统计、崩溃报告或其他第三方服务。它使用在你手机上运行的开源库。"),
            ("h2", "本网站"),
            ("p", "本网站不使用 Cookie、统计或脚本，也不从其他网站加载任何内容。网站托管在 GitHub Pages，域名通过 Cloudflare 管理。与任何网站托管方一样，它们可能依据各自的隐私政策保留标准服务器日志。开发者不会接收或使用这些数据。"),
            ("h2", "儿童"),
            ("p", "WakeDeck 不面向儿童，也不收集任何人的数据。"),
            ("h2", "变更"),
            ("p", "如本政策有变更，新版本将在此地址发布，并标注新的日期。"),
            ("h2", "联系方式"),
            ("p", 'Diego Bergonsi，<a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
    "ja": dict(
        title="プライバシーポリシー", meta_title="プライバシーポリシー | WakeDeck", date_label="最終更新日：2026 年 10 月 11 日",
        desc="WakeDeck は個人データを収集せず、共有も販売もしません。広告、分析、トラッキングはありません。このポリシーでは、スマートフォンに何が保存されるかを説明します。",
        ogdesc="広告なし、分析なし、アカウント不要。データはスマートフォンの中に残ります。",
        note="この翻訳は参考用です。英語版と異なる場合は、英語版が優先されます。",
        summary="<strong>要点：</strong>WakeDeck は個人データを収集せず、共有も販売もしません。広告、分析、トラッキングもありません。入力した内容はすべてスマートフォンの中に残ります。",
        intro="WakeDeck は Diego Bergonsi が開発した Android 用の Wake-on-LAN アプリです。このポリシーでは、アプリがあなたのデータをどのように扱うかを説明します。",
        blocks=[
            ("h2", "スマートフォンに保存されるもの"),
            ("p", "WakeDeck に入力した内容は、すべてスマートフォンの中にのみ保存されます："),
            ("ul", [
                "追加したデバイス：名前、MAC アドレス、IP アドレスまたはホスト名、ブロードキャストアドレス、ポート、OS、グループ、および任意の SSH ユーザー名とポート。",
                "ウィジェット、ショートカット、クイック設定タイルの設定。",
                "デバイスの最後に確認されたオンライン／オフライン状態。",
                "任意のリモートシャットダウン機能のための SSH 鍵ペア。秘密鍵は Android Keystore の内部で作成され、そこから読み取ったりコピーしたりすることはできません。アプリはパスワードを求めることも保存することもありません。また、信頼すると選んだ各 PC の SSH ホスト鍵のフィンガープリントを保存します。",
            ]),
            ("p", "これらは開発者にも、開発者が運営するサーバーにも送信されません。WakeDeck にはアカウントもサーバーもバックエンドもありません。アプリのデータは Android のクラウドバックアップの対象外です。"),
            ("h2", "ネットワーク通信"),
            ("p", "WakeDeck は、あなたが設定したアドレスにのみ接続します："),
            ("ul", [
                "デバイスに設定したブロードキャストアドレスへの Wake-on-LAN マジックパケット（UDP）。",
                "オンライン／オフラインの確認：デバイスのホストへの TCP 接続の試行または ping。",
                "リモートシャットダウン（タップして確認したときのみ）：設定した PC への SSH 接続。",
            ]),
            ("p", "この通信は、スマートフォンからあなた自身のデバイスへ、Wi-Fi またはご自身で運用する VPN を通じて直接行われます。開発者を経由することはありません。"),
            ("h2", "作成するファイル"),
            ("p", "エクスポート機能は、デバイス一覧をあなたが選んだファイルに書き出します。このファイルには SSH 鍵や信頼済みホスト鍵は含まれません。WakeDeck がファイルをアップロードすることはありません。ファイルの扱いはあなたに任されています。"),
            ("p", "インポート機能は、あなたが選んだファイルを読み込みます。何かを追加する前に、WakeDeck はファイル内のデバイスを表示し、自宅ネットワークの外を指しているものについて警告します。"),
            ("h2", "権限"),
            ("ul", [
                "<strong>インターネットとネットワーク状態：</strong>パケットの送信とデバイス状態の確認のため。",
                "<strong>ウェイクロック：</strong>画面がオフのときに、起動や状態確認を最後まで行うため。",
                "<strong>通知：</strong>ランチャーのショートカットから起動した後に、システムがアプリの短い確認メッセージを表示できるようにするためだけに使います。WakeDeck は通知、リマインダー、宣伝を送りません。",
            ]),
            ("h2", "第三者"),
            ("p", "WakeDeck には、データを収集する広告、分析、クラッシュレポート、その他の第三者サービスは含まれていません。スマートフォン上で動作するオープンソースライブラリを使用しています。"),
            ("h2", "このウェブサイト"),
            ("p", "このウェブサイトは Cookie、分析、スクリプトを使用せず、他のサイトから何も読み込みません。サイトは GitHub Pages でホストされ、ドメインは Cloudflare で管理されています。一般的なウェブホストと同様に、それぞれのプライバシーポリシーに従って標準的なサーバーログが保持される場合があります。開発者がそのデータを受け取ったり利用したりすることはありません。"),
            ("h2", "お子様"),
            ("p", "WakeDeck はお子様向けではなく、誰からもデータを収集しません。"),
            ("h2", "変更"),
            ("p", "このポリシーが変更された場合は、新しい版を新しい日付とともにこのアドレスで公開します。"),
            ("h2", "お問い合わせ"),
            ("p", 'Diego Bergonsi、<a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
        ],
    ),
}


# ---------------------------------------------------------------- support (tip) section
SUPPORT = {
    "en": ("Support WakeDeck", "WakeDeck is free and has no ads. If it saves you time and you want to chip in, a donation helps cover the Play Store fee and the domain.", "Make a donation"),
    "pt-br": ("Apoie o WakeDeck", "O WakeDeck é gratuito e não tem anúncios. Se ele poupa o seu tempo e você quer ajudar, uma doação ajuda a cobrir a taxa da Play Store e o domínio.", "Fazer uma doação"),
    "pt-pt": ("Apoie a WakeDeck", "A WakeDeck é gratuita e não tem anúncios. Se lhe poupa tempo e quiser ajudar, um donativo ajuda a cobrir a taxa da Play Store e o domínio.", "Fazer um donativo"),
    "fr": ("Soutenir WakeDeck", "WakeDeck est gratuit et sans publicité. S’il vous fait gagner du temps et que vous souhaitez contribuer, un don aide à couvrir les frais du Play Store et le nom de domaine.", "Faire un don"),
    "de": ("WakeDeck unterstützen", "WakeDeck ist kostenlos und werbefrei. Wenn es dir Zeit spart und du etwas beitragen möchtest, hilft eine Spende, die Play-Store-Gebühr und die Domain zu decken.", "Spenden"),
    "es": ("Apoya WakeDeck", "WakeDeck es gratis y no tiene anuncios. Si te ahorra tiempo y quieres colaborar, una donación ayuda a cubrir la tarifa de Play Store y el dominio.", "Hacer una donación"),
    "zh": ("支持 WakeDeck", "WakeDeck 免费且没有广告。如果它为你节省了时间，你也想表示支持，一笔捐赠能帮助支付 Play 商店费用和域名费用。", "捐赠"),
    "ja": ("WakeDeck を応援", "WakeDeck は無料で、広告もありません。お役に立てて、応援したいと思っていただけたら、寄付が Play ストアの手数料とドメイン代の足しになります。", "寄付する"),
}

# One privacy block per language, shown after "Third parties".
TIPS_BLOCK = {
    "en": ("Donations", "The donation button on this website opens Ko-fi, a third-party site with its own privacy policy. The app has no donation feature. Ko-fi and its payment providers handle any donation, and the developer sees only what Ko-fi shows for it, such as the name and message you choose to give."),
    "pt-br": ("Doações", "O botão de doação deste site abre o Ko-fi, um site de terceiros com a própria política de privacidade. O app não tem recurso de doação. O Ko-fi e os seus provedores de pagamento cuidam de qualquer doação, e o desenvolvedor vê apenas o que o Ko-fi mostra, como o nome e a mensagem que você decidir informar."),
    "pt-pt": ("Donativos", "O botão de donativo deste sítio abre o Ko-fi, um sítio de terceiros com a sua própria política de privacidade. A aplicação não tem funcionalidade de donativos. O Ko-fi e os seus fornecedores de pagamento tratam de qualquer donativo, e o programador vê apenas o que o Ko-fi mostra, como o nome e a mensagem que decidir indicar."),
    "fr": ("Dons", "Le bouton de don de ce site ouvre Ko-fi, un site tiers doté de sa propre politique de confidentialité. L’application n’a aucune fonction de don. Ko-fi et ses prestataires de paiement gèrent tout don, et le développeur ne voit que ce que Ko-fi lui indique, comme le nom et le message que vous choisissez de donner."),
    "de": ("Spenden", "Die Spenden-Schaltfläche auf dieser Website öffnet Ko-fi, eine Seite eines Drittanbieters mit eigener Datenschutzerklärung. Die App hat keine Spendenfunktion. Ko-fi und dessen Zahlungsdienstleister wickeln jede Spende ab, und der Entwickler sieht nur, was Ko-fi dazu anzeigt, etwa den Namen und die Nachricht, die du angibst."),
    "es": ("Donaciones", "El botón de donación de este sitio abre Ko-fi, un sitio de terceros con su propia política de privacidad. La app no tiene función de donaciones. Ko-fi y sus proveedores de pago gestionan cualquier donación, y el desarrollador solo ve lo que Ko-fi muestra, como el nombre y el mensaje que decidas indicar."),
    "zh": ("捐赠", "本网站上的捐赠按钮会打开 Ko-fi，这是一个有自己隐私政策的第三方网站。应用本身没有捐赠功能。任何捐赠均由 Ko-fi 及其支付服务商处理，开发者只能看到 Ko-fi 显示的信息，例如你选择提供的姓名和留言。"),
    "ja": ("寄付", "このウェブサイトの寄付ボタンは、独自のプライバシーポリシーを持つ第三者サイト Ko-fi を開きます。アプリ自体に寄付機能はありません。寄付の処理は Ko-fi とその決済事業者が行い、開発者に見えるのは、あなたが入力した名前やメッセージなど Ko-fi が表示する情報のみです。"),
}


THIRD_PARTIES = {"Third parties", "Terceiros", "Tiers", "Dritte", "Terceros", "第三方", "第三者"}

# Further languages live one per file in tools/site_i18n/. Each defines KEY, CODE, NAME, INDEX, PRIVACY, THIRD,
# SUPPORT, TIPS, GUIDE and optionally RTL.
RTL_LANGS = set()


def _load_extra_languages():
    import importlib.util
    order = "it ru ko ar tr vi id pl sv da nb fi".split()
    for f in sorted((pathlib.Path(__file__).resolve().parent / "site_i18n").glob("*.py"), key=lambda f: order.index(f.stem)):
        spec = importlib.util.spec_from_file_location(f"site_i18n_{f.stem}", f)
        m = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(m)
        LANGS.append((m.KEY, m.CODE, m.NAME))
        INDEX[m.KEY], PRIVACY[m.KEY], SUPPORT[m.KEY], TIPS_BLOCK[m.KEY] = m.INDEX, m.PRIVACY, m.SUPPORT, m.TIPS
        GUIDE_META[m.KEY] = m.GUIDE
        THIRD_PARTIES.add(m.THIRD)
        if getattr(m, "RTL", False):
            RTL_LANGS.add(m.KEY)


def base(folder):
    return f"/{folder}/" if folder else "/"


# ---------------------------------------------------------------- guide page (text comes from the app's Help screen)
RES = pathlib.Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res"
RES_DIR = {"": "values", "pt-br": "values-pt-rBR", "pt-pt": "values-pt-rPT", "id": "values-in"}
PAGE_SUFFIX = {"index": "", "privacy": "privacy", "guide": "guide"}

GUIDE_META = {
    "en": ("Turn on Wake-on-LAN and remote shutdown on your Linux, Windows or Mac PC so WakeDeck can wake it from your phone.",
           "Replace PASTE_KEY_LINE_FROM_APP with the key line shown in WakeDeck\u2019s SSH key screen."),
    "pt-br": ("Ative o Wake-on-LAN e o desligamento remoto no seu PC com Linux, Windows ou Mac para o WakeDeck ligá-lo pelo celular.",
              "Troque PASTE_KEY_LINE_FROM_APP pela linha da chave mostrada na tela Chave SSH do WakeDeck."),
    "pt-pt": ("Ative o Wake-on-LAN e o encerramento remoto no seu PC com Linux, Windows ou Mac para o WakeDeck o ligar a partir do telemóvel.",
              "Troque PASTE_KEY_LINE_FROM_APP pela linha da chave mostrada no ecrã Chave SSH do WakeDeck."),
    "fr": ("Activez Wake-on-LAN et l’extinction à distance sur votre PC Linux, Windows ou Mac pour que WakeDeck puisse l’allumer depuis votre téléphone.",
           "Remplacez PASTE_KEY_LINE_FROM_APP par la ligne de clé affichée dans l’écran Clé SSH de WakeDeck."),
    "de": ("Aktiviere Wake-on-LAN und das Herunterfahren aus der Ferne auf deinem Linux-, Windows- oder Mac-PC, damit WakeDeck ihn von deinem Telefon aus aufwecken kann.",
           "Ersetze PASTE_KEY_LINE_FROM_APP durch die Schlüsselzeile aus dem Bildschirm SSH-Schlüssel in WakeDeck."),
    "es": ("Activa Wake-on-LAN y el apagado remoto en tu PC con Linux, Windows o Mac para que WakeDeck pueda encenderlo desde tu teléfono.",
           "Sustituye PASTE_KEY_LINE_FROM_APP por la línea de clave que muestra la pantalla Clave SSH de WakeDeck."),
    "zh": ("在你的 Linux、Windows 或 Mac 电脑上开启 Wake-on-LAN 和远程关机，让 WakeDeck 可以从手机唤醒它。",
           "请把 PASTE_KEY_LINE_FROM_APP 替换为 WakeDeck 的 SSH 密钥界面中显示的密钥行。"),
    "ja": ("Linux、Windows、Mac の PC で Wake-on-LAN とリモートシャットダウンを有効にして、WakeDeck からスマートフォンで起動できるようにします。",
           "PASTE_KEY_LINE_FROM_APP は、WakeDeck の SSH 鍵画面に表示される鍵の行に置き換えてください。"),
}
KEY_PLACEHOLDER = "PASTE_KEY_LINE_FROM_APP"
_load_extra_languages()


def _unescape(v):
    v = re.sub(r"\\u([0-9a-fA-F]{4})", lambda m: chr(int(m.group(1), 16)), v)
    return re.sub(r"\\(.)", lambda m: "\n" if m.group(1) == "n" else m.group(1), v)


def load_strings(folder):
    """Android string resources for a language, falling back to the default file."""
    out = {}
    names = ["values/strings.xml", "values/commands.xml"]
    if folder:
        names.append(RES_DIR.get(folder, f"values-{folder}") + "/strings.xml")
    for n in names:
        for el in ET.parse(RES / n).getroot().iter("string"):
            assert len(el) == 0, el.get("name")
            out[el.get("name")] = _unescape(el.text or "")
    return out


def guide_label(folder):
    g = load_strings(folder)["empty_devices_help"]
    return g[:1].upper() + g[1:]


def head(lang_code, folder, page, title, desc, ogdesc):
    suffix = PAGE_SUFFIX[page]
    path = base(folder) + suffix
    alternates = "\n".join(
        f'<link rel="alternate" hreflang="{code}" href="{SITE}{base(f)}{suffix}">'
        for f, code, _ in LANGS
    )
    alternates += f'\n<link rel="alternate" hreflang="x-default" href="{SITE}/{suffix}">'
    return f"""<!doctype html>
<html lang="{lang_code}"{' dir="rtl"' if folder in RTL_LANGS else ""}>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{html.escape(title)}</title>
<meta name="description" content="{html.escape(desc)}">
<meta name="color-scheme" content="dark">
<meta name="theme-color" content="#161E56">
<link rel="canonical" href="{SITE}{path}">
{alternates}
<link rel="icon" type="image/png" href="/favicon.png">
<link rel="apple-touch-icon" href="/apple-touch-icon.png">
<meta property="og:type" content="website">
<meta property="og:title" content="{html.escape(title)}">
<meta property="og:description" content="{html.escape(ogdesc)}">
<meta property="og:url" content="{SITE}{path}">
<meta property="og:image" content="{SITE}/assets/og.png">
<link rel="stylesheet" href="/assets/site.css?v={CSS_VERSION}">
</head>"""


def header(t, folder, page):
    b = base(folder)
    items = []
    for f, code, name in LANGS:
        href = base(f) + PAGE_SUFFIX[page]
        cur = ' aria-current="true"' if f == folder else ""
        items.append(f'<li><a href="{href}" lang="{code}" hreflang="{code}"{cur}>{name}</a></li>')
    current = next(n for f, c, n in LANGS if f == folder)
    cur_priv = ' aria-current="page"' if page == "privacy" else ""
    cur_guide = ' aria-current="page"' if page == "guide" else ""
    return f"""<body>
<a class="skip" href="#main">{t["skip"]}</a>

<header>
  <div class="wrap">
    <a class="brand" href="{b}" aria-label="WakeDeck"><b>Wake</b><span>Deck</span></a>
    <nav aria-label="Main">
      <a class="btn donate nav-donate" href="{KOFI}" rel="noopener noreferrer">{SUPPORT[folder or "en"][2]}</a>
      <a class="opt" href="{b}#features">{t["features"]}</a>
      <a class="opt" href="{b}#how">{t["how"]}</a>
      <a href="{b}guide"{cur_guide}>{t["guide"]}</a>
      <a href="{b}privacy"{cur_priv}>{t["privacy"]}</a>
      <details class="lang">
        <summary aria-label="{t["language"]}"><svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.6 2.6 4 5.6 4 9s-1.4 6.4-4 9c-2.6-2.6-4-5.6-4-9s1.4-6.4 4-9z"/></svg><span>{current}</span></summary>
        <ul>
          {chr(10).join("          " + i for i in items).strip()}
        </ul>
      </details>
    </nav>
  </div>
</header>
"""


def footer(t, folder):
    b = base(folder)
    return f"""
<footer>
  <div class="wrap">
    <div>&copy; 2026 Diego Bergonsi</div>
    <ul>
      <li><a href="{b}guide">{t["guide"]}</a></li>
      <li><a href="{b}privacy">{t["privacy"]}</a></li>
      <li><a href="{KOFI}" rel="noopener noreferrer">{SUPPORT[folder or "en"][2]}</a></li>
      <li><a href="mailto:contact@wakedeck.app">{t["contact"]}</a></li>
    </ul>
  </div>
</footer>
</body>
</html>
"""


def build_index(folder, code):
    t = INDEX[folder or "en"]
    pre, em, post = t["h1"]
    cards = "\n".join(f'      <article class="card">\n        <h3>{h}</h3>\n        <p>{p}</p>\n      </article>' for h, p in t["cards"])
    steps = "\n".join(f"      <li><h3>{h}</h3><p>{p}</p></li>" for h, p in t["steps"])
    b = base(folder)
    return head(code, folder, "index", t["title"], t["desc"], t["ogdesc"]) + "\n" + header(t, folder, "index") + f"""
<main id="main">
  <div class="wrap hero">
    <div>
      <h1>{pre}<em>{em}</em>{post}</h1>
      <p class="lead">{t["lead"]}</p>
      <div class="actions">
        <span class="btn primary" aria-disabled="true">{t["play"]}</span>
        <a class="btn donate" href="{KOFI}" rel="noopener noreferrer">{SUPPORT[folder or "en"][2]}</a>
      </div>
      <p class="soon">{t["soon"]}</p>
    </div>
    <img src="/assets/hero.webp" width="720" height="720" alt="WakeDeck">
  </div>

  <section id="features" class="wrap">
    <h2>{t["feat_h"]}</h2>
    <p class="sub">{t["feat_sub"]}</p>
    <div class="grid">
{cards}
    </div>
  </section>

  <section id="how" class="wrap">
    <h2>{t["how_h"]}</h2>
    <p class="sub">{t["how_sub"]}</p>
    <ol class="steps">
{steps}
    </ol>
  </section>

  <div class="band">
    <div class="wrap">
      <section class="inner" aria-labelledby="privacy-h">
        <div>
          <h2 id="privacy-h">{t["priv_h"]}</h2>
          <p>{t["priv_p"]}</p>
        </div>
        <a class="btn ghost" href="{b}privacy">{t["priv_btn"]}</a>
      </section>
    </div>
  </div>

  <section class="wrap">
    <h2>{t["away_h"]}</h2>
    <p class="sub">{t["away_p"]}</p>
  </section>

  <section class="wrap support" aria-labelledby="support-h">
    <h2 id="support-h">{SUPPORT[folder or "en"][0]}</h2>
    <p class="sub">{SUPPORT[folder or "en"][1]}</p>
    <a class="btn donate" href="{KOFI}" rel="noopener noreferrer">{SUPPORT[folder or "en"][2]}</a>
  </section>
</main>
""" + footer(t, folder)


def build_privacy(folder, code):
    p = PRIVACY[folder or "en"]
    t = INDEX[folder or "en"]
    out = []
    blocks = list(p["blocks"])
    third = next(i for i, b in enumerate(blocks) if b[0] == "h2" and b[1] in THIRD_PARTIES)
    blocks[third + 2:third + 2] = [("h2", TIPS_BLOCK[folder or "en"][0]), ("p", TIPS_BLOCK[folder or "en"][1])]
    for kind, val in blocks:
        if kind == "h2":
            out.append(f"    <h2>{val}</h2>")
        elif kind == "p":
            out.append(f"    <p>{val}</p>")
        else:
            out.append("    <ul>\n" + "\n".join(f"      <li>{i}</li>" for i in val) + "\n    </ul>")
    note = f'    <p class="note">{p["note"]}</p>\n' if p["note"] else ""
    return head(code, folder, "privacy", p["meta_title"], p["desc"], p["ogdesc"]) + "\n" + header(t, folder, "privacy") + f"""
<main id="main">
  <div class="wrap page-title">
    <h1>{p["title"]}</h1>
    <p class="meta">{p["date_label"]}</p>
  </div>

  <div class="wrap prose">
    <div class="summary">
      <p>{p["summary"]}</p>
    </div>
{note}
    <p>{p["intro"]}</p>

{chr(10).join(out)}
  </div>
</main>
""" + footer(t, folder)


def build_guide(folder, code):
    t = INDEX[folder or "en"]
    g = load_strings(folder)
    desc, key_note = GUIDE_META[folder or "en"]
    e = html.escape

    def body(name):
        return f"    <p>{e(g[name])}</p>"

    def cmd(name):
        text = g[name].replace("%1$s", KEY_PLACEHOLDER)
        return f"    <pre><code>{e(text)}</code></pre>"

    def os_h3(slug, name):
        return f'    <h3 id="{slug}">{e(g[name])}</h3>'

    def label(name):
        return f"    <p><strong>{e(g[name])}</strong></p>"

    wol = [body("help_wol_intro")]
    wol += [os_h3("wol-linux", "help_os_linux"), body("help_wol_bios"),
            body("help_wol_l1"), cmd("cmd_linux_list_ifaces"), body("help_wol_l2"), cmd("cmd_linux_check_wol"),
            body("help_wol_l3"), cmd("cmd_linux_set_wol"), body("help_wol_l4"), cmd("cmd_linux_persist_wol")]
    wol += [os_h3("wol-windows", "help_os_windows"), body("help_wol_bios"),
            body("help_wol_w1"), body("help_wol_w2"), body("help_wol_w3")]
    wol += [os_h3("wol-macos", "help_os_macos"), body("help_wol_m1"), body("help_wol_m2")]
    wol += [body("help_wol_app"),
            label("help_os_linux"), cmd("cmd_linux_list_ifaces"),
            label("help_os_windows"), cmd("cmd_win_ipconfig"),
            label("help_os_macos"), cmd("cmd_mac_networksetup"),
            body("help_wol_app2")]

    ssh = [body("help_ssh_intro")]
    ssh += [os_h3("ssh-linux", "help_os_linux"),
            body("help_ssh_l1"), cmd("cmd_linux_install_ssh"), body("help_ssh_l1b"), cmd("cmd_linux_enable_sshd"),
            body("help_ssh_l2"), cmd("cmd_linux_add_key"),
            body("help_ssh_l3"), cmd("cmd_linux_sudoers"), cmd("cmd_linux_sudoers_line")]
    ssh += [os_h3("ssh-windows", "help_os_windows"),
            body("help_ssh_w1"), cmd("cmd_win_install_ssh"), body("help_ssh_w2"),
            cmd("cmd_win_add_admin_key"), cmd("cmd_win_lock_admin_keys"), body("help_ssh_w3"), body("help_ssh_w4")]
    ssh += [os_h3("ssh-macos", "help_os_macos"),
            body("help_ssh_m1"), body("help_ssh_l2"), cmd("cmd_linux_add_key"),
            body("help_ssh_m3"), cmd("cmd_linux_sudoers"), cmd("cmd_mac_sudoers_line")]
    ssh += [body("help_ssh_app"),
            label("help_os_linux"), cmd("cmd_linux_hostkey"),
            label("help_os_windows"), cmd("cmd_win_hostkey"),
            label("help_os_macos"), cmd("cmd_linux_hostkey"),
            body("help_ssh_trouble")]

    title = guide_label(folder)
    return head(code, folder, "guide", f"{title} | WakeDeck", desc, desc) + "\n" + header(t, folder, "guide") + f"""
<main id="main">
  <div class="wrap page-title">
    <h1>{e(title)}</h1>
    <p class="meta">{e(g["help_intro"])}</p>
  </div>

  <div class="wrap prose">
    <h2 id="wol">{e(g["help_s1"])}</h2>
{chr(10).join(wol)}

    <h2 id="ssh">{e(g["help_s2"])}</h2>
    <p class="note">{e(key_note)}</p>
{chr(10).join(ssh)}

    <h2 id="away">{e(g["help_s3"])}</h2>
{body("help_remote")}
{body("help_remote_errors")}
  </div>
</main>
""" + footer(t, folder)


def main():
    for folder, code, _ in LANGS:
        INDEX[folder or "en"]["guide"] = guide_label(folder)
        target = ROOT / folder if folder else ROOT
        target.mkdir(exist_ok=True)
        (target / "index.html").write_text(build_index(folder, code), encoding="utf-8")
        (target / "privacy.html").write_text(build_privacy(folder, code), encoding="utf-8")
        (target / "guide.html").write_text(build_guide(folder, code), encoding="utf-8")
        print("built", folder or "en")


if __name__ == "__main__":
    main()

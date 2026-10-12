KEY, CODE, NAME = "da", "da", "Dansk"

INDEX = dict(
    title="WakeDeck: Wake-on-LAN-widgets til Android",
    desc="Væk dine pc’er fra Androids startskærm. Widgets, genveje og felter i Hurtige indstillinger, som du selv kan tilpasse. Ingen reklamer, ingen analyse, ingen konti.",
    ogdesc="Væk dine enheder fra din smartphone. Widgets til startskærmen, som du selv kan tilpasse. Ingen reklamer, ingen analyse, ingen konti.",
    skip="Gå til indhold", language="Sprog", features="Funktioner", how="Sådan virker det", privacy="Privatliv", contact="Kontakt",
    h1=("Væk dine enheder fra din ", "smartphone", ""),
    lead="WakeDeck tænder dine pc’er med en Wake-on-LAN-magisk pakke, direkte fra widgets, du selv designer. Ét tryk på startskærmen, og appen behøver ikke engang at åbne.",
    play="Google Play Store: kommer snart", soon="Gratis til Android 8.0 og nyere. Ingen reklamer, ingen analyse, ingen konti.",
    feat_h="Bygget omkring widgetten", feat_sub="Din startskærm er fjernbetjeningen. Vælg, hvordan den ser ud, og hvor den sidder.",
    cards=[
        ("Widgets, du selv designer", "Én enhed, en liste eller et gitter. Vælg farver, gennemsigtighed, hjørneradius, kant, ikon, tekststørrelse og layout. Status online og offline opdateres i baggrunden."),
        ("Ét tryk, uden at åbne appen", "Tryk på Tænd i selve widgetten. Du kan også tænde fra genveje i launcheren og felter i Hurtige indstillinger."),
        ("Se, hvad der er tændt", "Se, hvilke enheder der er online. Opdater dem alle med en knap, eller træk listen nedad."),
        ("Grupper og Tænd alle", "Saml enheder i grupper, for eksempel Kontor eller Lab, og væk en hel gruppe med én knap."),
        ("Fjernlukning via SSH", "Valgfrit. WakeDeck logger ind med en nøgle, der ligger i Android Keystore. Appen beder aldrig om og gemmer aldrig en adgangskode, og den fastlåser værtsnøglen for hver pc."),
        ("Privat fra bunden", "Ingen reklamer, ingen analyse, ingen konti. Dine enheder og indstillinger bliver på telefonen, og appen taler kun med de adresser, du tilføjer."),
    ],
    how_h="Tre trin", how_sub="En guide i appen fører dig igennem pc-siden, til Windows og Linux.",
    steps=[
        ("Slå Wake-on-LAN til", "Slå det til i pc’ens BIOS eller UEFI og i netværkskortets indstillinger. Kablet Ethernet virker bedst."),
        ("Tilføj din enhed", "Skriv et navn og MAC-adressen. Tilføj IP-adressen, hvis du vil se status online."),
        ("Tilføj en widget", "Tryk længe på startskærmen, tilføj en WakeDeck-widget, og tilpas den, som du vil."),
    ],
    priv_h="Dine data er dine", priv_p="WakeDeck har ingen server og ingen konto. Appen indsamler eller deler ikke personlige oplysninger. Læs den korte privatlivspolitik for at se præcis, hvad der gemmes på din telefon.", priv_btn="Privatlivspolitik",
    away_h="Væk hjemmefra?", away_p="Brug din egen VPN, og sæt enhedens broadcast-adresse til dit undernets, for eksempel 192.168.1.255. Din VPN og din router skal videresende den. Hjælpen i appen forklarer detaljerne og de mest almindelige fejlmeddelelser.",
)

PRIVACY = dict(
    title="Privatlivspolitik", meta_title="Privatlivspolitik | WakeDeck", date_label="Senest opdateret: 11. oktober 2026",
    desc="WakeDeck indsamler, deler eller sælger ikke personoplysninger. Ingen reklamer, analyse eller sporing. Politikken forklarer, hvad der bliver på din telefon.",
    ogdesc="Ingen reklamer, ingen analyse, ingen konti. Dine data bliver på din telefon.",
    note="Denne oversættelse er stillet til rådighed for nemheds skyld. Hvis den afviger fra den engelske version, gælder den engelske version.",
    summary="<strong>Kort sagt:</strong> WakeDeck indsamler, deler eller sælger ingen personoplysninger og har ingen reklamer, analyse eller sporing. Alt, hvad du indtaster, bliver på din telefon.",
    intro="WakeDeck er en Wake-on-LAN-app til Android lavet af Diego Bergonsi. Denne politik forklarer, hvad appen gør med dine data.",
    blocks=[
        ("h2", "Hvad der bliver på din telefon"),
        ("p", "Alt, hvad du indtaster i WakeDeck, gemmes kun på din telefon:"),
        ("ul", [
            "De enheder, du tilføjer: navn, MAC-adresse, IP-adresse eller værtsnavn, broadcast-adresse, porte, operativsystem, gruppe samt valgfri SSH-bruger og SSH-port.",
            "Dine indstillinger for widgets, genveje og felter i Hurtige indstillinger.",
            "Den senest kendte status online eller offline for dine enheder.",
            "Til den valgfrie fjernlukning et SSH-nøglepar. Den private nøgle oprettes i Android Keystore og kan ikke læses eller kopieres derfra. Appen beder aldrig om og gemmer aldrig en adgangskode. Appen gemmer også fingeraftrykket af SSH-værtsnøglen for hver pc, du har valgt at stole på.",
        ]),
        ("p", "Intet af dette sendes til udvikleren eller til nogen server, som udvikleren driver. WakeDeck har ingen konto, ingen server og ingen backend. Appens data er undtaget fra Androids skybackup."),
        ("h2", "Netværkstrafik"),
        ("p", "WakeDeck forbinder kun til de adresser, du konfigurerer:"),
        ("ul", [
            "En Wake-on-LAN-magisk pakke (UDP) til den broadcast-adresse, du har angivet for en enhed.",
            "Kontrol af online eller offline: et TCP-forbindelsesforsøg eller en ping til enhedens vært.",
            "Fjernlukning, kun når du trykker på den og bekræfter: en SSH-forbindelse til den pc, du har sat op.",
        ]),
        ("p", "Trafikken går direkte fra din telefon til dine egne enheder, over dit Wi-Fi eller over en VPN, som du selv driver. Den går ikke via udvikleren."),
        ("h2", "Filer, du vælger at oprette"),
        ("p", "Eksportfunktionen skriver din enhedsliste til en fil, du vælger. Filen indeholder hverken din SSH-nøgle eller betroede værtsnøgler. WakeDeck uploader den ikke noget sted. Hvad du bruger filen til, er op til dig."),
        ("p", "Importfunktionen læser en fil, du vælger. Før der tilføjes noget, viser WakeDeck dig enhederne i filen og advarer dig om dem, der peger uden for dit hjemmenetværk."),
        ("h2", "Tilladelser"),
        ("ul", [
            "<strong>Internet og netværksstatus:</strong> for at sende pakken og tjekke enhedernes status.",
            "<strong>Wake lock:</strong> for at gennemføre en opvågning eller et statustjek, mens skærmen er slukket.",
            "<strong>Notifikationer:</strong> kun så systemet lader appen vise den korte bekræftelse efter en opvågning fra en genvej i launcheren. WakeDeck sender ingen notifikationer, påmindelser eller tilbud.",
        ]),
        ("h2", "Tredjeparter"),
        ("p", "WakeDeck indeholder ingen reklamer, analyse, nedbrudsrapportering eller andre tredjepartstjenester, der indsamler data. Appen bruger open source-biblioteker, som kører på din telefon."),
        ("h2", "Dette websted"),
        ("p", "Dette websted bruger ingen cookies, ingen analyse og ingen scripts, og det henter intet fra andre websteder. Det hostes på GitHub Pages, og domænet administreres via Cloudflare. Som alle webhoteller kan de gemme almindelige serverlogs efter deres egne privatlivspolitikker. Udvikleren modtager eller bruger ikke disse data."),
        ("h2", "Børn"),
        ("p", "WakeDeck henvender sig ikke til børn og indsamler ikke data fra nogen."),
        ("h2", "Ændringer"),
        ("p", "Hvis politikken ændres, offentliggøres den nye version på denne adresse med en ny dato."),
        ("h2", "Kontakt"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Tredjeparter"

SUPPORT = ("Støt WakeDeck", "WakeDeck er gratis og uden reklamer. Hvis det sparer dig tid, og du vil bidrage, hjælper en donation med at dække gebyret til Play Store og domænet.", "Giv en donation")

TIPS = ("Donationer", "Donationsknappen på dette websted åbner Ko-fi, et tredjepartssted med sin egen privatlivspolitik. Appen har ingen donationsfunktion. Ko-fi og deres betalingsudbydere håndterer enhver donation, og udvikleren ser kun det, Ko-fi viser, for eksempel det navn og den besked, du vælger at oplyse.")

GUIDE = ("Slå Wake-on-LAN og fjernlukning til på din Linux-, Windows- eller Mac-pc, så WakeDeck kan vække den fra din telefon.",
         "Erstat PASTE_KEY_LINE_FROM_APP med nøglelinjen, der vises på skærmen SSH-nøgle i WakeDeck.")

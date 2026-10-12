KEY, CODE, NAME = "nb", "nb", "Norsk bokmål"

INDEX = dict(
    title="WakeDeck: Wake-on-LAN-widgeter for Android",
    desc="Slå på PC-ene dine fra startskjermen på Android. Widgeter, snarveier og ruter i Hurtiginnstillinger som du kan tilpasse. Ingen reklame, ingen analyse, ingen kontoer.",
    ogdesc="Slå på enhetene dine fra smarttelefonen. Widgeter for startskjermen som du kan tilpasse. Ingen reklame, ingen analyse, ingen kontoer.",
    skip="Hopp til innhold", language="Språk", features="Funksjoner", how="Slik fungerer det", privacy="Personvern", contact="Kontakt",
    h1=("Slå på enhetene dine fra ", "smarttelefonen", ""),
    lead="WakeDeck slår på PC-ene dine med en Wake-on-LAN magisk pakke, direkte fra widgeter du designer selv. Ett trykk på startskjermen, og appen trenger ikke engang å åpnes.",
    play="Google Play Store: kommer snart", soon="Gratis for Android 8.0 og nyere. Ingen reklame, ingen analyse, ingen kontoer.",
    feat_h="Bygget rundt widgeten", feat_sub="Startskjermen er fjernkontrollen din. Velg hvordan den ser ut og hvor den sitter.",
    cards=[
        ("Widgeter du designer selv", "Én enhet, en liste eller et rutenett. Velg farger, gjennomsiktighet, hjørneradius, kant, ikon, tekststørrelse og layout. Status online og offline oppdateres i bakgrunnen."),
        ("Ett trykk, uten å åpne appen", "Trykk på Slå på i selve widgeten. Du kan også slå på fra snarveier i launcheren og ruter i Hurtiginnstillinger."),
        ("Se hva som er på", "Se hvilke enheter som er online. Oppdater alle med én knapp, eller dra listen nedover."),
        ("Grupper og Slå på alle", "Samle enheter i grupper, for eksempel Kontor eller Lab, og slå på en hel gruppe med én knapp."),
        ("Fjernavslåing via SSH", "Valgfritt. WakeDeck logger på med en nøkkel som ligger i Android Keystore. Appen ber aldri om og lagrer aldri et passord, og den fester vertsnøkkelen for hver PC."),
        ("Privat fra grunnen av", "Ingen reklame, ingen analyse, ingen kontoer. Enhetene og innstillingene dine blir på telefonen, og appen snakker bare med adressene du legger til."),
    ],
    how_h="Tre trinn", how_sub="En guide i appen tar deg gjennom PC-siden, for Windows og Linux.",
    steps=[
        ("Slå på Wake-on-LAN", "Slå det på i BIOS eller UEFI på PC-en og i innstillingene for nettverkskortet. Kablet Ethernet fungerer best."),
        ("Legg til enheten din", "Skriv inn et navn og MAC-adressen. Legg til IP-adressen hvis du vil se status online."),
        ("Legg til en widget", "Trykk og hold på startskjermen, legg til en WakeDeck-widget og tilpass den slik du vil."),
    ],
    priv_h="Dataene dine er dine", priv_p="WakeDeck har ingen server og ingen konto. Appen samler ikke inn eller deler personopplysninger. Les den korte personvernerklæringen for å se nøyaktig hva som lagres på telefonen din.", priv_btn="Personvernerklæring",
    away_h="Borte fra hjemmet?", away_p="Bruk ditt eget VPN, og sett enhetens kringkastingsadresse til adressen for subnettet ditt, for eksempel 192.168.1.255. VPN-et og ruteren din må videresende den. Hjelpen i appen forklarer detaljene og de vanligste feilmeldingene.",
)

PRIVACY = dict(
    title="Personvernerklæring", meta_title="Personvernerklæring | WakeDeck", date_label="Sist oppdatert: 11. oktober 2026",
    desc="WakeDeck samler ikke inn, deler eller selger personopplysninger. Ingen reklame, analyse eller sporing. Erklæringen forklarer hva som blir på telefonen din.",
    ogdesc="Ingen reklame, ingen analyse, ingen kontoer. Dataene dine blir på telefonen.",
    note="Denne oversettelsen er gitt for enkelhets skyld. Hvis den avviker fra den engelske versjonen, gjelder den engelske versjonen.",
    summary="<strong>Kort sagt:</strong> WakeDeck samler ikke inn, deler eller selger personopplysninger og har ingen reklame, analyse eller sporing. Alt du skriver inn, blir på telefonen din.",
    intro="WakeDeck er en Wake-on-LAN-app for Android laget av Diego Bergonsi. Denne erklæringen forklarer hva appen gjør med dataene dine.",
    blocks=[
        ("h2", "Hva som blir på telefonen"),
        ("p", "Alt du skriver inn i WakeDeck, lagres bare på telefonen din:"),
        ("ul", [
            "Enhetene du legger til: navn, MAC-adresse, IP-adresse eller vertsnavn, kringkastingsadresse, porter, operativsystem, gruppe og valgfri SSH-bruker og SSH-port.",
            "Innstillingene dine for widgeter, snarveier og ruter i Hurtiginnstillinger.",
            "Den sist kjente statusen online eller offline for enhetene dine.",
            "For den valgfrie fjernavslåingen et SSH-nøkkelpar. Den private nøkkelen opprettes i Android Keystore og kan ikke leses eller kopieres derfra. Appen ber aldri om og lagrer aldri et passord. Appen lagrer også fingeravtrykket til SSH-vertsnøkkelen for hver PC du har valgt å stole på.",
        ]),
        ("p", "Ingenting av dette sendes til utvikleren eller til noen server utvikleren driver. WakeDeck har ingen konto, ingen server og ingen backend. Appdata er unntatt fra Androids skysikkerhetskopi."),
        ("h2", "Nettverkstrafikk"),
        ("p", "WakeDeck kobler bare til adressene du konfigurerer:"),
        ("ul", [
            "En Wake-on-LAN magisk pakke (UDP) til kringkastingsadressen du har satt for en enhet.",
            "Kontroll av online eller offline: et TCP-tilkoblingsforsøk eller en ping til enhetens vert.",
            "Fjernavslåing, bare når du trykker på den og bekrefter: en SSH-tilkobling til PC-en du har satt opp.",
        ]),
        ("p", "Trafikken går direkte fra telefonen din til dine egne enheter, over Wi-Fi-et ditt eller over et VPN du driver selv. Den går ikke via utvikleren."),
        ("h2", "Filer du velger å opprette"),
        ("p", "Eksportfunksjonen skriver enhetslisten din til en fil du velger. Filen inneholder verken SSH-nøkkelen din eller klarerte vertsnøkler. WakeDeck laster den ikke opp noe sted. Hva du bruker filen til, er opp til deg."),
        ("p", "Importfunksjonen leser en fil du velger. Før noe legges til, viser WakeDeck deg enhetene i filen og advarer deg om de som peker utenfor hjemmenettverket ditt."),
        ("h2", "Tillatelser"),
        ("ul", [
            "<strong>Internett og nettverksstatus:</strong> for å sende pakken og sjekke statusen til enhetene.",
            "<strong>Wake lock:</strong> for å fullføre en oppvåking eller en statussjekk mens skjermen er av.",
            "<strong>Varsler:</strong> bare slik at systemet lar appen vise den korte bekreftelsen etter en oppvåking fra en snarvei i launcheren. WakeDeck sender ingen varsler, påminnelser eller tilbud.",
        ]),
        ("h2", "Tredjeparter"),
        ("p", "WakeDeck inneholder ingen reklame, analyse, krasjrapportering eller andre tredjepartstjenester som samler inn data. Appen bruker åpen kildekode-biblioteker som kjører på telefonen din."),
        ("h2", "Dette nettstedet"),
        ("p", "Dette nettstedet bruker ingen informasjonskapsler, ingen analyse og ingen skript, og det henter ingenting fra andre nettsteder. Det er driftet på GitHub Pages, og domenet administreres via Cloudflare. Som alle webverter kan de lagre vanlige serverlogger etter sine egne personvernerklæringer. Utvikleren mottar eller bruker ikke disse dataene."),
        ("h2", "Barn"),
        ("p", "WakeDeck er ikke rettet mot barn og samler ikke inn data fra noen."),
        ("h2", "Endringer"),
        ("p", "Hvis erklæringen endres, publiseres den nye versjonen på denne adressen med en ny dato."),
        ("h2", "Kontakt"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Tredjeparter"

SUPPORT = ("Støtt WakeDeck", "WakeDeck er gratis og uten reklame. Hvis det sparer deg tid og du vil bidra, hjelper en donasjon med å dekke gebyret til Play Store og domenet.", "Gi en donasjon")

TIPS = ("Donasjoner", "Donasjonsknappen på dette nettstedet åpner Ko-fi, et tredjepartsnettsted med sin egen personvernerklæring. Appen har ingen donasjonsfunksjon. Ko-fi og betalingsleverandørene deres håndterer enhver donasjon, og utvikleren ser bare det Ko-fi viser, for eksempel navnet og meldingen du velger å oppgi.")

GUIDE = ("Slå på Wake-on-LAN og fjernavslåing på Linux-, Windows- eller Mac-PC-en din, slik at WakeDeck kan slå den på fra telefonen.",
         "Bytt ut PASTE_KEY_LINE_FROM_APP med nøkkellinjen som vises på skjermen SSH-nøkkel i WakeDeck.")

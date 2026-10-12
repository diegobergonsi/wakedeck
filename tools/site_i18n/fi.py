KEY, CODE, NAME = "fi", "fi", "Suomi"

INDEX = dict(
    title="WakeDeck: Wake-on-LAN-widgetit Androidille",
    desc="Herätä tietokoneesi Androidin aloitusnäytöstä. Muokattavat widgetit, pikakuvakkeet ja Pika-asetusten ruudut. Ei mainoksia, ei analytiikkaa, ei tilejä.",
    ogdesc="Herätä laitteesi älypuhelimella. Muokattavat widgetit aloitusnäyttöön. Ei mainoksia, ei analytiikkaa, ei tilejä.",
    skip="Siirry sisältöön", language="Kieli", features="Ominaisuudet", how="Näin se toimii", privacy="Tietosuoja", contact="Yhteystiedot",
    h1=("Herätä laitteesi ", "älypuhelimella", ""),
    lead="WakeDeck käynnistää tietokoneesi Wake-on-LAN-taikapaketilla suoraan itse suunnittelemistasi widgeteistä. Yksi napautus aloitusnäytössä, eikä sovellusta tarvitse edes avata.",
    play="Google Play Store: tulossa pian", soon="Ilmainen Android 8.0:lle ja uudemmille. Ei mainoksia, ei analytiikkaa, ei tilejä.",
    feat_h="Rakennettu widgetin ympärille", feat_sub="Aloitusnäyttösi on kaukosäädin. Valitse, miltä se näyttää ja missä se on.",
    cards=[
        ("Itse suunnittelemasi widgetit", "Yksi laite, luettelo tai ruudukko. Valitse värit, läpinäkyvyys, kulmien pyöristys, reunus, kuvake, tekstin koko ja asettelu. Verkossa- ja ei verkossa -tila päivittyy taustalla."),
        ("Yksi napautus, ei sovellusta", "Napauta Herätä itse widgetissä. Voit herättää myös käynnistimen pikakuvakkeista ja Pika-asetusten ruuduista."),
        ("Tiedät, mikä on päällä", "Näet, mitkä laitteet ovat verkossa. Päivitä kaikki yhdellä painikkeella tai vetämällä luetteloa alaspäin."),
        ("Ryhmät ja Herätä kaikki", "Kokoa laitteet ryhmiin, kuten Toimisto tai Laboratorio, ja herätä koko ryhmä yhdellä painikkeella."),
        ("Etäsammutus SSH:lla", "Valinnainen. WakeDeck kirjautuu Android Keystoreen tallennetulla avaimella. Se ei koskaan pyydä tai tallenna salasanaa, ja se kiinnittää jokaisen tietokoneen isäntäavaimen."),
        ("Yksityinen alusta asti", "Ei mainoksia, ei analytiikkaa, ei tilejä. Laitteesi ja asetuksesi pysyvät puhelimessa, ja sovellus keskustelee vain lisäämiesi osoitteiden kanssa."),
    ],
    how_h="Kolme vaihetta", how_sub="Sovelluksen sisäinen opas opastaa tietokoneen puolella, Windowsille, Linuxille ja macOS:lle.",
    steps=[
        ("Ota Wake-on-LAN käyttöön", "Ota se käyttöön tietokoneen BIOSissa tai UEFIssa ja verkkokortin asetuksissa. Langallinen Ethernet toimii parhaiten."),
        ("Lisää laitteesi", "Anna nimi ja MAC-osoite. Lisää IP-osoite, jos haluat nähdä verkkotilan."),
        ("Lisää widget", "Paina aloitusnäyttöä pitkään, lisää WakeDeck-widget ja muokkaa sitä haluamallasi tavalla."),
    ],
    priv_h="Tietosi pysyvät sinun", priv_p="WakeDeckillä ei ole palvelinta eikä tiliä. Se ei kerää eikä jaa henkilötietoja. Lue lyhyt tietosuojakäytäntö nähdäksesi tarkalleen, mitä puhelimeesi tallennetaan.", priv_btn="Tietosuojakäytäntö",
    away_h="Poissa kotoa?", away_p="Käytä omaa VPN:ääsi ja aseta laitteen yleislähetysosoitteeksi aliverkkosi osoite, esimerkiksi 192.168.1.255. VPN:n ja reitittimen on välitettävä se. Sovelluksen ohje selittää yksityiskohdat ja yleisimmät virheilmoitukset.",
)

PRIVACY = dict(
    title="Tietosuojakäytäntö", meta_title="Tietosuojakäytäntö | WakeDeck", date_label="Päivitetty viimeksi: 11. lokakuuta 2026",
    desc="WakeDeck ei kerää, jaa tai myy henkilötietoja. Ei mainoksia, analytiikkaa eikä seurantaa. Tämä käytäntö kertoo, mitä puhelimeesi jää.",
    ogdesc="Ei mainoksia, ei analytiikkaa, ei tilejä. Tietosi pysyvät puhelimessasi.",
    note="Tämä käännös on tarjolla mukavuuden vuoksi. Jos se eroaa englanninkielisestä versiosta, englanninkielinen versio on ensisijainen.",
    summary="<strong>Lyhyesti:</strong> WakeDeck ei kerää, jaa tai myy henkilötietoja, eikä siinä ole mainoksia, analytiikkaa tai seurantaa. Kaikki antamasi tiedot pysyvät puhelimessasi.",
    intro="WakeDeck on Diego Bergonsin tekemä Android-sovellus Wake-on-LAN-herätykseen. Tämä käytäntö kertoo, mitä sovellus tekee tiedoillasi.",
    blocks=[
        ("h2", "Mitä puhelimeesi jää"),
        ("p", "Kaikki WakeDeckiin antamasi tiedot tallennetaan vain puhelimeesi:"),
        ("ul", [
            "Lisäämäsi laitteet: nimi, MAC-osoite, IP-osoite tai isäntänimi, yleislähetysosoite, portit, käyttöjärjestelmä, ryhmä sekä valinnainen SSH-käyttäjä ja SSH-portti.",
            "Widgettien, pikakuvakkeiden ja Pika-asetusten ruutujen asetukset.",
            "Laitteidesi viimeksi tiedossa ollut tila, verkossa tai ei verkossa.",
            "Valinnaista etäsammutusta varten SSH-avainpari. Yksityinen avain luodaan Android Keystoren sisällä, eikä sitä voi lukea tai kopioida sieltä pois. Sovellus ei koskaan pyydä tai tallenna salasanaa. Se tallentaa myös niiden tietokoneiden SSH-isäntäavaimen sormenjäljen, joihin olet päättänyt luottaa.",
        ]),
        ("p", "Mitään tästä ei lähetetä kehittäjälle eikä millekään kehittäjän ylläpitämälle palvelimelle. WakeDeckillä ei ole tiliä, palvelinta eikä taustajärjestelmää. Sovelluksen tiedot on jätetty Androidin pilvivarmuuskopioinnin ulkopuolelle."),
        ("h2", "Verkkoliikenne"),
        ("p", "WakeDeck yhdistää vain määrittämiisi osoitteisiin:"),
        ("ul", [
            "Wake-on-LAN-taikapaketti (UDP) laitteelle asettamaasi yleislähetysosoitteeseen.",
            "Verkossa- tai ei verkossa -tarkistukset: TCP-yhteysyritys tai ping laitteen isäntään.",
            "Etäsammutus, vain kun napautat sitä ja vahvistat: SSH-yhteys määrittämääsi tietokoneeseen.",
        ]),
        ("p", "Tämä liikenne kulkee suoraan puhelimestasi omiin laitteisiisi Wi-Fi-verkkosi tai itse ylläpitämäsi VPN:n kautta. Se ei kulje kehittäjän kautta."),
        ("h2", "Tiedostot, jotka päätät luoda"),
        ("p", "Vientitoiminto kirjoittaa laiteluettelosi valitsemaasi tiedostoon. Tiedosto ei sisällä SSH-avaintasi eikä luotettuja isäntäavaimia. WakeDeck ei lähetä sitä minnekään. Siitä, mitä tiedostolle teet, päätät itse."),
        ("p", "Tuontitoiminto lukee valitsemasi tiedoston. Ennen kuin mitään lisätään, WakeDeck näyttää tiedoston laitteet ja varoittaa niistä, jotka osoittavat kotiverkkosi ulkopuolelle."),
        ("h2", "Käyttöoikeudet"),
        ("ul", [
            "<strong>Internet ja verkon tila:</strong> paketin lähettämiseen ja laitteiden tilan tarkistamiseen.",
            "<strong>Wake lock:</strong> herätyksen tai tilatarkistuksen loppuunsaattamiseen näytön ollessa pois päältä.",
            "<strong>Ilmoitukset:</strong> vain jotta järjestelmä sallii sovelluksen näyttää lyhyen vahvistusviestin käynnistimen pikakuvakkeesta tehdyn herätyksen jälkeen. WakeDeck ei lähetä ilmoituksia, muistutuksia eikä mainoksia.",
        ]),
        ("h2", "Kolmannet osapuolet"),
        ("p", "WakeDeck ei sisällä mainoksia, analytiikkaa, kaatumisraportointia eikä muita kolmannen osapuolen palveluita, jotka keräävät tietoja. Se käyttää avoimen lähdekoodin kirjastoja, jotka toimivat puhelimessasi."),
        ("h2", "Tämä verkkosivusto"),
        ("p", "Tämä verkkosivusto ei käytä evästeitä, analytiikkaa eikä komentosarjoja, eikä se lataa mitään muilta sivustoilta. Se on GitHub Pagesissa, ja sen verkkotunnusta hallinnoidaan Cloudflaren kautta. Kuten kaikki verkkopalvelut, ne voivat säilyttää tavallisia palvelinlokeja omien tietosuojakäytäntöjensä mukaisesti. Kehittäjä ei saa eikä käytä näitä tietoja."),
        ("h2", "Lapset"),
        ("p", "WakeDeck ei ole suunnattu lapsille, eikä se kerää tietoja keneltäkään."),
        ("h2", "Muutokset"),
        ("p", "Jos tämä käytäntö muuttuu, uusi versio julkaistaan tässä osoitteessa uudella päiväyksellä."),
        ("h2", "Yhteystiedot"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Kolmannet osapuolet"

SUPPORT = ("Tue WakeDeckiä", "WakeDeck on ilmainen ja mainokseton. Jos se säästää aikaasi ja haluat osallistua, lahjoitus auttaa kattamaan Play Storen maksun ja verkkotunnuksen.", "Lahjoita")

TIPS = ("Lahjoitukset", "Tämän verkkosivuston lahjoituspainike avaa Ko-fin, kolmannen osapuolen sivuston, jolla on oma tietosuojakäytäntönsä. Sovelluksessa ei ole lahjoitustoimintoa. Ko-fi ja sen maksupalveluntarjoajat käsittelevät kaikki lahjoitukset, ja kehittäjä näkee vain sen, mitä Ko-fi näyttää, kuten nimen ja viestin, jonka päätät antaa.")

GUIDE = ("Ota Wake-on-LAN ja etäsammutus käyttöön Linux-, Windows- tai Mac-tietokoneellasi, jotta WakeDeck voi herättää sen puhelimesta.",
         "Korvaa PASTE_KEY_LINE_FROM_APP WakeDeckin SSH-avain-näkymässä näkyvällä avainrivillä.")

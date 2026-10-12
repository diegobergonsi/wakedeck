KEY, CODE, NAME = "sv", "sv", "Svenska"

INDEX = dict(
    title="WakeDeck: Wake-on-LAN-widgetar för Android",
    desc="Väck dina datorer från Androids startskärm. Anpassningsbara widgetar, genvägar och rutor i Snabbinställningar. Inga annonser, ingen analys, inga konton.",
    ogdesc="Väck dina enheter från mobilen. Anpassningsbara widgetar för startskärmen. Inga annonser, ingen analys, inga konton.",
    skip="Hoppa till innehåll", language="Språk", features="Funktioner", how="Så fungerar det", privacy="Integritet", contact="Kontakt",
    h1=("Väck dina enheter från ", "mobilen", ""),
    lead="WakeDeck startar dina datorer med ett Wake-on-LAN-magic packet, direkt från widgetar som du själv utformar. Ett tryck på startskärmen, och appen behöver inte ens öppnas.",
    play="Google Play Store: kommer snart", soon="Gratis för Android 8.0 och senare. Inga annonser, ingen analys, inga konton.",
    feat_h="Byggt kring widgeten", feat_sub="Din startskärm är fjärrkontrollen. Välj hur den ser ut och var den sitter.",
    cards=[
        ("Widgetar du utformar själv", "En enda enhet, en lista eller ett rutnät. Välj färger, genomskinlighet, hörnradie, kantlinje, ikon, textstorlek och layout. Status online och offline uppdateras i bakgrunden."),
        ("Ett tryck, utan att öppna appen", "Tryck på Väck i själva widgeten. Du kan också väcka från genvägar i startprogrammet och rutor i Snabbinställningar."),
        ("Se vad som är på", "Se vilka enheter som är online. Uppdatera alla med en knapp eller genom att dra listan nedåt."),
        ("Grupper och Väck alla", "Samla enheter i grupper, till exempel Kontor eller Labb, och väck en hel grupp med en knapp."),
        ("Fjärravstängning via SSH", "Valfritt. WakeDeck loggar in med en nyckel som ligger i Android Keystore. Appen frågar aldrig efter och sparar aldrig ett lösenord, och fäster värdnyckeln för varje dator."),
        ("Privat från grunden", "Inga annonser, ingen analys, inga konton. Dina enheter och inställningar stannar i telefonen, och appen pratar bara med de adresser du lägger till."),
    ],
    how_h="Tre steg", how_sub="En guide i appen går igenom datorsidan, för Windows, Linux och macOS.",
    steps=[
        ("Aktivera Wake-on-LAN", "Aktivera det i datorns BIOS eller UEFI och i nätverkskortets inställningar. Trådbundet Ethernet fungerar bäst."),
        ("Lägg till din enhet", "Ange ett namn och MAC-adressen. Lägg till IP-adressen om du vill se status online."),
        ("Lägg till en widget", "Tryck länge på startskärmen, lägg till en WakeDeck-widget och utforma den som du vill."),
    ],
    priv_h="Dina data är dina", priv_p="WakeDeck har ingen server och inget konto. Appen samlar inte in eller delar personuppgifter. Läs den korta integritetspolicyn för att se exakt vad som sparas i telefonen.", priv_btn="Integritetspolicy",
    away_h="Borta från hemmet?", away_p="Använd ditt eget VPN och ställ in enhetens broadcast-adress på ditt subnäts, till exempel 192.168.1.255. Ditt VPN och din router måste vidarebefordra den. Hjälpen i appen förklarar detaljerna och de vanligaste felmeddelandena.",
)

PRIVACY = dict(
    title="Integritetspolicy", meta_title="Integritetspolicy | WakeDeck", date_label="Senast uppdaterad: 11 oktober 2026",
    desc="WakeDeck samlar inte in, delar eller säljer personuppgifter. Inga annonser, ingen analys, ingen spårning. Policyn förklarar vad som stannar i telefonen.",
    ogdesc="Inga annonser, ingen analys, inga konton. Dina data stannar i telefonen.",
    note="Den här översättningen tillhandahålls för bekvämlighets skull. Om den skiljer sig från den engelska versionen gäller den engelska versionen.",
    summary="<strong>Kort sagt:</strong> WakeDeck samlar inte in, delar eller säljer några personuppgifter och har inga annonser, ingen analys och ingen spårning. Allt du anger stannar i telefonen.",
    intro="WakeDeck är en Wake-on-LAN-app för Android gjord av Diego Bergonsi. Den här policyn förklarar vad appen gör med dina data.",
    blocks=[
        ("h2", "Vad som stannar i telefonen"),
        ("p", "Allt du anger i WakeDeck sparas bara i din telefon:"),
        ("ul", [
            "Enheterna du lägger till: namn, MAC-adress, IP-adress eller värdnamn, broadcast-adress, portar, operativsystem, grupp samt valfri SSH-användare och SSH-port.",
            "Dina inställningar för widgetar, genvägar och rutor i Snabbinställningar.",
            "Den senast kända statusen online eller offline för dina enheter.",
            "För den valfria fjärravstängningen ett SSH-nyckelpar. Den privata nyckeln skapas i Android Keystore och kan inte läsas eller kopieras därifrån. Appen frågar aldrig efter och sparar aldrig ett lösenord. Appen sparar också fingeravtrycket för SSH-värdnyckeln för varje dator som du valt att lita på.",
        ]),
        ("p", "Inget av detta skickas till utvecklaren eller till någon server som utvecklaren driver. WakeDeck har inget konto, ingen server och ingen backend. Appdata är undantagen från Androids molnbackup."),
        ("h2", "Nätverkstrafik"),
        ("p", "WakeDeck ansluter bara till de adresser du konfigurerar:"),
        ("ul", [
            "Ett Wake-on-LAN-magic packet (UDP) till den broadcast-adress du angett för en enhet.",
            "Kontroller av online eller offline: ett TCP-anslutningsförsök eller en ping till enhetens värd.",
            "Fjärravstängning, bara när du trycker på den och bekräftar: en SSH-anslutning till datorn du ställt in.",
        ]),
        ("p", "Trafiken går direkt från din telefon till dina egna enheter, över ditt Wi-Fi eller över ett VPN som du själv driver. Den går inte via utvecklaren."),
        ("h2", "Filer du väljer att skapa"),
        ("p", "Exportfunktionen skriver din enhetslista till en fil du väljer. Filen innehåller varken din SSH-nyckel eller betrodda värdnycklar. WakeDeck laddar inte upp den någonstans. Vad du gör med filen avgör du själv."),
        ("p", "Importfunktionen läser en fil du väljer. Innan något läggs till visar WakeDeck enheterna i filen och varnar för sådana som pekar utanför ditt hemnätverk."),
        ("h2", "Behörigheter"),
        ("ul", [
            "<strong>Internet och nätverksstatus:</strong> för att skicka paketet och kontrollera enheternas status.",
            "<strong>Wake lock:</strong> för att slutföra en väckning eller en statuskontroll medan skärmen är släckt.",
            "<strong>Aviseringar:</strong> bara för att systemet ska låta appen visa det korta bekräftelsemeddelandet efter en väckning från en genväg i startprogrammet. WakeDeck skickar inga aviseringar, påminnelser eller erbjudanden.",
        ]),
        ("h2", "Tredje part"),
        ("p", "WakeDeck innehåller inga annonser, ingen analys, ingen kraschrapportering och inga andra tredjepartstjänster som samlar in data. Appen använder öppen källkod i bibliotek som körs i din telefon."),
        ("h2", "Den här webbplatsen"),
        ("p", "Webbplatsen använder inga cookies, ingen analys och inga skript, och den läser inte in något från andra webbplatser. Den ligger på GitHub Pages och domänen hanteras via Cloudflare. Som alla webbhotell kan de spara vanliga serverloggar enligt sina egna integritetspolicyer. Utvecklaren får inte och använder inte de uppgifterna."),
        ("h2", "Barn"),
        ("p", "WakeDeck riktar sig inte till barn och samlar inte in data från någon."),
        ("h2", "Ändringar"),
        ("p", "Om policyn ändras publiceras den nya versionen på den här adressen med ett nytt datum."),
        ("h2", "Kontakt"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Tredje part"

SUPPORT = ("Stöd WakeDeck", "WakeDeck är gratis och utan annonser. Om det sparar tid för dig och du vill bidra hjälper en donation till att täcka avgiften för Play Store och domänen.", "Donera")

TIPS = ("Donationer", "Donationsknappen på den här webbplatsen öppnar Ko-fi, en tredjepartssajt med egen integritetspolicy. Appen har ingen donationsfunktion. Ko-fi och deras betalningsleverantörer hanterar varje donation, och utvecklaren ser bara det Ko-fi visar, till exempel det namn och meddelande du väljer att ange.")

GUIDE = ("Aktivera Wake-on-LAN och fjärravstängning på din Linux-, Windows- eller Mac-dator så att WakeDeck kan väcka den från mobilen.",
         "Ersätt PASTE_KEY_LINE_FROM_APP med nyckelraden som visas på skärmen SSH-nyckel i WakeDeck.")

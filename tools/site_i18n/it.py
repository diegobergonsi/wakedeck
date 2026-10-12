KEY, CODE, NAME = "it", "it", "Italiano"

INDEX = dict(
    title="WakeDeck: widget Wake-on-LAN per Android",
    desc="Accendi i tuoi PC dalla schermata Home di Android. Widget personalizzabili, scorciatoie e riquadri delle Impostazioni rapide. Nessuna pubblicità, nessuna analisi, nessun account.",
    ogdesc="Accendi i tuoi dispositivi dallo smartphone. Widget personalizzabili per la schermata Home. Nessuna pubblicità, nessuna analisi, nessun account.",
    skip="Vai al contenuto", language="Lingua", features="Funzioni", how="Come funziona", privacy="Privacy", contact="Contatti",
    h1=("Accendi i tuoi dispositivi dallo ", "smartphone", ""),
    lead="WakeDeck accende i tuoi PC con un magic packet Wake-on-LAN, direttamente da widget che progetti tu. Un tocco sulla schermata Home e l’app non si apre nemmeno.",
    play="Google Play Store: in arrivo", soon="Gratis per Android 8.0 e versioni successive. Nessuna pubblicità, nessuna analisi, nessun account.",
    feat_h="Pensato attorno al widget", feat_sub="La schermata Home è il tuo telecomando. Scegli come appare e dove sta.",
    cards=[
        ("Widget progettati da te", "Un solo dispositivo, un elenco o una griglia. Scegli colori, trasparenza, raggio degli angoli, bordo, icona, dimensione del testo e layout. Lo stato online e offline si aggiorna in background."),
        ("Un tocco, senza aprire l’app", "Tocca Accendi nel widget stesso. Puoi accendere anche dalle scorciatoie del launcher e dai riquadri delle Impostazioni rapide."),
        ("Sai cosa è acceso", "Vedi quali dispositivi sono online. Aggiornali tutti con un pulsante o trascinando l’elenco verso il basso."),
        ("Gruppi e Accendi tutti", "Raggruppa i dispositivi, per esempio Ufficio o Laboratorio, e accendi un intero gruppo con un solo pulsante."),
        ("Spegnimento remoto via SSH", "Facoltativo. WakeDeck accede con una chiave conservata nell’Android Keystore. Non chiede né salva mai una password e fissa la chiave host di ogni PC."),
        ("Privato per progetto", "Nessuna pubblicità, nessuna analisi, nessun account. I tuoi dispositivi e le impostazioni restano sul telefono, e l’app comunica solo con gli indirizzi che aggiungi."),
    ],
    how_h="Tre passaggi", how_sub="Una guida nell’app ti accompagna lato PC, per Windows, Linux e macOS.",
    steps=[
        ("Attiva il Wake-on-LAN", "Abilitalo nel BIOS o UEFI del PC e nelle impostazioni della scheda di rete. L’Ethernet via cavo funziona meglio."),
        ("Aggiungi il dispositivo", "Inserisci un nome e l’indirizzo MAC. Aggiungi l’indirizzo IP se vuoi vedere lo stato online."),
        ("Aggiungi un widget", "Tieni premuta la schermata Home, aggiungi un widget WakeDeck e personalizzalo."),
    ],
    priv_h="I tuoi dati restano tuoi", priv_p="WakeDeck non ha server né account. Non raccoglie né condivide dati personali. Leggi la breve informativa sulla privacy per vedere esattamente cosa viene salvato sul telefono.", priv_btn="Informativa sulla privacy",
    away_h="Lontano da casa?", away_p="Usa la tua VPN e imposta l’indirizzo di broadcast del dispositivo su quello della tua sottorete, per esempio 192.168.1.255. La VPN e il router devono inoltrarlo. La guida nell’app spiega i dettagli e i messaggi di errore più comuni.",
)

PRIVACY = dict(
    title="Informativa sulla privacy", meta_title="Informativa sulla privacy | WakeDeck", date_label="Ultimo aggiornamento: 11 ottobre 2026",
    desc="WakeDeck non raccoglie, condivide né vende dati personali. Nessuna pubblicità, analisi o tracciamento. Questa informativa spiega cosa resta sul tuo telefono.",
    ogdesc="Nessuna pubblicità, nessuna analisi, nessun account. I tuoi dati restano sul telefono.",
    note="Questa traduzione è fornita per comodità. In caso di differenze rispetto alla versione inglese, prevale la versione inglese.",
    summary="<strong>In breve:</strong> WakeDeck non raccoglie, condivide né vende dati personali e non ha pubblicità, analisi o tracciamento. Tutto ciò che inserisci resta sul telefono.",
    intro="WakeDeck è un’app Wake-on-LAN per Android creata da Diego Bergonsi. Questa informativa spiega cosa fa l’app con i tuoi dati.",
    blocks=[
        ("h2", "Cosa resta sul telefono"),
        ("p", "Tutto ciò che inserisci in WakeDeck è salvato solo sul telefono:"),
        ("ul", [
            "I dispositivi che aggiungi: nome, indirizzo MAC, indirizzo IP o nome host, indirizzo di broadcast, porte, sistema operativo, gruppo e, facoltativamente, utente e porta SSH.",
            "Le impostazioni di widget, scorciatoie e riquadri delle Impostazioni rapide.",
            "L’ultimo stato online o offline noto dei dispositivi.",
            "Per lo spegnimento remoto facoltativo, una coppia di chiavi SSH. La chiave privata viene creata nell’Android Keystore e non può essere letta né copiata da lì. L’app non chiede né salva mai una password. Salva anche l’impronta della chiave host SSH di ogni PC di cui hai scelto di fidarti.",
        ]),
        ("p", "Niente di tutto questo viene inviato allo sviluppatore né a server gestiti dallo sviluppatore. WakeDeck non ha account, server né backend. I dati dell’app sono esclusi dal backup cloud di Android."),
        ("h2", "Traffico di rete"),
        ("p", "WakeDeck si connette solo agli indirizzi che configuri:"),
        ("ul", [
            "Un magic packet Wake-on-LAN (UDP) all’indirizzo di broadcast impostato per un dispositivo.",
            "Controlli online o offline: un tentativo di connessione TCP o un ping all’host del dispositivo.",
            "Spegnimento remoto, solo quando lo tocchi e lo confermi: una connessione SSH al PC che hai configurato.",
        ]),
        ("p", "Questo traffico va direttamente dal telefono ai tuoi dispositivi, tramite il tuo Wi-Fi o una VPN che gestisci tu. Non passa dallo sviluppatore."),
        ("h2", "File che scegli di creare"),
        ("p", "La funzione di esportazione scrive l’elenco dei dispositivi in un file che scegli. Il file non contiene la tua chiave SSH né le chiavi host attendibili. WakeDeck non lo carica da nessuna parte. Cosa farne dipende da te."),
        ("p", "La funzione di importazione legge un file che scegli. Prima di aggiungere qualsiasi cosa, WakeDeck mostra i dispositivi del file e ti avvisa di quelli che puntano fuori dalla tua rete domestica."),
        ("h2", "Autorizzazioni"),
        ("ul", [
            "<strong>Internet e stato della rete:</strong> per inviare il pacchetto e controllare lo stato dei dispositivi.",
            "<strong>Wake lock:</strong> per completare un’accensione o un controllo di stato a schermo spento.",
            "<strong>Notifiche:</strong> solo perché il sistema permetta all’app di mostrare il breve messaggio di conferma dopo un’accensione da una scorciatoia del launcher. WakeDeck non invia notifiche, promemoria o promozioni.",
        ]),
        ("h2", "Terze parti"),
        ("p", "WakeDeck non contiene pubblicità, analisi, segnalazione degli arresti anomali né altri servizi di terze parti che raccolgono dati. Usa librerie open source che girano sul telefono."),
        ("h2", "Questo sito web"),
        ("p", "Questo sito non usa cookie, analisi né script e non carica nulla da altri siti. È ospitato su GitHub Pages e il dominio è gestito tramite Cloudflare. Come qualsiasi host web, possono conservare i normali log del server secondo le proprie informative sulla privacy. Lo sviluppatore non riceve né usa questi dati."),
        ("h2", "Minori"),
        ("p", "WakeDeck non è rivolto ai minori e non raccoglie dati da nessuno."),
        ("h2", "Modifiche"),
        ("p", "Se questa informativa cambia, la nuova versione sarà pubblicata a questo indirizzo con una nuova data."),
        ("h2", "Contatti"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Terze parti"

SUPPORT = ("Sostieni WakeDeck", "WakeDeck è gratuito e senza pubblicità. Se ti fa risparmiare tempo e vuoi contribuire, una donazione aiuta a coprire la quota del Play Store e il dominio.", "Fai una donazione")

TIPS = ("Donazioni", "Il pulsante di donazione di questo sito apre Ko-fi, un sito di terze parti con una propria informativa sulla privacy. L’app non ha funzioni di donazione. Ko-fi e i suoi fornitori di pagamento gestiscono ogni donazione, e lo sviluppatore vede solo ciò che Ko-fi mostra, come il nome e il messaggio che scegli di indicare.")

GUIDE = ("Attiva Wake-on-LAN e lo spegnimento remoto sul tuo PC Linux, Windows o Mac, così WakeDeck può accenderlo dal telefono.",
         "Sostituisci PASTE_KEY_LINE_FROM_APP con la riga della chiave mostrata nella schermata Chiave SSH di WakeDeck.")

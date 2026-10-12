KEY, CODE, NAME = "pl", "pl", "Polski"

INDEX = dict(
    title="WakeDeck: widżety Wake-on-LAN na Androida",
    desc="Włączaj swoje komputery z ekranu głównego Androida. Konfigurowalne widżety, skróty i kafelki Szybkich ustawień. Bez reklam, bez analityki, bez kont.",
    ogdesc="Włączaj swoje urządzenia ze smartfona. Konfigurowalne widżety na ekran główny. Bez reklam, bez analityki, bez kont.",
    skip="Przejdź do treści", language="Język", features="Funkcje", how="Jak to działa", privacy="Prywatność", contact="Kontakt",
    h1=("Włączaj swoje urządzenia ze ", "smartfona", ""),
    lead="WakeDeck włącza Twoje komputery pakietem Wake-on-LAN (magic packet), prosto z widżetów, które sam projektujesz. Jedno dotknięcie na ekranie głównym i aplikacja nawet się nie otwiera.",
    play="Google Play Store: wkrótce", soon="Za darmo na Androida 8.0 i nowszego. Bez reklam, bez analityki, bez kont.",
    feat_h="Zbudowany wokół widżetu", feat_sub="Ekran główny to Twój pilot. Wybierz, jak wygląda i gdzie stoi.",
    cards=[
        ("Widżety projektowane przez Ciebie", "Jedno urządzenie, lista albo siatka. Wybierz kolory, przezroczystość, promień rogów, obramowanie, ikonę, rozmiar tekstu i układ. Stan online i offline odświeża się w tle."),
        ("Jedno dotknięcie, bez aplikacji", "Dotknij Włącz w samym widżecie. Możesz też włączać ze skrótów launchera i kafelków Szybkich ustawień."),
        ("Wiesz, co jest włączone", "Zobacz, które urządzenia są online. Odśwież wszystkie jednym przyciskiem albo pociągnięciem listy w dół."),
        ("Grupy i Włącz wszystkie", "Połącz urządzenia w grupy, na przykład Biuro lub Laboratorium, i włącz całą grupę jednym przyciskiem."),
        ("Zdalne wyłączanie przez SSH", "Opcjonalne. WakeDeck loguje się kluczem przechowywanym w Android Keystore. Nigdy nie prosi o hasło ani go nie zapisuje i przypina klucz hosta każdego komputera."),
        ("Prywatność od podstaw", "Bez reklam, bez analityki, bez kont. Twoje urządzenia i ustawienia zostają w telefonie, a aplikacja łączy się tylko z adresami, które dodasz."),
    ],
    how_h="Trzy kroki", how_sub="Przewodnik w aplikacji prowadzi przez konfigurację komputera, dla Windows i Linuksa.",
    steps=[
        ("Włącz Wake-on-LAN", "Włącz go w BIOS lub UEFI komputera oraz w ustawieniach karty sieciowej. Najlepiej działa przewodowy Ethernet."),
        ("Dodaj urządzenie", "Wpisz nazwę i adres MAC. Dodaj adres IP, jeśli chcesz widzieć stan online."),
        ("Dodaj widżet", "Przytrzymaj ekran główny, dodaj widżet WakeDeck i ustaw go po swojemu."),
    ],
    priv_h="Twoje dane zostają Twoje", priv_p="WakeDeck nie ma serwera ani kont. Nie zbiera i nie udostępnia danych osobowych. Przeczytaj krótką politykę prywatności, aby zobaczyć dokładnie, co jest zapisane w telefonie.", priv_btn="Polityka prywatności",
    away_h="Poza domem?", away_p="Użyj własnego VPN i ustaw adres rozgłoszeniowy urządzenia na adres swojej podsieci, na przykład 192.168.1.255. Twój VPN i router muszą go przekazywać. Pomoc w aplikacji wyjaśnia szczegóły i najczęstsze komunikaty o błędach.",
)

PRIVACY = dict(
    title="Polityka prywatności", meta_title="Polityka prywatności | WakeDeck", date_label="Ostatnia aktualizacja: 11 października 2026",
    desc="WakeDeck nie zbiera, nie udostępnia i nie sprzedaje danych osobowych. Nie ma reklam, analityki ani śledzenia. Ta polityka wyjaśnia, co zostaje w Twoim telefonie.",
    ogdesc="Bez reklam, bez analityki, bez kont. Twoje dane zostają w telefonie.",
    note="To tłumaczenie jest udostępnione dla wygody. W razie różnic w stosunku do wersji angielskiej obowiązuje wersja angielska.",
    summary="<strong>W skrócie:</strong> WakeDeck nie zbiera, nie udostępnia i nie sprzedaje żadnych danych osobowych oraz nie ma reklam, analityki ani śledzenia. Wszystko, co wpiszesz, zostaje w telefonie.",
    intro="WakeDeck to aplikacja Wake-on-LAN na Androida stworzona przez Diego Bergonsi. Ta polityka wyjaśnia, co aplikacja robi z Twoimi danymi.",
    blocks=[
        ("h2", "Co zostaje w telefonie"),
        ("p", "Wszystko, co wpiszesz w WakeDeck, jest przechowywane wyłącznie w Twoim telefonie:"),
        ("ul", [
            "Dodane przez Ciebie urządzenia: nazwa, adres MAC, adres IP lub nazwa hosta, adres rozgłoszeniowy, porty, system operacyjny, grupa oraz opcjonalnie użytkownik i port SSH.",
            "Ustawienia widżetów, skrótów i kafelków Szybkich ustawień.",
            "Ostatni znany stan online lub offline Twoich urządzeń.",
            "Na potrzeby opcjonalnego zdalnego wyłączania para kluczy SSH. Klucz prywatny jest tworzony wewnątrz Android Keystore i nie można go stamtąd odczytać ani skopiować. Aplikacja nigdy nie prosi o hasło ani go nie zapisuje. Zapisuje też odcisk klucza hosta SSH każdego komputera, któremu postanowisz zaufać.",
        ]),
        ("p", "Nic z tego nie jest wysyłane do dewelopera ani na żaden serwer prowadzony przez dewelopera. WakeDeck nie ma konta, serwera ani zaplecza. Dane aplikacji są wyłączone z chmurowej kopii zapasowej Androida."),
        ("h2", "Ruch sieciowy"),
        ("p", "WakeDeck łączy się wyłącznie z adresami, które skonfigurujesz:"),
        ("ul", [
            "Pakiet Wake-on-LAN (magic packet, UDP) na adres rozgłoszeniowy ustawiony dla urządzenia.",
            "Sprawdzanie stanu online lub offline: próba połączenia TCP lub ping do hosta urządzenia.",
            "Zdalne wyłączanie, tylko gdy je dotkniesz i potwierdzisz: połączenie SSH z komputerem, który został skonfigurowany.",
        ]),
        ("p", "Ten ruch idzie bezpośrednio z Twojego telefonu do Twoich urządzeń, przez Twoje Wi-Fi lub VPN, który sam prowadzisz. Nie przechodzi przez dewelopera."),
        ("h2", "Pliki, które sam tworzysz"),
        ("p", "Funkcja eksportu zapisuje listę urządzeń w wybranym przez Ciebie pliku. Plik nie zawiera Twojego klucza SSH ani zaufanych kluczy hostów. WakeDeck nigdzie go nie wysyła. To, co zrobisz z plikiem, zależy od Ciebie."),
        ("p", "Funkcja importu odczytuje wybrany przez Ciebie plik. Zanim cokolwiek doda, WakeDeck pokazuje urządzenia z pliku i ostrzega przed tymi, które wskazują poza Twoją sieć domową."),
        ("h2", "Uprawnienia"),
        ("ul", [
            "<strong>Internet i stan sieci:</strong> aby wysłać pakiet i sprawdzić stan urządzeń.",
            "<strong>Wake lock:</strong> aby dokończyć włączanie lub sprawdzanie stanu przy wyłączonym ekranie.",
            "<strong>Powiadomienia:</strong> wyłącznie po to, by system pozwolił aplikacji pokazać krótki komunikat potwierdzający po włączeniu ze skrótu launchera. WakeDeck nie wysyła powiadomień, przypomnień ani promocji.",
        ]),
        ("h2", "Podmioty trzecie"),
        ("p", "WakeDeck nie zawiera reklam, analityki, raportowania awarii ani innych usług podmiotów trzecich, które zbierają dane. Korzysta z bibliotek open source działających w Twoim telefonie."),
        ("h2", "Ta strona internetowa"),
        ("p", "Ta strona nie używa plików cookie, analityki ani skryptów i niczego nie ładuje z innych witryn. Jest hostowana w GitHub Pages, a jej domena jest zarządzana przez Cloudflare. Jak każdy host internetowy, mogą oni przechowywać standardowe logi serwera zgodnie z własnymi politykami prywatności. Deweloper nie otrzymuje tych danych i ich nie wykorzystuje."),
        ("h2", "Dzieci"),
        ("p", "WakeDeck nie jest skierowany do dzieci i nie zbiera danych od nikogo."),
        ("h2", "Zmiany"),
        ("p", "Jeśli ta polityka ulegnie zmianie, nowa wersja zostanie opublikowana pod tym adresem z nową datą."),
        ("h2", "Kontakt"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Podmioty trzecie"

SUPPORT = ("Wesprzyj WakeDeck", "WakeDeck jest darmowy i bez reklam. Jeśli oszczędza Ci czas i chcesz się dołożyć, darowizna pomaga pokryć opłatę w Play Store i domenę.", "Przekaż darowiznę")

TIPS = ("Darowizny", "Przycisk darowizny na tej stronie otwiera Ko-fi, witrynę podmiotu trzeciego z własną polityką prywatności. Aplikacja nie ma funkcji darowizn. Każdą darowiznę obsługuje Ko-fi i jego dostawcy płatności, a deweloper widzi tylko to, co pokazuje Ko-fi, na przykład imię i wiadomość, które zdecydujesz się podać.")

GUIDE = ("Włącz Wake-on-LAN i zdalne wyłączanie na komputerze z Linuksem, Windows lub Makiem, aby WakeDeck mógł go włączyć z telefonu.",
         "Zastąp PASTE_KEY_LINE_FROM_APP wierszem klucza widocznym na ekranie Klucz SSH w WakeDeck.")

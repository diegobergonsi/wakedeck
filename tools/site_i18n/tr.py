KEY, CODE, NAME = "tr", "tr", "Türkçe"

INDEX = dict(
    title="WakeDeck: Android için Wake-on-LAN widget’ları",
    desc="Bilgisayarlarınızı Android ana ekranından açın. Özelleştirilebilir widget’lar, kısayollar ve Hızlı Ayarlar karoları. Reklam yok, analiz yok, hesap yok.",
    ogdesc="Cihazlarınızı akıllı telefonunuzdan açın. Ana ekran için özelleştirilebilir widget’lar. Reklam yok, analiz yok, hesap yok.",
    skip="İçeriğe geç", language="Dil", features="Özellikler", how="Nasıl çalışır", privacy="Gizlilik", contact="İletişim",
    h1=("Cihazlarınızı ", "akıllı telefonunuzdan", " açın"),
    lead="WakeDeck, kendi tasarladığınız widget’lardan doğrudan Wake-on-LAN sihirli paketi göndererek bilgisayarlarınızı açar. Ana ekranda tek dokunuş yeter, uygulama açılmaz bile.",
    play="Google Play Store: yakında", soon="Android 8.0 ve üzeri için ücretsiz. Reklam yok, analiz yok, hesap yok.",
    feat_h="Widget’ın etrafında tasarlandı", feat_sub="Ana ekranınız uzaktan kumandanız. Nasıl göründüğünü ve nerede durduğunu siz seçin.",
    cards=[
        ("Kendi tasarladığınız widget’lar", "Tek cihaz, liste ya da ızgara. Renkleri, saydamlığı, köşe yarıçapını, kenarlığı, simgeyi, yazı boyutunu ve düzeni seçin. Çevrimiçi ve çevrimdışı durumu arka planda güncellenir."),
        ("Tek dokunuş, uygulama yok", "Widget’ın kendisinde Aç’a dokunun. Başlatıcı kısayollarından ve Hızlı Ayarlar karolarından da açabilirsiniz."),
        ("Neyin açık olduğunu bilin", "Hangi cihazların çevrimiçi olduğunu görün. Hepsini bir düğmeyle ya da listeyi aşağı çekerek yenileyin."),
        ("Gruplar ve Tümünü aç", "Cihazları Ofis ya da Laboratuvar gibi gruplara ayırın ve bir düğmeyle bütün grubu açın."),
        ("SSH ile uzaktan kapatma", "İsteğe bağlı. WakeDeck, Android Keystore’da tutulan bir anahtarla oturum açar. Hiçbir zaman parola istemez ya da saklamaz ve her bilgisayarın ana makine anahtarını sabitler."),
        ("Tasarımdan gizli", "Reklam yok, analiz yok, hesap yok. Cihazlarınız ve ayarlarınız telefonunuzda kalır, uygulama yalnızca eklediğiniz adreslerle konuşur."),
    ],
    how_h="Üç adım", how_sub="Uygulama içi bir kılavuz, Windows ve Linux için bilgisayar tarafında size yol gösterir.",
    steps=[
        ("Wake-on-LAN’ı açın", "Bilgisayarınızın BIOS veya UEFI ayarlarında ve ağ kartı ayarlarında etkinleştirin. Kablolu Ethernet en iyi sonucu verir."),
        ("Cihazınızı ekleyin", "Bir ad ve MAC adresi girin. Çevrimiçi durumunu görmek istiyorsanız IP adresini de ekleyin."),
        ("Bir widget ekleyin", "Ana ekrana uzun basın, bir WakeDeck widget’ı ekleyin ve istediğiniz gibi biçimlendirin."),
    ],
    priv_h="Verileriniz sizde kalır", priv_p="WakeDeck’in sunucusu ve hesabı yoktur. Kişisel veri toplamaz ya da paylaşmaz. Telefonunuzda tam olarak neyin saklandığını görmek için kısa gizlilik politikasını okuyun.", priv_btn="Gizlilik politikası",
    away_h="Evden uzakta mısınız?", away_p="Kendi VPN’inizi kullanın ve cihazın yayın adresini alt ağınızınkine ayarlayın, örneğin 192.168.1.255. VPN’inizin ve yönlendiricinizin bunu iletmesi gerekir. Uygulama içi yardım ayrıntıları ve sık görülen hata iletilerini açıklar.",
)

PRIVACY = dict(
    title="Gizlilik politikası", meta_title="Gizlilik politikası | WakeDeck", date_label="Son güncelleme: 11 Ekim 2026",
    desc="WakeDeck kişisel veri toplamaz, paylaşmaz ve satmaz. Reklam, analiz ya da izleme yoktur. Bu politika telefonunuzda nelerin kaldığını açıklar.",
    ogdesc="Reklam yok, analiz yok, hesap yok. Verileriniz telefonunuzda kalır.",
    note="Bu çeviri kolaylık için sunulmuştur. İngilizce sürümle fark olması hâlinde İngilizce sürüm geçerlidir.",
    summary="<strong>Kısaca:</strong> WakeDeck hiçbir kişisel veriyi toplamaz, paylaşmaz ya da satmaz; reklam, analiz ve izleme içermez. Girdiğiniz her şey telefonunuzda kalır.",
    intro="WakeDeck, Diego Bergonsi tarafından geliştirilen bir Android Wake-on-LAN uygulamasıdır. Bu politika, uygulamanın verilerinizle ne yaptığını açıklar.",
    blocks=[
        ("h2", "Telefonunuzda kalanlar"),
        ("p", "WakeDeck’e girdiğiniz her şey yalnızca telefonunuzda saklanır:"),
        ("ul", [
            "Eklediğiniz cihazlar: ad, MAC adresi, IP adresi veya ana makine adı, yayın adresi, bağlantı noktaları, işletim sistemi, grup ve isteğe bağlı SSH kullanıcı adı ile bağlantı noktası.",
            "Widget, kısayol ve Hızlı Ayarlar karosu ayarlarınız.",
            "Cihazlarınızın bilinen son çevrimiçi veya çevrimdışı durumu.",
            "İsteğe bağlı uzaktan kapatma özelliği için bir SSH anahtar çifti. Özel anahtar Android Keystore içinde oluşturulur ve oradan okunamaz ya da kopyalanamaz. Uygulama hiçbir zaman parola istemez ya da saklamaz. Güvenmeyi seçtiğiniz her bilgisayarın SSH ana makine anahtarının parmak izini de saklar.",
        ]),
        ("p", "Bunların hiçbiri geliştiriciye ya da geliştiricinin işlettiği herhangi bir sunucuya gönderilmez. WakeDeck’in hesabı, sunucusu ve arka ucu yoktur. Uygulama verileri Android bulut yedeklemesinin dışında tutulur."),
        ("h2", "Ağ trafiği"),
        ("p", "WakeDeck yalnızca sizin yapılandırdığınız adreslere bağlanır:"),
        ("ul", [
            "Bir cihaz için belirlediğiniz yayın adresine bir Wake-on-LAN sihirli paketi (UDP).",
            "Çevrimiçi veya çevrimdışı denetimleri: cihazın ana makinesine bir TCP bağlantı denemesi ya da ping.",
            "Uzaktan kapatma, yalnızca dokunup onayladığınızda: kurduğunuz bilgisayara bir SSH bağlantısı.",
        ]),
        ("p", "Bu trafik telefonunuzdan doğrudan kendi cihazlarınıza, Wi-Fi ağınız ya da işlettiğiniz bir VPN üzerinden gider. Geliştiriciden geçmez."),
        ("h2", "Oluşturmayı seçtiğiniz dosyalar"),
        ("p", "Dışa aktarma işlevi cihaz listenizi seçtiğiniz bir dosyaya yazar. Dosya SSH anahtarınızı ya da güvenilen ana makine anahtarlarını içermez. WakeDeck dosyayı hiçbir yere yüklemez. Dosyayla ne yapacağınız size kalmıştır."),
        ("p", "İçe aktarma işlevi seçtiğiniz bir dosyayı okur. Herhangi bir şey eklemeden önce WakeDeck dosyadaki cihazları gösterir ve ev ağınızın dışını işaret edenler konusunda sizi uyarır."),
        ("h2", "İzinler"),
        ("ul", [
            "<strong>İnternet ve ağ durumu:</strong> paketi göndermek ve cihaz durumunu denetlemek için.",
            "<strong>Wake lock:</strong> ekran kapalıyken bir açma işlemini ya da durum denetimini tamamlamak için.",
            "<strong>Bildirimler:</strong> yalnızca sistemin, başlatıcı kısayolundan açma sonrasında uygulamanın kısa onay iletisini göstermesine izin vermesi için. WakeDeck bildirim, hatırlatma ya da tanıtım göndermez.",
        ]),
        ("h2", "Üçüncü taraflar"),
        ("p", "WakeDeck veri toplayan reklam, analiz, çökme raporlama ya da başka üçüncü taraf hizmetleri içermez. Telefonunuzda çalışan açık kaynaklı kitaplıklar kullanır."),
        ("h2", "Bu web sitesi"),
        ("p", "Bu web sitesi çerez, analiz ve betik kullanmaz, başka sitelerden hiçbir şey yüklemez. GitHub Pages üzerinde barındırılır ve alan adı Cloudflare ile yönetilir. Her web barındırıcısında olduğu gibi, kendi gizlilik politikaları uyarınca standart sunucu günlükleri tutabilirler. Geliştirici bu verileri almaz ve kullanmaz."),
        ("h2", "Çocuklar"),
        ("p", "WakeDeck çocuklara yönelik değildir ve kimseden veri toplamaz."),
        ("h2", "Değişiklikler"),
        ("p", "Bu politika değişirse yeni sürüm yeni bir tarihle bu adreste yayımlanır."),
        ("h2", "İletişim"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Üçüncü taraflar"

SUPPORT = ("WakeDeck’e destek olun", "WakeDeck ücretsizdir ve reklamsızdır. Size zaman kazandırıyorsa ve katkıda bulunmak isterseniz, bir bağış Play Store ücretini ve alan adını karşılamaya yardımcı olur.", "Bağış yap")

TIPS = ("Bağışlar", "Bu web sitesindeki bağış düğmesi, kendi gizlilik politikası olan üçüncü taraf bir site olan Ko-fi’yi açar. Uygulamada bağış özelliği yoktur. Her bağışı Ko-fi ve ödeme sağlayıcıları işler; geliştirici yalnızca Ko-fi’nin gösterdiklerini, örneğin vermeyi seçtiğiniz ad ve iletiyi görür.")

GUIDE = ("Linux, Windows veya Mac bilgisayarınızda Wake-on-LAN ve uzaktan kapatmayı etkinleştirin; böylece WakeDeck onu telefonunuzdan açabilir.",
         "PASTE_KEY_LINE_FROM_APP yerine WakeDeck’in SSH anahtarı ekranında gösterilen anahtar satırını yazın.")

KEY, CODE, NAME = "id", "id", "Bahasa Indonesia"

INDEX = dict(
    title="WakeDeck: widget Wake-on-LAN untuk Android",
    desc="Nyalakan PC Anda dari layar utama Android. Widget yang bisa disesuaikan, pintasan, dan ubin Pengaturan cepat. Tanpa iklan, tanpa analitik, tanpa akun.",
    ogdesc="Nyalakan perangkat Anda dari ponsel. Widget layar utama yang bisa disesuaikan. Tanpa iklan, tanpa analitik, tanpa akun.",
    skip="Langsung ke konten", language="Bahasa", features="Fitur", how="Cara kerja", privacy="Privasi", contact="Kontak",
    h1=("Nyalakan perangkat Anda dari ", "ponsel", ""),
    lead="WakeDeck menyalakan PC Anda dengan paket ajaib Wake-on-LAN, langsung dari widget yang Anda rancang sendiri. Satu ketukan di layar utama, dan aplikasinya bahkan tidak perlu dibuka.",
    play="Google Play Store: segera hadir", soon="Gratis untuk Android 8.0 ke atas. Tanpa iklan, tanpa analitik, tanpa akun.",
    feat_h="Dibangun di sekitar widget", feat_sub="Layar utama Anda adalah remote kontrolnya. Pilih tampilan dan posisinya.",
    cards=[
        ("Widget rancangan Anda", "Satu perangkat, daftar, atau kisi. Pilih warna, transparansi, radius sudut, bingkai, ikon, ukuran teks, dan tata letak. Status online dan offline diperbarui di latar belakang."),
        ("Satu ketukan, tanpa membuka aplikasi", "Ketuk Nyalakan langsung di widget. Anda juga bisa menyalakan dari pintasan launcher dan ubin Pengaturan cepat."),
        ("Tahu mana yang menyala", "Lihat perangkat mana yang online. Segarkan semuanya dengan satu tombol, atau tarik daftar ke bawah."),
        ("Grup dan Nyalakan semua", "Kelompokkan perangkat, misalnya Kantor atau Lab, lalu nyalakan satu grup penuh dengan satu tombol."),
        ("Pematian jarak jauh lewat SSH", "Opsional. WakeDeck masuk dengan kunci yang disimpan di Android Keystore. Aplikasi tidak pernah meminta atau menyimpan kata sandi, dan menyematkan kunci host setiap PC."),
        ("Privat sejak awal", "Tanpa iklan, tanpa analitik, tanpa akun. Perangkat dan pengaturan Anda tetap di ponsel, dan aplikasi hanya berkomunikasi dengan alamat yang Anda tambahkan."),
    ],
    how_h="Tiga langkah", how_sub="Panduan di dalam aplikasi mengarahkan Anda menyiapkan sisi PC, untuk Windows, Linux, dan macOS.",
    steps=[
        ("Aktifkan Wake-on-LAN", "Aktifkan di BIOS atau UEFI PC Anda dan di pengaturan kartu jaringan. Ethernet berkabel bekerja paling baik."),
        ("Tambahkan perangkat", "Masukkan nama dan alamat MAC. Tambahkan alamat IP jika ingin melihat status online."),
        ("Tambahkan widget", "Tekan lama layar utama, tambahkan widget WakeDeck, dan atur sesuai selera Anda."),
    ],
    priv_h="Data Anda tetap milik Anda", priv_p="WakeDeck tidak punya server dan tidak punya akun. Aplikasi ini tidak mengumpulkan atau membagikan data pribadi. Baca kebijakan privasi yang singkat untuk melihat dengan tepat apa yang disimpan di ponsel Anda.", priv_btn="Kebijakan privasi",
    away_h="Sedang jauh dari rumah?", away_p="Gunakan VPN Anda sendiri dan setel alamat broadcast perangkat ke alamat subnet Anda, misalnya 192.168.1.255. VPN dan router Anda harus meneruskannya. Bantuan di dalam aplikasi menjelaskan detailnya dan pesan galat yang umum.",
)

PRIVACY = dict(
    title="Kebijakan privasi", meta_title="Kebijakan privasi | WakeDeck", date_label="Terakhir diperbarui: 11 Oktober 2026",
    desc="WakeDeck tidak mengumpulkan, membagikan, atau menjual data pribadi. Tidak ada iklan, analitik, atau pelacakan. Kebijakan ini menjelaskan apa yang tetap berada di ponsel Anda.",
    ogdesc="Tanpa iklan, tanpa analitik, tanpa akun. Data Anda tetap di ponsel.",
    note="Terjemahan ini disediakan untuk kemudahan. Jika ada perbedaan dengan versi bahasa Inggris, versi bahasa Inggris yang berlaku.",
    summary="<strong>Singkatnya:</strong> WakeDeck tidak mengumpulkan, membagikan, atau menjual data pribadi apa pun, dan tidak memiliki iklan, analitik, atau pelacakan. Semua yang Anda masukkan tetap di ponsel Anda.",
    intro="WakeDeck adalah aplikasi Wake-on-LAN untuk Android buatan Diego Bergonsi. Kebijakan ini menjelaskan apa yang dilakukan aplikasi dengan data Anda.",
    blocks=[
        ("h2", "Yang tetap di ponsel Anda"),
        ("p", "Semua yang Anda masukkan di WakeDeck hanya disimpan di ponsel Anda:"),
        ("ul", [
            "Perangkat yang Anda tambahkan: nama, alamat MAC, alamat IP atau nama host, alamat broadcast, port, sistem operasi, grup, serta nama pengguna dan port SSH yang bersifat opsional.",
            "Pengaturan widget, pintasan, dan ubin Pengaturan cepat Anda.",
            "Status online atau offline terakhir yang diketahui dari perangkat Anda.",
            "Untuk fitur pematian jarak jauh yang opsional, sepasang kunci SSH. Kunci privat dibuat di dalam Android Keystore dan tidak dapat dibaca atau disalin dari sana. Aplikasi tidak pernah meminta atau menyimpan kata sandi. Aplikasi juga menyimpan sidik jari kunci host SSH dari setiap PC yang Anda pilih untuk dipercaya.",
        ]),
        ("p", "Tidak ada satu pun dari ini yang dikirim ke pengembang atau ke server mana pun yang dijalankan pengembang. WakeDeck tidak memiliki akun, server, maupun backend. Data aplikasi dikecualikan dari pencadangan cloud Android."),
        ("h2", "Lalu lintas jaringan"),
        ("p", "WakeDeck hanya terhubung ke alamat yang Anda konfigurasi:"),
        ("ul", [
            "Paket ajaib Wake-on-LAN (UDP) ke alamat broadcast yang Anda atur untuk perangkat.",
            "Pemeriksaan online atau offline: upaya koneksi TCP atau ping ke host perangkat.",
            "Pematian jarak jauh, hanya saat Anda mengetuknya dan mengonfirmasi: koneksi SSH ke PC yang Anda siapkan.",
        ]),
        ("p", "Lalu lintas ini langsung dari ponsel Anda ke perangkat Anda sendiri, melalui Wi-Fi Anda atau VPN yang Anda kelola. Lalu lintas tidak melewati pengembang."),
        ("h2", "Berkas yang Anda pilih untuk dibuat"),
        ("p", "Fungsi ekspor menulis daftar perangkat Anda ke berkas yang Anda pilih. Berkas tidak berisi kunci SSH Anda atau kunci host tepercaya. WakeDeck tidak mengunggahnya ke mana pun. Apa yang Anda lakukan dengan berkas itu terserah Anda."),
        ("p", "Fungsi impor membaca berkas yang Anda pilih. Sebelum menambahkan apa pun, WakeDeck menampilkan perangkat di dalam berkas dan memperingatkan Anda tentang perangkat yang mengarah ke luar jaringan rumah Anda."),
        ("h2", "Izin"),
        ("ul", [
            "<strong>Internet dan status jaringan:</strong> untuk mengirim paket dan memeriksa status perangkat.",
            "<strong>Wake lock:</strong> untuk menyelesaikan proses menyalakan atau pemeriksaan status saat layar mati.",
            "<strong>Notifikasi:</strong> hanya agar sistem mengizinkan aplikasi menampilkan pesan konfirmasi singkat setelah menyalakan dari pintasan launcher. WakeDeck tidak mengirim notifikasi, pengingat, atau promosi.",
        ]),
        ("h2", "Pihak ketiga"),
        ("p", "WakeDeck tidak berisi iklan, analitik, pelaporan kerusakan, atau layanan pihak ketiga lain yang mengumpulkan data. Aplikasi memakai pustaka sumber terbuka yang berjalan di ponsel Anda."),
        ("h2", "Situs web ini"),
        ("p", "Situs web ini tidak memakai cookie, analitik, maupun skrip, dan tidak memuat apa pun dari situs lain. Situs ini dihosting di GitHub Pages dan domainnya dikelola melalui Cloudflare. Seperti penyedia hosting web mana pun, mereka dapat menyimpan log server standar sesuai kebijakan privasi masing-masing. Pengembang tidak menerima atau menggunakan data tersebut."),
        ("h2", "Anak-anak"),
        ("p", "WakeDeck tidak ditujukan untuk anak-anak dan tidak mengumpulkan data dari siapa pun."),
        ("h2", "Perubahan"),
        ("p", "Jika kebijakan ini berubah, versi baru akan dipublikasikan di alamat ini dengan tanggal baru."),
        ("h2", "Kontak"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Pihak ketiga"

SUPPORT = ("Dukung WakeDeck", "WakeDeck gratis dan tanpa iklan. Jika aplikasi ini menghemat waktu Anda dan Anda ingin ikut membantu, donasi membantu menutup biaya Play Store dan domain.", "Berdonasi")

TIPS = ("Donasi", "Tombol donasi di situs web ini membuka Ko-fi, situs pihak ketiga dengan kebijakan privasinya sendiri. Aplikasi tidak memiliki fitur donasi. Ko-fi dan penyedia pembayarannya menangani setiap donasi, dan pengembang hanya melihat apa yang ditampilkan Ko-fi, seperti nama dan pesan yang Anda pilih untuk diberikan.")

GUIDE = ("Aktifkan Wake-on-LAN dan pematian jarak jauh di PC Linux, Windows, atau Mac Anda agar WakeDeck dapat menyalakannya dari ponsel.",
         "Ganti PASTE_KEY_LINE_FROM_APP dengan baris kunci yang ditampilkan di layar Kunci SSH WakeDeck.")

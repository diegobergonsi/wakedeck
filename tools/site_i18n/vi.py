KEY, CODE, NAME = "vi", "vi", "Tiếng Việt"

INDEX = dict(
    title="WakeDeck: widget Wake-on-LAN cho Android",
    desc="Bật máy tính của bạn ngay từ màn hình chính Android. Widget tùy biến, lối tắt và ô Cài đặt nhanh. Không quảng cáo, không phân tích, không tài khoản.",
    ogdesc="Bật thiết bị của bạn từ điện thoại thông minh. Widget tùy biến cho màn hình chính. Không quảng cáo, không phân tích, không tài khoản.",
    skip="Chuyển đến nội dung", language="Ngôn ngữ", features="Tính năng", how="Cách hoạt động", privacy="Quyền riêng tư", contact="Liên hệ",
    h1=("Bật thiết bị của bạn từ ", "điện thoại", ""),
    lead="WakeDeck bật máy tính của bạn bằng gói tin ma thuật Wake-on-LAN, ngay từ các widget do chính bạn thiết kế. Chạm một lần trên màn hình chính, ứng dụng thậm chí không cần mở.",
    play="Google Play Store: sắp ra mắt", soon="Miễn phí cho Android 8.0 trở lên. Không quảng cáo, không phân tích, không tài khoản.",
    feat_h="Xây dựng quanh widget", feat_sub="Màn hình chính chính là điều khiển từ xa của bạn. Chọn kiểu dáng và vị trí tùy thích.",
    cards=[
        ("Widget do bạn thiết kế", "Một thiết bị, một danh sách hoặc một lưới. Chọn màu sắc, độ trong suốt, bán kính góc, viền, biểu tượng, cỡ chữ và bố cục. Trạng thái trực tuyến và ngoại tuyến được cập nhật ở chế độ nền."),
        ("Chạm một lần, không cần mở ứng dụng", "Chạm Bật ngay trong widget. Bạn cũng có thể bật từ lối tắt trên trình khởi chạy và ô Cài đặt nhanh."),
        ("Biết thiết bị nào đang bật", "Xem thiết bị nào đang trực tuyến. Làm mới tất cả bằng một nút hoặc kéo danh sách xuống."),
        ("Nhóm và Bật tất cả", "Gom thiết bị thành nhóm như Văn phòng hoặc Phòng thí nghiệm, rồi bật cả nhóm chỉ bằng một nút."),
        ("Tắt máy từ xa qua SSH", "Tùy chọn. WakeDeck đăng nhập bằng khóa lưu trong Android Keystore. Ứng dụng không bao giờ hỏi hay lưu mật khẩu, và ghim khóa máy chủ của từng máy tính."),
        ("Riêng tư từ thiết kế", "Không quảng cáo, không phân tích, không tài khoản. Thiết bị và cài đặt của bạn nằm trên điện thoại, và ứng dụng chỉ kết nối tới những địa chỉ bạn thêm."),
    ],
    how_h="Ba bước", how_sub="Hướng dẫn trong ứng dụng sẽ chỉ bạn cách thiết lập phía máy tính, cho Windows, Linux và macOS.",
    steps=[
        ("Bật Wake-on-LAN", "Bật trong BIOS hoặc UEFI của máy tính và trong cài đặt card mạng. Ethernet có dây hoạt động tốt nhất."),
        ("Thêm thiết bị", "Nhập tên và địa chỉ MAC. Thêm địa chỉ IP nếu bạn muốn xem trạng thái trực tuyến."),
        ("Thêm widget", "Nhấn giữ màn hình chính, thêm widget WakeDeck và tùy chỉnh theo ý bạn."),
    ],
    priv_h="Dữ liệu của bạn vẫn là của bạn", priv_p="WakeDeck không có máy chủ và không có tài khoản. Ứng dụng không thu thập hay chia sẻ dữ liệu cá nhân. Hãy đọc chính sách quyền riêng tư ngắn gọn để biết chính xác những gì được lưu trên điện thoại của bạn.", priv_btn="Chính sách quyền riêng tư",
    away_h="Khi ở xa nhà?", away_p="Dùng VPN của riêng bạn và đặt địa chỉ quảng bá của thiết bị thành địa chỉ mạng con của bạn, ví dụ 192.168.1.255. VPN và bộ định tuyến của bạn phải chuyển tiếp địa chỉ này. Phần trợ giúp trong ứng dụng giải thích chi tiết và các thông báo lỗi thường gặp.",
)

PRIVACY = dict(
    title="Chính sách quyền riêng tư", meta_title="Chính sách quyền riêng tư | WakeDeck", date_label="Cập nhật lần cuối: 11 tháng 10 năm 2026",
    desc="WakeDeck không thu thập, chia sẻ hay bán dữ liệu cá nhân. Không có quảng cáo, phân tích hay theo dõi. Chính sách này giải thích những gì được giữ lại trên điện thoại của bạn.",
    ogdesc="Không quảng cáo, không phân tích, không tài khoản. Dữ liệu của bạn ở lại trên điện thoại.",
    note="Bản dịch này được cung cấp để tiện theo dõi. Nếu có khác biệt so với bản tiếng Anh, bản tiếng Anh sẽ được ưu tiên.",
    summary="<strong>Tóm tắt:</strong> WakeDeck không thu thập, chia sẻ hay bán bất kỳ dữ liệu cá nhân nào, và không có quảng cáo, phân tích hay theo dõi. Mọi thứ bạn nhập đều ở lại trên điện thoại.",
    intro="WakeDeck là ứng dụng Wake-on-LAN cho Android do Diego Bergonsi tạo ra. Chính sách này giải thích ứng dụng làm gì với dữ liệu của bạn.",
    blocks=[
        ("h2", "Những gì ở lại trên điện thoại"),
        ("p", "Mọi thứ bạn nhập vào WakeDeck chỉ được lưu trên điện thoại của bạn:"),
        ("ul", [
            "Các thiết bị bạn thêm: tên, địa chỉ MAC, địa chỉ IP hoặc tên máy chủ, địa chỉ quảng bá, cổng, hệ điều hành, nhóm, và tên người dùng cùng cổng SSH (tùy chọn).",
            "Cài đặt widget, lối tắt và ô Cài đặt nhanh của bạn.",
            "Trạng thái trực tuyến hoặc ngoại tuyến được biết gần nhất của thiết bị.",
            "Với tính năng tắt máy từ xa tùy chọn, một cặp khóa SSH. Khóa riêng được tạo bên trong Android Keystore và không thể đọc hay sao chép ra khỏi đó. Ứng dụng không bao giờ hỏi hay lưu mật khẩu. Ứng dụng cũng lưu dấu vân tay khóa máy chủ SSH của từng máy tính mà bạn đã chọn tin cậy.",
        ]),
        ("p", "Không có gì trong số này được gửi tới nhà phát triển hay bất kỳ máy chủ nào do nhà phát triển vận hành. WakeDeck không có tài khoản, máy chủ hay hệ thống phụ trợ. Dữ liệu ứng dụng bị loại khỏi sao lưu đám mây của Android."),
        ("h2", "Lưu lượng mạng"),
        ("p", "WakeDeck chỉ kết nối tới những địa chỉ bạn cấu hình:"),
        ("ul", [
            "Gói tin ma thuật Wake-on-LAN (UDP) gửi tới địa chỉ quảng bá bạn đặt cho thiết bị.",
            "Kiểm tra trực tuyến hoặc ngoại tuyến: một lần thử kết nối TCP hoặc ping tới máy chủ của thiết bị.",
            "Tắt máy từ xa, chỉ khi bạn chạm và xác nhận: một kết nối SSH tới máy tính bạn đã thiết lập.",
        ]),
        ("p", "Lưu lượng này đi thẳng từ điện thoại của bạn tới các thiết bị của chính bạn, qua Wi-Fi của bạn hoặc qua VPN do bạn vận hành. Nó không đi qua nhà phát triển."),
        ("h2", "Tệp bạn chọn tạo ra"),
        ("p", "Chức năng xuất ghi danh sách thiết bị của bạn vào một tệp do bạn chọn. Tệp không chứa khóa SSH của bạn hay các khóa máy chủ đã tin cậy. WakeDeck không tải tệp lên bất cứ đâu. Việc dùng tệp ra sao là tùy bạn."),
        ("p", "Chức năng nhập đọc một tệp do bạn chọn. Trước khi thêm bất kỳ thứ gì, WakeDeck hiển thị các thiết bị trong tệp và cảnh báo bạn về những thiết bị trỏ ra ngoài mạng gia đình."),
        ("h2", "Quyền"),
        ("ul", [
            "<strong>Internet và trạng thái mạng:</strong> để gửi gói tin và kiểm tra trạng thái thiết bị.",
            "<strong>Wake lock:</strong> để hoàn tất việc bật hoặc kiểm tra trạng thái khi màn hình tắt.",
            "<strong>Thông báo:</strong> chỉ để hệ thống cho phép ứng dụng hiện thông báo xác nhận ngắn sau khi bật từ lối tắt trình khởi chạy. WakeDeck không gửi thông báo, lời nhắc hay khuyến mãi.",
        ]),
        ("h2", "Bên thứ ba"),
        ("p", "WakeDeck không chứa quảng cáo, phân tích, báo cáo sự cố hay dịch vụ bên thứ ba nào khác thu thập dữ liệu. Ứng dụng dùng các thư viện mã nguồn mở chạy trên điện thoại của bạn."),
        ("h2", "Trang web này"),
        ("p", "Trang web này không dùng cookie, phân tích hay tập lệnh, và không tải gì từ các trang khác. Trang được lưu trữ trên GitHub Pages và tên miền được quản lý qua Cloudflare. Như mọi dịch vụ lưu trữ web, họ có thể giữ nhật ký máy chủ tiêu chuẩn theo chính sách quyền riêng tư của họ. Nhà phát triển không nhận và không sử dụng dữ liệu đó."),
        ("h2", "Trẻ em"),
        ("p", "WakeDeck không hướng tới trẻ em và không thu thập dữ liệu từ bất kỳ ai."),
        ("h2", "Thay đổi"),
        ("p", "Nếu chính sách này thay đổi, phiên bản mới sẽ được đăng tại địa chỉ này cùng ngày mới."),
        ("h2", "Liên hệ"),
        ("p", 'Diego Bergonsi, <a href="mailto:contact@wakedeck.app">contact@wakedeck.app</a>'),
    ],
)

THIRD = "Bên thứ ba"

SUPPORT = ("Ủng hộ WakeDeck", "WakeDeck miễn phí và không có quảng cáo. Nếu ứng dụng giúp bạn tiết kiệm thời gian và bạn muốn góp sức, một khoản quyên góp sẽ giúp trang trải phí Play Store và tên miền.", "Quyên góp")

TIPS = ("Quyên góp", "Nút quyên góp trên trang web này mở Ko-fi, một trang bên thứ ba có chính sách quyền riêng tư riêng. Ứng dụng không có tính năng quyên góp. Ko-fi và các nhà cung cấp thanh toán của họ xử lý mọi khoản quyên góp, và nhà phát triển chỉ thấy những gì Ko-fi hiển thị, chẳng hạn tên và lời nhắn bạn chọn cung cấp.")

GUIDE = ("Bật Wake-on-LAN và tắt máy từ xa trên máy tính Linux, Windows hoặc Mac của bạn để WakeDeck có thể bật máy từ điện thoại.",
         "Thay PASTE_KEY_LINE_FROM_APP bằng dòng khóa hiển thị trong màn hình Khóa SSH của WakeDeck.")

# 🏥 Hospital Management System

Modern ve kullanıcı dostu bir hastane yönetim sistemi mobil uygulaması. Android platformu için Kotlin ve Jetpack Compose teknolojileri kullanılarak geliştirilmiştir.

## 📱 Proje Hakkında

Bu uygulama, hastane süreçlerini dijitalleştirmek ve hasta-doktor-hastane yöneticisi arasındaki iletişimi kolaylaştırmak amacıyla geliştirilmiştir. Modern Android geliştirme teknikleri kullanılarak, kullanıcı deneyimini ön planda tutan bir tasarım benimsenmiştir.

## ✨ Özellikler

### 👤 Hasta Özellikleri
- **Kullanıcı Kayıt ve Giriş Sistemi**: Güvenli hesap oluşturma ve giriş
- **Randevu Alma**: Doktor ve departman seçimi ile randevu oluşturma
- **Randevu Yönetimi**: Mevcut randevuları görüntüleme ve yönetme
- **Profil Yönetimi**: Kişisel bilgileri güncelleme
- **Kan Grubu ve Adres Bilgileri**: Tıbbi bilgilerin saklanması
- **Medikal Raporlar**: Doktor tarafından verilen raporları görüntüleme
- **Hastane Duyuruları**: Önemli hastane duyurularını takip etme

### 👨‍⚕️ Admin/Yönetici Özellikleri
- **Doktor Yönetimi**: Doktor ekleme, güncelleme ve silme
- **Randevu Yönetimi**: Tüm randevuları görüntüleme ve düzenleme
- **Hasta Raporları**: Hastalara medikal rapor gönderme
- **Duyuru Yönetimi**: Hastane duyurularını oluşturma ve yönetme
- **Departman Yönetimi**: Hastane departmanlarını organize etme

### 🏥 Desteklenen Departmanlar
- **Kulak Burun Boğaz (KBB)**
- **Kardiyoloji**
- **Ortopedi**
- **Dahiliye**
- **Genel Cerrahi**

## 🛠️ Teknoloji Yığını

- **Platform**: Android (Native)
- **Programlama Dili**: Kotlin 100%
- **UI Framework**: Jetpack Compose
- **Veritabanı**: SQLite (Android Room olmadan, native SQLiteOpenHelper)
- **Mimari**: MVVM (Model-View-ViewModel)
- **Navigation**: Jetpack Navigation Compose
- **State Management**: Compose State & Remember
- **Material Design**: Material 3 komponentleri

## 📂 Proje Yapısı

```
app/src/main/java/com/eren/hospitalui/
├── admin/                      # Admin giriş ve ana ekranları
├── adminnavigationbar/         # Admin paneli navigasyon ekranları
│   ├── AdminAppointmentScreen.kt
│   ├── AdminDoctorScreen.kt
│   └── AdminMedicineScreen.kt
├── auth/                       # Kimlik doğrulama ve veritabanı
│   ├── DatabaseHelper.kt       # SQLite veritabanı yönetimi
│   ├── LoginScreen.kt          # Kullanıcı giriş ekranı
│   └── RegisterScreen.kt       # Kullanıcı kayıt ekranı
├── home/                       # Ana ekran ve MainActivity
├── navigationbar/              # Kullanıcı navigasyon ekranları
│   ├── AccountScreen.kt        # Profil yönetimi
│   ├── AppointmentScreen.kt    # Randevu alma
│   └── MedicineScreen.kt       # Medikal raporlar
├── repository/                 # Veri katmanı
├── theme/                      # UI tema ve renkler
└── utils/                      # Yardımcı fonksiyonlar
```

## 💾 Veritabanı Şeması

Uygulama 6 ana tablo kullanmaktadır:

### 👥 Users (Kullanıcılar)
- ID, İsim, Soyisim, Kullanıcı adı, Şifre, Kan grubu, Adres

### 📅 Appointments (Randevular)
- ID, Tarih, Saat, Doktor adı, Departman

### 👨‍⚕️ Doctors (Doktorlar)
- ID, İsim, Departman

### 👑 Admins (Yöneticiler)
- ID, Kullanıcı adı, Şifre

### 📋 Reports (Raporlar)
- ID, Başlık, İçerik, Kullanıcı adı

### 📢 Announcements (Duyurular)
- ID, Başlık, İçerik, Zaman damgası

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Android Studio Arctic Fox veya üzeri
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34
- Kotlin 1.8+
- Gradle 8.0+

### Kurulum Adımları

1. **Repository'yi klonlayın**
```bash
git clone https://github.com/oxygeneafk/hospital.git
cd hospital
```

2. **Android Studio'da açın**
   - Android Studio'yu başlatın
   - "Open an existing project" seçin
   - Klonladığınız klasörü seçin

3. **Gradle Sync**
   - Android Studio otomatik olarak gradle sync başlatacaktır
   - Gerekli dependencies indirilecektir

4. **Uygulamayı çalıştırın**
   - Emulator veya fiziksel cihaz bağlayın
   - Run butonuna tıklayın veya `Shift + F10` kullanın

## 📱 Kullanım Kılavuzu

### İlk Kurulum
1. Uygulamayı ilk açtığınızda kayıt ekranı gelecektir
2. Hesap oluşturun veya mevcut hesabınızla giriş yapın
3. Admin girişi için ayrı bir giriş ekranı mevcuttur

### Hasta İşlemleri
1. **Randevu Alma**: Departman seçin → Doktor seçin → Tarih ve saat belirleyin
2. **Randevu Yönetimi**: Mevcut randevularınızı görüntüleyin
3. **Profil Güncelleme**: Kişisel bilgilerinizi düzenleyin
4. **Raporlar**: Size gönderilen medikal raporları kontrol edin

### Admin İşlemleri
1. **Doktor Yönetimi**: Yeni doktor ekleyin, mevcut doktorları düzenleyin
2. **Randevu Kontrolü**: Tüm randevuları görüntüleyin ve düzenleyin
3. **Rapor Gönderme**: Hastalara medikal rapor gönderin
4. **Duyuru Yayınlama**: Hastane duyurularını yayınlayın

## 🔧 Geliştirme Notları

### Önemli Özellikler
- **Real-time Updates**: Veritabanı değişiklikleri anında UI'a yansıtılır
- **Form Validation**: Tüm girdi alanlarında doğrulama kontrolü
- **Error Handling**: Hata durumlarında kullanıcı dostu mesajlar
- **Responsive Design**: Farklı ekran boyutlarına uyumlu tasarım

### Kod Kalitesi
- Clean Architecture prensiplerine uygun kod yapısı
- Single Responsibility Principle
- Separation of Concerns
- Material Design 3 guidelines

## 🎯 Gelecek Geliştirmeler

- [ ] Push notification desteği
- [ ] Online senkronizasyon
- [ ] Çoklu dil desteği
- [ ] Dark mode tema
- [ ] Biometrik kimlik doğrulama
- [ ] PDF rapor export özelliği
- [ ] Video konferans entegrasyonu
- [ ] Ödeme sistemi entegrasyonu

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir. Ticari kullanım için geliştirici ile iletişime geçiniz.

## 👥 Katkıda Bulunma

1. Bu repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📞 İletişim

**Geliştirici**: Eren  
**GitHub**: [@oxygeneafk](https://github.com/oxygeneafk)  
**Email**: [İletişim için GitHub profilini ziyaret edin]

---

⭐ Bu projeyi beğendiyseniz yıldız vermeyi unutmayın!

**Hospital Management System** - Modern sağlık hizmetleri için geliştirilmiş kapsamlı mobil çözüm.

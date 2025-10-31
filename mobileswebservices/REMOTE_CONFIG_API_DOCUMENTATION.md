# Remote Config API Dokümantasyonu

## 📋 Genel Bakış

Bu API, mobil uygulamanın dinamik içeriklerini (faydalı linkler, yapılandırmalar) yönetmek için kullanılır.

**Base URL:** `http://10.136.1.11:8080/mobilewebservices/config`

## 🔗 Endpoint'ler

### 1. Aktif Faydalı Linkleri Getir (Mobil App)

Mobil uygulamanın kullandığı ana endpoint. Sadece aktif linkleri döner.

**Endpoint:** `GET /useful-links`

**Response:** `200 OK`
```json
[
  {
    "id": "1",
    "title": "Hastane Randevu",
    "icon": "medical-outline",
    "url": "https://ganteptiphastaportali.mergentech.com.tr/#/auth/login",
    "description": "GAÜN Tıp Fakültesi Hastanesi randevu sistemi",
    "color": "#CC0000",
    "order": 1,
    "isActive": true
  }
]
```

**Curl Örneği:**
```bash
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/useful-links"
```

**PowerShell Örneği:**
```powershell
Invoke-RestMethod -Uri "http://10.136.1.11:8080/mobilewebservices/config/useful-links" -Method Get
```

---

### 2. Config Versiyonunu Getir (Mobil App)

Mobil uygulamanın güncelleme kontrolü için kullandığı endpoint.

**Endpoint:** `GET /version`

**Response:** `200 OK`
```json
{
  "version": "1.0.0",
  "lastUpdated": "2025-10-14T10:30:00",
  "minAppVersion": "1.0.0"
}
```

**Curl Örneği:**
```bash
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/version"
```

---

### 3. Tüm Linkleri Getir (Admin)

Aktif ve inaktif tüm linkleri döner.

**Endpoint:** `GET /useful-links/all`

**Response:** `200 OK`
```json
[
  {
    "id": "1",
    "title": "Hastane Randevu",
    "icon": "medical-outline",
    "url": "https://ganteptiphastaportali.mergentech.com.tr/#/auth/login",
    "description": "GAÜN Tıp Fakültesi Hastanesi randevu sistemi",
    "color": "#CC0000",
    "order": 1,
    "isActive": true
  },
  {
    "id": "6",
    "title": "Eski Link",
    "icon": "link-outline",
    "url": "https://old-link.com",
    "description": "Artık kullanılmayan link",
    "color": "#999999",
    "order": 99,
    "isActive": false
  }
]
```

---

### 4. Yeni Link Oluştur (Admin)

**Endpoint:** `POST /useful-links`

**Request Body:**
```json
{
  "id": "6",
  "title": "Kütüphane",
  "icon": "book-outline",
  "url": "https://kutuphane.gaziantep.edu.tr/",
  "description": "GAÜN Kütüphane sistemi",
  "color": "#009900",
  "order": 6,
  "isActive": true
}
```

**Response:** `201 Created`
```json
{
  "id": "6",
  "title": "Kütüphane",
  "icon": "book-outline",
  "url": "https://kutuphane.gaziantep.edu.tr/",
  "description": "GAÜN Kütüphane sistemi",
  "color": "#009900",
  "order": 6,
  "isActive": true
}
```

**Curl Örneği:**
```bash
curl -X POST "http://10.136.1.11:8080/mobilewebservices/config/useful-links" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "6",
    "title": "Kütüphane",
    "icon": "book-outline",
    "url": "https://kutuphane.gaziantep.edu.tr/",
    "description": "GAÜN Kütüphane sistemi",
    "color": "#009900",
    "order": 6,
    "isActive": true
  }'
```

**PowerShell Örneği:**
```powershell
$body = @{
    id = "6"
    title = "Kütüphane"
    icon = "book-outline"
    url = "https://kutuphane.gaziantep.edu.tr/"
    description = "GAÜN Kütüphane sistemi"
    color = "#009900"
    order = 6
    isActive = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://10.136.1.11:8080/mobilewebservices/config/useful-links" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

---

### 5. Link Güncelle (Admin)

**Endpoint:** `PUT /useful-links/{id}`

**Request Body:**
```json
{
  "id": "1",
  "title": "Hastane Randevu Sistemi",
  "icon": "medical-outline",
  "url": "https://yeni-url.com",
  "description": "Güncellenmiş açıklama",
  "color": "#CC0000",
  "order": 1,
  "isActive": true
}
```

**Response:** `200 OK`

**Curl Örneği:**
```bash
curl -X PUT "http://10.136.1.11:8080/mobilewebservices/config/useful-links/1" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "title": "Hastane Randevu Sistemi",
    "icon": "medical-outline",
    "url": "https://yeni-url.com",
    "description": "Güncellenmiş açıklama",
    "color": "#CC0000",
    "order": 1,
    "isActive": true
  }'
```

---

### 6. Link Durumunu Değiştir (Admin)

Link'i aktif/inaktif yapar.

**Endpoint:** `PATCH /useful-links/{id}/toggle`

**Response:** `200 OK`
```json
{
  "id": "5",
  "title": "Akademik Takvim",
  "icon": "calendar-outline",
  "url": "https://oidb.gaziantep.edu.tr/page.php?url=akademik-takvim-4",
  "description": "Akademik takvim",
  "color": "#CC0000",
  "order": 5,
  "isActive": false
}
```

**Curl Örneği:**
```bash
curl -X PATCH "http://10.136.1.11:8080/mobilewebservices/config/useful-links/5/toggle"
```

---

### 7. Link Sil (Admin)

**Endpoint:** `DELETE /useful-links/{id}`

**Response:** `204 No Content`

**Curl Örneği:**
```bash
curl -X DELETE "http://10.136.1.11:8080/mobilewebservices/config/useful-links/6"
```

---

### 8. Cache Temizle (Admin)

Tüm cache'leri temizler. Güncelleme sonrası kullanılır.

**Endpoint:** `POST /cache/clear`

**Response:** `200 OK`
```json
"All caches cleared successfully"
```

**Curl Örneği:**
```bash
curl -X POST "http://10.136.1.11:8080/mobilewebservices/config/cache/clear"
```

---

## 🎨 Validation Kuralları

### ID
- **Zorunlu:** Evet
- **Maksimum:** 50 karakter
- **Format:** Alfanumerik

### Title
- **Zorunlu:** Evet
- **Minimum:** 3 karakter
- **Maksimum:** 200 karakter

### Icon
- **Zorunlu:** Evet
- **Maksimum:** 100 karakter
- **Format:** Ionicons icon adı (örn: `medical-outline`)

### URL
- **Zorunlu:** Evet
- **Format:** Valid URL

### Description
- **Zorunlu:** Hayır
- **Maksimum:** 500 karakter

### Color
- **Zorunlu:** Evet
- **Format:** Hex renk kodu (örn: `#CC0000`)
- **Pattern:** `^#[0-9A-Fa-f]{6}$`

### Order
- **Zorunlu:** Hayır
- **Varsayılan:** 0
- **Format:** Integer

### IsActive
- **Zorunlu:** Hayır
- **Varsayılan:** true
- **Format:** Boolean

---

## 🔍 Hata Kodları

| HTTP Kodu | Açıklama |
|-----------|----------|
| `200` | Başarılı |
| `201` | Oluşturuldu |
| `204` | İçerik Yok (Silme başarılı) |
| `400` | Geçersiz İstek (Validation hatası) |
| `404` | Bulunamadı |
| `500` | Sunucu Hatası |

---

## 📊 Cache Stratejisi

### Cache Süreleri
- **Useful Links:** 1 saat
- **Config Version:** 1 saat

### Cache Temizleme
Cache otomatik olarak şu durumlarda temizlenir:
- Yeni link ekleme
- Link güncelleme
- Link silme
- Link durumu değiştirme
- Manuel cache temizleme

---

## 🧪 Test Senaryoları

### Senaryo 1: Mobil App İlk Açılış
```bash
# 1. Aktif linkleri çek
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/useful-links"

# 2. Versiyon kontrolü
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/version"
```

### Senaryo 2: Yeni Link Ekleme
```bash
# 1. Yeni link ekle
curl -X POST "http://10.136.1.11:8080/mobilewebservices/config/useful-links" \
  -H "Content-Type: application/json" \
  -d '{"id":"7","title":"Yeni Link","icon":"link-outline","url":"https://example.com","description":"Test","color":"#0099CC","order":7,"isActive":true}'

# 2. Cache temizle
curl -X POST "http://10.136.1.11:8080/mobilewebservices/config/cache/clear"

# 3. Mobil app'te pull-to-refresh yap
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/useful-links"
```

### Senaryo 3: Link Güncelleme
```bash
# 1. Link'i güncelle
curl -X PUT "http://10.136.1.11:8080/mobilewebservices/config/useful-links/1" \
  -H "Content-Type: application/json" \
  -d '{"id":"1","title":"Güncellenmiş Başlık","icon":"medical-outline","url":"https://new-url.com","description":"Yeni açıklama","color":"#CC0000","order":1,"isActive":true}'

# 2. Versiyon kontrolü (otomatik artmış olmalı)
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/version"
```

### Senaryo 4: Link Devre Dışı Bırakma
```bash
# 1. Link'i devre dışı bırak
curl -X PATCH "http://10.136.1.11:8080/mobilewebservices/config/useful-links/5/toggle"

# 2. Aktif linkleri kontrol et (5 numaralı link olmamalı)
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/useful-links"

# 3. Tüm linkleri kontrol et (5 numaralı link isActive=false olmalı)
curl -X GET "http://10.136.1.11:8080/mobilewebservices/config/useful-links/all"
```

---

## 📱 Mobil App Entegrasyonu

Mobil uygulama şu endpoint'leri kullanır:

1. **İlk açılış:** `GET /useful-links` + `GET /version`
2. **Pull-to-refresh:** `GET /useful-links` (forceRefresh=true)
3. **Periyodik kontrol:** `GET /version` (her 1 saatte)

---

## 🔐 Güvenlik Notları

### Şu An
- ✅ Tüm endpoint'ler açık (authentication yok)
- ✅ Validation aktif
- ✅ SQL injection koruması (JPA)

### Gelecek İyileştirmeler
- 🔄 Admin endpoint'leri için authentication ekle
- 🔄 Rate limiting ekle
- 🔄 CORS yapılandırması
- 🔄 HTTPS zorunlu kıl

---

## 📖 Swagger UI

API dokümantasyonuna tarayıcıdan erişin:

**URL:** `http://10.136.1.11:8080/swagger-ui/index.html`

Swagger UI'da tüm endpoint'leri test edebilirsiniz.

---

## 🐛 Sorun Giderme

### Problem: 404 Not Found
**Çözüm:** 
- Endpoint URL'ini kontrol edin
- Spring Boot uygulamasının çalıştığından emin olun
- Port numarasını kontrol edin (8080)

### Problem: 500 Internal Server Error
**Çözüm:**
- Veritabanı bağlantısını kontrol edin
- Tabloların oluşturulduğundan emin olun
- Application log'larını kontrol edin

### Problem: Validation Error (400)
**Çözüm:**
- Request body'yi kontrol edin
- Zorunlu alanların dolu olduğundan emin olun
- Renk formatının doğru olduğunu kontrol edin (#RRGGBB)

### Problem: Cache güncellenmiyor
**Çözüm:**
- `/cache/clear` endpoint'ini çağırın
- Uygulamayı yeniden başlatın

---

## 📞 Destek

Sorularınız için:
- Backend log'larını kontrol edin
- Swagger UI'ı kullanın
- Postman collection'ı import edin

---

## 🎉 Özet

✅ **8 endpoint** hazır
✅ **CRUD operasyonları** tam
✅ **Cache mekanizması** aktif
✅ **Validation** çalışıyor
✅ **Swagger dokümantasyonu** mevcut
✅ **Mobil app entegrasyonu** hazır

**Sonraki Adım:** Veritabanını setup edin ve uygulamayı başlatın!

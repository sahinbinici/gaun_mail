// CSRF token işlemleri için yardımcı fonksiyon
function getCsrfToken() {
    const tokenElement = document.querySelector("meta[name='_csrf']");
    const headerElement = document.querySelector("meta[name='_csrf_header']");
    
    if (!tokenElement || !headerElement) {
        console.warn('CSRF token meta etiketleri bulunamadı');
        return {};
    }
    
    const token = tokenElement.getAttribute("content");
    const header = headerElement.getAttribute("content");
    
    if (!token || !header) {
        console.warn('CSRF token değerleri bulunamadı');
        return {};
    }
    
    return { token, header };
}

// Form aktivasyon fonksiyonları
function activateMailForm(id) {
    if (confirm('Bu mail başvurusunu aktifleştirmek istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = new Headers();
        
        if (token && header) {
            headers.append(header, token);
            headers.append('Content-Type', 'application/json');
        }
        
        fetch('/bim-basvuru/mail/activate/' + id, {
            method: 'POST',
            headers: headers,
            credentials: 'same-origin'
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

function activateEduroamForm(id) {
    if (confirm('Bu eduroam başvurusunu aktifleştirmek istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = {};
        
        if (token && header) {
            headers[header] = token;
        }
        
        fetch('/bim-basvuru/eduroam/activate/' + id, {
            method: 'POST',
            headers: headers
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

function activateWirelessForm(id) {
    if (confirm('Bu kablosuz ağ başvurusunu onaylamak istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = {};
        
        if (token && header) {
            headers[header] = token;
        }
        
        fetch('/bim-basvuru/wireless/activate/' + id, {
            method: 'POST',
            headers: headers
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

function activateIpMacForm(id) {
    if (confirm('Bu IP-MAC başvurusunu onaylamak istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = {};
        
        if (token && header) {
            headers[header] = token;
        }
        
        fetch('/bim-basvuru/ip-mac/activate/' + id, {
            method: 'POST',
            headers: headers
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

function activateCloudForm(id) {
    if (confirm('Bu GAUN Bulut başvurusunu onaylamak istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = {};
        
        if (token && header) {
            headers[header] = token;
        }
        
        fetch('/bim-basvuru/cloud/activate/' + id, {
            method: 'POST',
            headers: headers
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

function activateVpnForm(id) {
    if (confirm('Bu VPN başvurusunu onaylamak istediğinize emin misiniz?')) {
        const { token, header } = getCsrfToken();
        const headers = {};
        
        if (token && header) {
            headers[header] = token;
        }
        
        fetch('/bim-basvuru/vpn/activate/' + id, {
            method: 'POST',
            headers: headers
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                throw new Error('İşlem başarısız oldu');
            }
        }).catch(error => {
            console.error('Hata:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

// Kullanıcı detayları modalı için fonksiyonlar
function showUserDetails(element) {
    const username = element.getAttribute('data-username');
    
    fetch(`/bim-basvuru/api/user-details/${username}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Kullanıcı bilgileri alınamadı');
            }
            return response.json();
        })
        .then(data => {
            // Öğrenci/Personel tipine göre ilgili alanları doldur ve göster
            if (data.type === 'STUDENT') {
                document.getElementById('studentDetails').style.display = 'block';
                document.getElementById('staffDetails').style.display = 'none';
                
                // Öğrenci bilgilerini doldur
                document.getElementById('ogrenciNo').textContent = data.ogrenciNo;
                document.getElementById('studentAd').textContent = data.ad;
                document.getElementById('studentSoyad').textContent = data.soyad;
                document.getElementById('fakulte').textContent = data.fakKod;
                document.getElementById('bolum').textContent = data.bolumAd;
                document.getElementById('program').textContent = data.programAd;
                document.getElementById('sinif').textContent = data.sinif;
            } else {
                document.getElementById('studentDetails').style.display = 'none';
                document.getElementById('staffDetails').style.display = 'block';
                
                // Personel bilgilerini doldur
                document.getElementById('tcKimlikNo').textContent = data.tcKimlikNo;
                document.getElementById('staffAd').textContent = data.ad;
                document.getElementById('staffSoyad').textContent = data.soyad;
                document.getElementById('birim').textContent = data.birim;
                document.getElementById('unvan').textContent = data.unvan;
            }
            
            // Modal'ı göster
            new bootstrap.Modal(document.getElementById('userDetailsModal')).show();
        })
        .catch(error => {
            console.error('Hata:', error);
            alert('Kullanıcı bilgileri alınamadı: ' + error.message);
        });
} 
// CSRF token işlemleri için yardımcı fonksiyon
function getCsrfToken() {
    const tokenMeta = document.querySelector("meta[name='_csrf']");
    const headerMeta = document.querySelector("meta[name='_csrf_header']");
    const token = tokenMeta ? tokenMeta.getAttribute("content") : null;
    const header = headerMeta ? headerMeta.getAttribute("content") : null;
    return { token, header };
}

// Form aktivasyon fonksiyonları
function activateForm(formType, id) {
    if (confirm(`Bu ${formType} başvurusunu aktifleştirmek istediğinize emin misiniz?`)) {
        const { token, header } = getCsrfToken();
        const headers = {};
        if (token && header) {
            headers[header] = token;
        }
        headers['Content-Type'] = 'application/json';

        // Aktif sekmeyi URL'ye ekle
        const activeTab = document.querySelector('.nav-link.active');
        const activeTabId = activeTab ? activeTab.getAttribute('href') : '#mail';
        window.location.hash = activeTabId;

        const baseUrl = window.location.origin;
        const url = `${baseUrl}/admin/${formType}/activate/${id}`;

        fetch(url, {
            method: 'POST',
            headers: headers,
            credentials: 'include'
        }).then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    console.error('Error response:', text);
                    throw new Error('İşlem başarısız oldu');
                });
            }
            location.reload();
        }).catch(error => {
            console.error('Error:', error);
            alert('Başvuru aktifleştirilemedi: ' + error.message);
        });
    }
}

// Her form tipi için aktivasyon fonksiyonları
function activateMailForm(id) {
    activateForm('mail', id);
}

function activateEduroamForm(id) {
    activateForm('eduroam', id);
}

// Kullanıcı detayları modalı için fonksiyonlar
function showUserDetails(element) {
    const username = element.getAttribute('data-username');

    // Ensure we're always using HTTPS
    const baseUrl = window.location.protocol === 'https:' ? window.location.origin : window.location.origin.replace('http:', 'https:');
    fetch(`${baseUrl}/admin/user-details/${username}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Kullanıcı bilgileri alınamadı');
        }
        return response.json();
    })
    .then(data => {
        console.log('Kullanıcı verileri:', data); // Debug: Gelen verileri logla
        if (data.userType === 'student') {
            document.getElementById('studentDetails').style.display = 'block';
            document.getElementById('staffDetails').style.display = 'none';

            // Öğrenci bilgilerini güncelle
            document.getElementById('student_ogrenciNo').textContent = data.ogrenciNo || '';
            document.getElementById('student_tcKimlikNo').textContent = data.tcKimlikNo.toString() || '';
            document.getElementById('student_ad').textContent = data.ad || '';
            document.getElementById('student_soyad').textContent = data.soyad || '';
            document.getElementById('student_gsm1').textContent = data.gsm1 || '';
            document.getElementById('student_eposta1').textContent = data.eposta1 || '';
            document.getElementById('student_fakulte').textContent = data.fakulte || '';
            document.getElementById('student_bolumAd').textContent = data.bolumAd || '';
            document.getElementById('student_programAd').textContent = data.programAd || '';
            document.getElementById('student_sinif').textContent = data.egitimDerecesi || '';
        } else {
            document.getElementById('studentDetails').style.display = 'none';
            document.getElementById('staffDetails').style.display = 'block';

            // Personel bilgilerini güncelle
            document.getElementById('staff_tcKimlikNo').textContent = data.tcKimlikNo || '';
            document.getElementById('staff_ad').textContent = data.ad || '';
            document.getElementById('staff_soyad').textContent = data.soyad || '';
            document.getElementById('staff_gsm').textContent = data.gsm || '';
            document.getElementById('staff_birim').textContent = data.birim || '';
            document.getElementById('staff_unvan').textContent = data.unvan || '';
        }

        const userDetailsModal = new bootstrap.Modal(document.getElementById('userDetailsModal'));
        userDetailsModal.show();
    })
    .catch(error => {
        console.error('Hata:', error);
        alert('Kullanıcı bilgileri alınamadı: ' + error.message);
    });
}

// Form silme fonksiyonu
function deleteForm(formType, formId) {
    if (!confirm('Bu başvuruyu silmek istediğinizden emin misiniz?')) {
        return;
    }

    const { token, header } = getCsrfToken();
    const headers = {};
    if (token && header) {
        headers[header] = token;
    }

    const baseUrl = window.location.origin;
    fetch(`${baseUrl}/admin/${formType}/delete/${formId}`, {
        method: 'POST',
        headers: headers,
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error('Silme işlemi başarısız');
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Silme işlemi sırasında bir hata oluştu: ' + error.message);
    });
}

// Event listener'ları bağlama fonksiyonu
function attachEventListeners() {
    // Reddetme butonları için event listener
    document.querySelectorAll('.reject-btn').forEach(button => {
        button.addEventListener('click', function() {
            const formId = this.getAttribute('data-form-id');
            const formType = this.getAttribute('data-form-type');

            if (document.getElementById('formId')) {
                document.getElementById('formId').value = formId;
            }

            if (document.getElementById('formType')) {
                document.getElementById('formType').value = formType;
            }

            const rejectModal = document.getElementById('rejectModal');
            if (rejectModal) {
                const bsModal = new bootstrap.Modal(rejectModal);
                bsModal.show();
            }
        });
    });
}

// Sayfa yüklendiğinde
document.addEventListener('DOMContentLoaded', function() {
    // Event listener'ları bağla
    attachEventListeners();

    // Reddetme modalı için onay butonu event listener'ı
    const confirmRejectBtn = document.getElementById('confirmReject');
    if (confirmRejectBtn) {
        confirmRejectBtn.addEventListener('click', function() {
            const formId = document.getElementById('formId').value;
            const formType = document.getElementById('formType').value;
            const reason = document.getElementById('rejectionReason').value;

            if (!reason) {
                alert('Lütfen red sebebi giriniz');
                return;
            }

            const { token, header } = getCsrfToken();
            const headers = {};
            if (token && header) {
                headers[header] = token;
            }
            headers['Content-Type'] = 'application/x-www-form-urlencoded';

            // Debug bilgisi
            console.log(`Form reddi isteği: /bim-basvuru/admin/${formType}/reject/${formId}`);
            console.log('Gönderilen sebep:', reason);

            const baseUrl = window.location.origin;
            fetch(`${baseUrl}/admin/${formType}/reject/${formId}`, {
                method: 'POST',
                headers: headers,
                body: `reason=${encodeURIComponent(reason)}`
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('İşlem başarısız oldu');
                }
                return response.text();
            })
            .then(() => {
                const rejectModal = bootstrap.Modal.getInstance(document.getElementById('rejectModal'));
                if (rejectModal) {
                    rejectModal.hide();
                }
                location.reload();
            })
            .catch(error => {
                console.error('Hata:', error);
                alert('Başvuru reddedilemedi: ' + error.message);
            });
        });
    }
});

// ============================================
// KULLANICI YÖNETİMİ FONKSİYONLARI
// ============================================

// Kullanıcı görüntüle
function viewUser(userId) {
    const modal = new bootstrap.Modal(document.getElementById('userDetailModal'));
    const content = document.getElementById('userDetailContent');

    content.innerHTML = '<div class="text-center"><div class="spinner-border text-primary"></div></div>';
    modal.show();

    const { token, header } = getCsrfToken();
    const headers = {};
    if (token && header) {
        headers[header] = token;
    }

    const baseUrl = window.location.origin;
    fetch(`${baseUrl}/admin/users/${userId}`, {
        method: 'GET',
        headers: headers,
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Kullanıcı bilgileri alınamadı');
        }
        return response.json();
    })
    .then(data => {
        // Rol isimlerini Türkçeleştir
        const roleNameMap = {
            'ROLE_USER': 'Öğrenci',
            'ROLE_STAFF': 'Personel',
            'ROLE_ADMIN': 'Admin'
        };
        const roles = data.roles.map(r => {
            const roleName = roleNameMap[r.name] || r.name;
            const badgeClass = r.name === 'ROLE_ADMIN' ? 'bg-danger' : (r.name === 'ROLE_STAFF' ? 'bg-warning' : 'bg-info');
            return `<span class="badge ${badgeClass}">${roleName}</span>`;
        }).join(' ');
        const smsVerifiedBadge = data.smsVerified
            ? '<span class="badge bg-success"><i class="fas fa-check"></i> Doğrulandı</span>'
            : '<span class="badge bg-warning"><i class="fas fa-times"></i> Doğrulanmadı</span>';

        content.innerHTML = `
            <table class="table table-bordered">
                <tr><th>ID:</th><td>${data.id}</td></tr>
                <tr><th>Kullanıcı Adı:</th><td>${data.identityNumber}</td></tr>
                <tr><th>TC Kimlik No:</th><td>${data.tcKimlikNo}</td></tr>
                <tr><th>Ad:</th><td>${data.ad || '-'}</td></tr>
                <tr><th>Soyad:</th><td>${data.soyad || '-'}</td></tr>
                <tr><th>Durum:</th><td>${data.active ? '<span class="badge bg-success">Aktif</span>' : '<span class="badge bg-secondary">Pasif</span>'}</td></tr>
                <tr><th>SMS Kodu:</th><td>${data.smsCode || 'Yok'}</td></tr>
                <tr><th>SMS Doğrulama:</th><td>${smsVerifiedBadge}</td></tr>
                <tr><th>Roller:</th><td>${roles}</td></tr>
                <tr><th>Kayıt Tarihi:</th><td>${data.registerDate}</td></tr>
            </table>
        `;
    })
    .catch(error => {
        console.error('Hata:', error);
        content.innerHTML = '<div class="alert alert-danger">Kullanıcı bilgileri yüklenemedi!</div>';
    });
}

// Kullanıcı durumunu değiştir
function toggleUserStatus(userId, currentStatus) {
    const action = currentStatus ? 'pasif yapmak' : 'aktif yapmak';
    if (!confirm(`Kullanıcıyı ${action} istediğinizden emin misiniz?`)) return;

    const { token, header } = getCsrfToken();
    const headers = {};
    if (token && header) {
        headers[header] = token;
    }

    const baseUrl = window.location.origin;
    fetch(`${baseUrl}/admin/users/${userId}/toggle-status`, {
        method: 'POST',
        headers: headers,
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error('İşlem başarısız oldu');
            });
        }
        return response.text();
    })
    .then(message => {
        alert(message);
        location.reload();
    })
    .catch(error => {
        console.error('Hata:', error);
        alert('İşlem başarısız oldu: ' + error.message);
    });
}

// Kullanıcı sil
function deleteUser(userId) {
    if (!confirm('Bu kullanıcıyı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz!')) return;

    const { token, header } = getCsrfToken();
    const headers = {};
    if (token && header) {
        headers[header] = token;
    }

    const baseUrl = window.location.origin;
    fetch(`${baseUrl}/admin/users/${userId}/delete`, {
        method: 'POST',
        headers: headers,
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error('Kullanıcı silinemedi');
            });
        }
        return response.text();
    })
    .then(message => {
        alert(message);
        location.reload();
    })
    .catch(error => {
        console.error('Hata:', error);
        alert('Kullanıcı silinemedi: ' + error.message);
    });
}

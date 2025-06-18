// CSRF token işlemleri için yardımcı fonksiyon
function getCsrfToken() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
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
            document.getElementById('student_fakulte').textContent = data.fakulte || '';
            document.getElementById('student_bolum').textContent = data.bolum || '';
            document.getElementById('student_program').textContent = data.program || '';
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
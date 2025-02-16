// CSRF token işlemleri için yardımcı fonksiyon
function getCsrfToken() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    
    // Debug için token bilgilerini konsola yazdır
    console.log('CSRF Token:', token);
    console.log('CSRF Header:', header);
    
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
        
        const url = `/bim-basvuru/admin/${formType}/activate/${id}`;
        console.log('Request URL:', url);
        console.log('Headers:', headers);
        
        fetch(url, {
            method: 'POST',
            headers: headers,
            credentials: 'include'
        }).then(response => {
            console.log('Response status:', response.status);
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

function activateIpMacForm(id) {
    activateForm('ipmac', id);
}

function activateCloudForm(id) {
    activateForm('cloud', id);
}

function activateVpnForm(id) {
    activateForm('vpn', id);
}

function activateWebAcademicForm(id) {
    activateForm('webacademic', id);
}

function activateServerSetupForm(id) {
    activateForm('serversetup', id);
}

// Kullanıcı detayları modalı için fonksiyonlar
function showUserDetails(element) {
    const username = element.getAttribute('data-username');
    
    fetch(`/bim-basvuru/admin/user-details/${username}`, {
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
        if (data.type === 'STUDENT') {
            document.getElementById('studentDetails').style.display = 'block';
            document.getElementById('staffDetails').style.display = 'none';
            
            // Öğrenci bilgilerini güncelle
            document.getElementById('student_ogrenciNo').textContent = data.ogrenciNo;
            document.getElementById('student_ad').textContent = data.ad;
            document.getElementById('student_soyad').textContent = data.soyad;
            document.getElementById('student_fakulte').textContent = data.fakKod;
            document.getElementById('student_bolum').textContent = data.bolumAd;
            document.getElementById('student_program').textContent = data.programAd;
            document.getElementById('student_sinif').textContent = data.sinif;
        } else {
            document.getElementById('studentDetails').style.display = 'none';
            document.getElementById('staffDetails').style.display = 'block';
            
            // Personel bilgilerini güncelle
            document.getElementById('staff_tcKimlikNo').textContent = data.tcKimlikNo;
            document.getElementById('staff_ad').textContent = data.ad;
            document.getElementById('staff_soyad').textContent = data.soyad;
            document.getElementById('staff_birim').textContent = data.birim;
            document.getElementById('staff_unvan').textContent = data.unvan;
        }
        
        new bootstrap.Modal(document.getElementById('userDetailsModal')).show();
    })
    .catch(error => {
        console.error('Hata:', error);
        alert('Kullanıcı bilgileri alınamadı: ' + error.message);
    });
}

// Reddetme işlemleri için modal ve form yönetimi
$(document).ready(function() {
    const rejectModal = new bootstrap.Modal(document.getElementById('rejectModal'));

    // Red butonu tıklandığında
    $('.reject-btn').click(function() {
        const formId = $(this).data('form-id');
        const formType = $(this).data('form-type');
        
        // Form verilerini modal'a aktar
        $('#formId').val(formId);
        $('#formType').val(formType);
        
        // Modal'ı göster
        rejectModal.show();
    });

    // Reddet butonuna tıklandığında
    $('#confirmReject').click(function() {
        const formId = $('#formId').val();
        const formType = $('#formType').val();
        const reason = $('#rejectionReason').val();

        if (!reason) {
            alert('Lütfen red sebebi giriniz');
            return;
        }

        // CSRF token ve headers
        const { token, header } = getCsrfToken();
        const headers = {};
        if (token && header) {
            headers[header] = token;
        }
        headers['Content-Type'] = 'application/x-www-form-urlencoded';

        // Reddetme isteği gönder
        fetch(`/bim-basvuru/admin/${formType}/reject/${formId}`, {
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
            rejectModal.hide();
            location.reload();
        })
        .catch(error => {
            console.error('Hata:', error);
            alert('Başvuru reddedilemedi: ' + error.message);
        });
    });
}); 
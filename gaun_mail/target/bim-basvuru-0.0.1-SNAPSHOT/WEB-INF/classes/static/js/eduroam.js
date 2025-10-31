/**
 * Eduroam form related JavaScript functionality
 */
document.addEventListener('DOMContentLoaded', function () {
    const eduroamForm = document.getElementById('eduroamForm');
    
    // Only initialize if the form exists on the page
    if (eduroamForm) {
        const modal = new bootstrap.Modal(document.getElementById('eduroamConfirmationModal'));
        const existingModal = new bootstrap.Modal(document.getElementById('existingEduroamModal'));
        const confirmCheckbox = document.getElementById('eduroamConfirmCheckbox');
        const submitButton = document.getElementById('eduroamSubmitButton');

        // Popup'ı göster
        window.showConfirmationPopup = async function () {
            // Form validasyonu
            if (!eduroamForm.checkValidity()) {
                eduroamForm.reportValidity();
                return;
            }

            // Şifre kontrolü
            const password = document.getElementById('eduroamPassword').value;
            const confirmPassword = document.getElementById('eduroamConfirmPassword').value;
            const tcKimlikNo = document.getElementById('tcKimlikNo').value;

            // Şifre boş kontrolü
            if (!password) {
                alert('Şifre boş olamaz!');
                return;
            }

            // Şifre eşleşme kontrolü
            if (password !== confirmPassword) {
                alert('Şifreler eşleşmiyor!');
                return;
            }

            // Şifre regex kontrolü
            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
            if (!passwordRegex.test(password)) {
                alert('Şifre en az 8 karakter olmalı ve en az 1 harf ve 1 rakam içermelidir!');
                return;
            }

            try {
                // Mevcut eduroam hesabı kontrolü
                const response = await fetch(`/bim-basvuru/check-eduroam-exists/${tcKimlikNo}`);
                if (!response.ok) {
                    console.error('Sunucu hatası:', response.status, response.statusText);
                    // Hata durumunda varsayılan davranış
                    modal.show();
                    return;
                }

                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.includes('application/json')) {
                    console.error('Sunucu JSON formatında yanıt vermedi:', contentType);
                    // JSON olmayan yanıt durumunda varsayılan davranış
                    modal.show();
                    return;
                }

                const data = await response.json();
                if (data.exists) {
                    existingModal.show();
                } else {
                    modal.show();
                }
            } catch (error) {
                console.error('Eduroam kontrolü sırasında hata:', error);
                // Herhangi bir hata durumunda varsayılan davranış
                modal.show();
            }
        };

        // Checkbox durumuna göre submit butonunu aktif/pasif yap
        if (confirmCheckbox) {
            confirmCheckbox.addEventListener('change', function () {
                submitButton.disabled = !this.checked;
            });
        }

        // Onayla ve Gönder butonuna tıklandığında
        if (submitButton) {
            submitButton.addEventListener('click', function () {
                modal.hide();
                eduroamForm.submit();
            });
        }
    }
    
    // Şifre görünürlüğünü değiştir
    window.toggleEduroamPassword = function(element) {
        const target = element.getAttribute('data-target');
        const inputField = document.getElementById(target);
        
        if (inputField.type === 'password') {
            inputField.type = 'text';
            element.innerHTML = '<i class="fas fa-eye-slash"></i>';
        } else {
            inputField.type = 'password';
            element.innerHTML = '<i class="fas fa-eye"></i>';
        }
    };
});

/**
 * Mail form related JavaScript functionality
 */
document.addEventListener('DOMContentLoaded', function () {
    const mailForm = document.getElementById('mailForm');
    
    // Only initialize if the form exists on the page
    if (mailForm) {
        const mailModal = new bootstrap.Modal(document.getElementById('mailConfirmationModal'));
        const mailExistingModal = new bootstrap.Modal(document.getElementById('mailExistingModal'));
        const mailConfirmCheckbox = document.getElementById('mailConfirmCheckbox');
        const mailSubmitButton = document.getElementById('mailSubmitButton');

        // Popup'ı göster
        window.showMailConfirmationPopup = async function () {
            if (!mailForm.checkValidity()) {
                mailForm.reportValidity();
                return;
            }

            const username = document.getElementById('mailUsername').value;
            const email = document.getElementById('mailEmailInput').value;

            // Email kontrolü
            if (!email) {
                alert('Mail adresi boş olamaz!');
                return;
            }

            try {
                const response = await fetch(`/bim-basvuru/check-mail-exists/${username}`);
                if (!response.ok) {
                    console.error('Sunucu hatası:', response.status, response.statusText);
                    // Hata durumunda varsayılan davranış - kullanıcıya form göster
                    mailModal.show();
                    return;
                }
                
                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.includes('application/json')) {
                    console.error('Sunucu JSON formatında yanıt vermedi:', contentType);
                    // JSON olmayan yanıt durumunda varsayılan davranış
                    mailModal.show();
                    return;
                }
                
                const data = await response.json();

                if (data.exists) {
                    document.getElementById('existingMailAddress').textContent = data.email + '@gantep.edu.tr';
                    mailExistingModal.show();
                } else {
                    mailModal.show();
                }
            } catch (error) {
                console.error('Mail kontrolü sırasında hata:', error);
                // Herhangi bir hata durumunda varsayılan davranış
                mailModal.show();
            }
        };

        // Checkbox durumuna göre submit butonunu aktif/pasif yap
        if (mailConfirmCheckbox) {
            mailConfirmCheckbox.addEventListener('change', function () {
                mailSubmitButton.disabled = !this.checked;
            });
        }

        // Onayla ve Gönder butonuna tıklandığında
        if (mailSubmitButton) {
            mailSubmitButton.addEventListener('click', function () {
                mailModal.hide();
                mailForm.submit();
            });
        }
    }
});

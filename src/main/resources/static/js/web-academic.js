document.addEventListener('DOMContentLoaded', function() {
    // Form validasyonu
    const forms = document.querySelectorAll('.needs-validation');
    
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Domain adı kontrolü
    const domainInput = document.getElementById('domainName');
    if (domainInput) {
        domainInput.addEventListener('blur', function() {
            const value = this.value.trim();
            if (value && !value.endsWith('.gantep.edu.tr')) {
                this.value = value + '.gantep.edu.tr';
            }
        });
    }

    // Kullanıcı adı formatlaması
    const ftpInput = document.getElementById('ftpUsername');
    const mysqlInput = document.getElementById('mysqlUsername');

    if (ftpInput) {
        ftpInput.addEventListener('input', function() {
            this.value = this.value.toLowerCase().replace(/[^a-z0-9]/g, '');
        });
    }

    if (mysqlInput) {
        mysqlInput.addEventListener('input', function() {
            this.value = this.value.toLowerCase().replace(/[^a-z0-9]/g, '');
        });
    }
}); 
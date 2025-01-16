function activateWirelessForm(id) {
    if (confirm('Bu başvuruyu onaylamak istediğinizden emin misiniz?')) {
        const csrfToken = document.querySelector("meta[name='_csrf']");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']");
        
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // CSRF token varsa ekle
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.content] = csrfToken.content;
        }
        
        fetch(`/wireless/activate/${id}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                alert('Başvuru başarıyla onaylandı');
                location.reload();
            } else {
                alert('Başvuru onaylanırken bir hata oluştu');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Başvuru onaylanırken bir hata oluştu');
        });
    }
}

function activateIpMacForm(id) {
    if (confirm('Bu IP-MAC başvurusunu onaylamak istediğinizden emin misiniz?')) {
        const csrfToken = document.querySelector("meta[name='_csrf']");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']");
        
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // CSRF token varsa ekle
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.content] = csrfToken.content;
        }
        
        fetch(`/ip-mac/activate/${id}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                alert('Başvuru başarıyla onaylandı');
                location.reload();
            } else {
                alert('Başvuru onaylanırken bir hata oluştu');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Başvuru onaylanırken bir hata oluştu');
        });
    }
}

function activateCloudForm(id) {
    if (confirm('Bu GAUN Bulut başvurusunu onaylamak istediğinizden emin misiniz?')) {
        const csrfToken = document.querySelector("meta[name='_csrf']");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']");
        
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // CSRF token varsa ekle
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.content] = csrfToken.content;
        }
        
        fetch(`/cloud/activate/${id}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                alert('Başvuru başarıyla onaylandı');
                location.reload();
            } else {
                alert('Başvuru onaylanırken bir hata oluştu');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Başvuru onaylanırken bir hata oluştu');
        });
    }
}

function activateVpnForm(id) {
    if (confirm('Bu VPN başvurusunu onaylamak istediğinizden emin misiniz?')) {
        const csrfToken = document.querySelector("meta[name='_csrf']");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']");
        
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // CSRF token varsa ekle
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.content] = csrfToken.content;
        }
        
        fetch(`/vpn/activate/${id}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                alert('Başvuru başarıyla onaylandı');
                location.reload();
            } else {
                alert('Başvuru onaylanırken bir hata oluştu');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Başvuru onaylanırken bir hata oluştu');
        });
    }
} 
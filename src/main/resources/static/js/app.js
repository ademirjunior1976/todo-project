document.addEventListener('DOMContentLoaded', function () {
    // Auto-hide alerts após 4 segundos
    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity .4s';
            alert.style.opacity = '0';
            setTimeout(function () { alert.remove(); }, 400);
        }, 4000);
    });

    // Floating labels — date inputs e selects não suportam :placeholder-shown
    document.querySelectorAll('.form-group--float .form-control').forEach(function (el) {
        if (el.type === 'date' || el.tagName === 'SELECT') {
            function sync() {
                el.closest('.form-group--float').classList.toggle('is-filled', el.value !== '');
            }
            sync();
            el.addEventListener('change', sync);
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    // Auto-hide alerts após 4 segundos
    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity .4s';
            alert.style.opacity = '0';
            setTimeout(function () { alert.remove(); }, 400);
        }, 4000);
    });

    // Floating labels — todos os inputs (date/select/password não suportam :placeholder-shown)
    document.querySelectorAll('.form-group--float .form-control').forEach(function (el) {
        function sync() {
            el.closest('.form-group--float').classList.toggle('is-filled', el.value !== '');
        }
        sync();
        el.addEventListener('input', sync);
        el.addEventListener('change', sync);
    });

    // Mostrar/ocultar senha
    document.querySelectorAll('.pwd-toggle').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var input = btn.closest('.pwd-wrapper').querySelector('.form-control');
            var visible = input.type === 'text';
            input.type = visible ? 'password' : 'text';
            btn.querySelector('.icon-eye').style.display     = visible ? '' : 'none';
            btn.querySelector('.icon-eye-off').style.display = visible ? 'none' : '';
        });
    });
});

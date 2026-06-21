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

    // Modal "Sobre a Bilca"
    (function () {
        var overlay = document.getElementById('bilcaAboutModal');
        if (!overlay) return;
        function openModal()  { overlay.classList.add('is-open'); }
        function closeModal() { overlay.classList.remove('is-open'); }
        document.querySelectorAll('.bilca-about').forEach(function (el) {
            el.style.cursor = 'pointer';
            el.addEventListener('click', function (e) { e.preventDefault(); openModal(); });
        });
        overlay.addEventListener('click', function (e) { if (e.target === overlay) closeModal(); });
        var closeBtn = overlay.querySelector('.modal-about__close');
        if (closeBtn) closeBtn.addEventListener('click', closeModal);
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeModal(); });
    })();

    // Modal PDF — fecha ao clicar fora
    var modalPdf = document.getElementById('modalPdf');
    if (modalPdf) {
        modalPdf.addEventListener('click', function (e) {
            if (e.target === modalPdf) modalPdf.classList.remove('is-open');
        });
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') modalPdf.classList.remove('is-open');
        });
        // Floating labels dos campos do modal
        modalPdf.querySelectorAll('.form-group--float .form-control').forEach(function (el) {
            function sync() { el.closest('.form-group--float').classList.toggle('is-filled', el.value !== ''); }
            sync();
            el.addEventListener('input', sync);
            el.addEventListener('change', sync);
        });
    }

    // Validações do formulário de tarefas
    (function () {
        var dataInicio  = document.getElementById('dataInicio');
        var dataTermino = document.getElementById('dataTermino');
        if (!dataInicio) return;

        var hoje = new Date().toISOString().split('T')[0];

        // Sugerir data atual quando for nova tarefa (campo vazio)
        if (!dataInicio.value) {
            dataInicio.value = hoje;
            dataInicio.dispatchEvent(new Event('change'));
        }

        // Inicializar min do dataTermino com o valor atual do dataInicio
        if (dataTermino && dataInicio.value) {
            dataTermino.min = dataInicio.value;
        }

        dataInicio.addEventListener('change', function () {
            var valor = this.value;

            if (valor < hoje) {
                var confirmar = confirm(
                    'A data de início é anterior ao dia de hoje.\n\nDeseja inserir uma data retroativa?'
                );
                if (!confirmar) {
                    this.value = hoje;
                }
            }

            // Atualizar restrição do dataTermino
            if (dataTermino) {
                dataTermino.min = this.value;
                if (dataTermino.value && dataTermino.value < this.value) {
                    dataTermino.value = '';
                    dataTermino.dispatchEvent(new Event('change'));
                }
            }

            this.dispatchEvent(new Event('input'));
        });

        if (dataTermino) {
            dataTermino.addEventListener('change', function () {
                if (dataInicio.value && this.value && this.value < dataInicio.value) {
                    alert('A data de término não pode ser anterior à data de início.');
                    this.value = '';
                    this.dispatchEvent(new Event('input'));
                }
            });
        }

        // Barreira no submit (segurança extra caso o usuário edite via DevTools)
        var form = dataInicio.closest('form');
        if (form) {
            form.addEventListener('submit', function (e) {
                if (dataTermino && dataInicio.value && dataTermino.value &&
                    dataTermino.value < dataInicio.value) {
                    e.preventDefault();
                    alert('A data de término não pode ser anterior à data de início.');
                    dataTermino.focus();
                }
            });
        }
    })();
});

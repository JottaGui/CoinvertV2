    function limparErro(campo) {
        const fieldBox = campo.closest(".field-box");
        const erro = fieldBox.querySelector(".field-error");

        fieldBox.classList.remove("invalid");

        if (erro) {
            erro.textContent = "";
        }
    }

    function aplicarErro(campo, mensagem) {
        const fieldBox = campo.closest(".field-box");
        const erro = fieldBox.querySelector(".field-error");

        fieldBox.classList.add("invalid");
        fieldBox.classList.remove("valid");

        if (erro) {
            erro.textContent = mensagem;
        }
    }

    function aplicarValido(campo) {
        const fieldBox = campo.closest(".field-box");

        fieldBox.classList.remove("invalid");
        fieldBox.classList.add("valid");
    }

    function validarEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
        return regex.test(email);
    }

    function alternarSenha() {
        const senha = document.getElementById("senha");
        const icone = document.getElementById("iconeSenha");

        if (!senha || !icone) return;

        if (senha.type === "password") {
            senha.type = "text";
            icone.classList.remove("bi-eye");
            icone.classList.add("bi-eye-slash");
        } else {
            senha.type = "password";
            icone.classList.remove("bi-eye-slash");
            icone.classList.add("bi-eye");
        }
    }

    function fecharPopup(id) {
        const popup = document.getElementById(id);

        if (!popup) return;

        popup.classList.add("saindo");

        setTimeout(function () {
            popup.remove();
        }, 250);
    }

    function mostrarToast(mensagem, tipo) {
        const toastAntigo = document.querySelector(".coinvert-toast.front-toast");

        if (toastAntigo) {
            toastAntigo.remove();
        }

        const isSuccess = tipo === "success";

        const toast = document.createElement("div");
        toast.className = "coinvert-toast front-toast " + (isSuccess ? "coinvert-toast-success" : "coinvert-toast-error");

        toast.innerHTML = `
            <div class="coinvert-toast-icon">
                <i class="bi ${isSuccess ? "bi-check-circle-fill" : "bi-exclamation-triangle-fill"}"></i>
            </div>

            <div class="coinvert-toast-content">
                <strong>${isSuccess ? "Tudo certo" : "Atenção"}</strong>
                <span>${mensagem}</span>
            </div>

            <button type="button" class="coinvert-toast-close">
                <i class="bi bi-x-lg"></i>
            </button>

            <div class="coinvert-toast-progress"></div>
        `;

        document.body.appendChild(toast);

        toast.querySelector(".coinvert-toast-close").addEventListener("click", function () {
            toast.remove();
        });

        setTimeout(function () {
            if (toast) {
                toast.remove();
            }
        }, 5000);
    }

    document.addEventListener("DOMContentLoaded", function () {
        const form = document.getElementById("loginForm");
        const email = document.getElementById("email");
        const senha = document.getElementById("senha");

        const popupErroBackend = document.getElementById("popupErroBackend");
        const popupErroParam = document.getElementById("popupErroParam");
        const popupSucessoBackend = document.getElementById("popupSucessoBackend");

        if (popupErroBackend) {
            setTimeout(function () {
                fecharPopup("popupErroBackend");
            }, 5000);
        }

        if (popupErroParam) {
            setTimeout(function () {
                fecharPopup("popupErroParam");
            }, 5000);
        }

        if (popupSucessoBackend) {
            setTimeout(function () {
                fecharPopup("popupSucessoBackend");
            }, 5000);
        }

        [email, senha].forEach(function (campo) {
            if (campo) {
                campo.addEventListener("input", function () {
                    limparErro(campo);
                });
            }
        });

        if (form) {
            form.addEventListener("submit", function (event) {
                let formularioValido = true;

                if (!validarEmail(email.value.trim())) {
                    aplicarErro(email, "Informe um e-mail válido.");
                    formularioValido = false;
                } else {
                    aplicarValido(email);
                }

                if (!senha.value.trim()) {
                    aplicarErro(senha, "Informe sua senha.");
                    formularioValido = false;
                } else {
                    aplicarValido(senha);
                }

                if (!formularioValido) {
                    event.preventDefault();
                    mostrarToast("Verifique os campos antes de continuar.", "error");
                }
            });
        }
    });
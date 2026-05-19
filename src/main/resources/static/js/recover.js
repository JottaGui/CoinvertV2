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
        const isInfo = tipo === "info";

        let classe = "coinvert-toast-error";
        let icone = "bi-exclamation-triangle-fill";
        let titulo = "Atenção";

        if (isSuccess) {
            classe = "coinvert-toast-success";
            icone = "bi-check-circle-fill";
            titulo = "Tudo certo";
        }

        if (isInfo) {
            classe = "coinvert-toast-info";
            icone = "bi-info-circle-fill";
            titulo = "Informação";
        }

        const toast = document.createElement("div");
        toast.className = "coinvert-toast front-toast " + classe;

        toast.innerHTML = `
            <div class="coinvert-toast-icon">
                <i class="bi ${icone}"></i>
            </div>

            <div class="coinvert-toast-content">
                <strong>${titulo}</strong>
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
        const form = document.getElementById("recuperarSenhaForm");
        const email = document.getElementById("email");

        const popupErroBackend = document.getElementById("popupErroBackend");
        const popupMensagemBackend = document.getElementById("popupMensagemBackend");
        const popupSucessoBackend = document.getElementById("popupSucessoBackend");

        if (popupErroBackend) {
            setTimeout(function () {
                fecharPopup("popupErroBackend");
            }, 5000);
        }

        if (popupMensagemBackend) {
            setTimeout(function () {
                fecharPopup("popupMensagemBackend");
            }, 5000);
        }

        if (popupSucessoBackend) {
            setTimeout(function () {
                fecharPopup("popupSucessoBackend");
            }, 5000);
        }

        if (email) {
            email.addEventListener("input", function () {
                limparErro(email);
            });
        }

        if (form) {
            form.addEventListener("submit", function (event) {
                let formularioValido = true;

                if (!validarEmail(email.value.trim())) {
                    aplicarErro(email, "Informe um e-mail válido.");
                    formularioValido = false;
                } else {
                    aplicarValido(email);
                }

                if (!formularioValido) {
                    event.preventDefault();
                    mostrarToast("Informe um e-mail válido para continuar.", "error");
                }
            });
        }
    });
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

    function alternarSenha(inputId, iconId) {
        const input = document.getElementById(inputId);
        const icone = document.getElementById(iconId);

        if (!input || !icone) return;

        if (input.type === "password") {
            input.type = "text";
            icone.classList.remove("bi-eye");
            icone.classList.add("bi-eye-slash");
        } else {
            input.type = "password";
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
        const form = document.getElementById("perfilForm");
        const nome = document.getElementById("nome");
        const email = document.getElementById("email");
        const senha = document.getElementById("senha");
        const confirmarSenha = document.getElementById("confirmarSenha");

        const popupErroBackend = document.getElementById("popupErroBackend");
        const popupSucessoBackend = document.getElementById("popupSucessoBackend");

        if (popupErroBackend) {
            setTimeout(function () {
                fecharPopup("popupErroBackend");
            }, 5000);
        }

        if (popupSucessoBackend) {
            setTimeout(function () {
                fecharPopup("popupSucessoBackend");
            }, 5000);
        }

        [nome, email, senha, confirmarSenha].forEach(function (campo) {
            if (campo) {
                campo.addEventListener("input", function () {
                    limparErro(campo);
                });
            }
        });

        if (form) {
            form.addEventListener("submit", function (event) {
                let formularioValido = true;

                const nomeValor = nome.value.trim();
                const emailValor = email.value.trim();
                const senhaValor = senha.value.trim();
                const confirmarSenhaValor = confirmarSenha.value.trim();

                if (!nomeValor || nomeValor.length < 3) {
                    aplicarErro(nome, "Informe um nome com pelo menos 3 caracteres.");
                    formularioValido = false;
                } else {
                    aplicarValido(nome);
                }

                if (!validarEmail(emailValor)) {
                    aplicarErro(email, "Informe um e-mail válido.");
                    formularioValido = false;
                } else {
                    aplicarValido(email);
                }

                if (senhaValor || confirmarSenhaValor) {
                    if (senhaValor.length < 6) {
                        aplicarErro(senha, "A nova senha deve ter pelo menos 6 caracteres.");
                        formularioValido = false;
                    } else {
                        aplicarValido(senha);
                    }

                    if (senhaValor !== confirmarSenhaValor) {
                        aplicarErro(confirmarSenha, "As senhas não conferem.");
                        formularioValido = false;
                    } else if (confirmarSenhaValor.length >= 6) {
                        aplicarValido(confirmarSenha);
                    }
                }

                if (!formularioValido) {
                    event.preventDefault();
                    mostrarToast("Verifique os campos destacados antes de salvar.", "error");
                }
            });
        }
    });
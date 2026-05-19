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

function aplicarMascaraCPF(valor) {
    let cpf = valor.replace(/\D/g, "");

    cpf = cpf.substring(0, 11);

    cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
    cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
    cpf = cpf.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

    return cpf;
}

function validarCPF(cpf) {
    cpf = cpf.replace(/\D/g, "");

    if (cpf.length !== 11) {
        return false;
    }

    if (/^(\d)\1{10}$/.test(cpf)) {
        return false;
    }

    let soma = 0;
    let resto = 0;

    for (let i = 1; i <= 9; i++) {
        soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
    }

    resto = (soma * 10) % 11;

    if (resto === 10 || resto === 11) {
        resto = 0;
    }

    if (resto !== parseInt(cpf.substring(9, 10))) {
        return false;
    }

    soma = 0;

    for (let i = 1; i <= 10; i++) {
        soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
    }

    resto = (soma * 10) % 11;

    if (resto === 10 || resto === 11) {
        resto = 0;
    }

    return resto === parseInt(cpf.substring(10, 11));
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

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("cadastroForm");
    const nome = document.getElementById("nome");
    const cpf = document.getElementById("cpf");
    const email = document.getElementById("email");
    const senha = document.getElementById("senha");

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

    if (cpf) {
        cpf.addEventListener("input", function () {
            cpf.value = aplicarMascaraCPF(cpf.value);
            limparErro(cpf);
        });

        cpf.addEventListener("blur", function () {
            if (cpf.value.trim() !== "" && !validarCPF(cpf.value)) {
                aplicarErro(cpf, "CPF inválido.");
            } else if (cpf.value.trim() !== "") {
                aplicarValido(cpf);
            }
        });
    }

    [nome, email, senha].forEach(function (campo) {
        if (campo) {
            campo.addEventListener("input", function () {
                limparErro(campo);
            });
        }
    });

    if (form) {
        form.addEventListener("submit", function (event) {
            let formularioValido = true;

            if (!nome.value.trim() || nome.value.trim().length < 3) {
                aplicarErro(nome, "Informe um nome com pelo menos 3 caracteres.");
                formularioValido = false;
            } else {
                aplicarValido(nome);
            }

            if (!validarCPF(cpf.value)) {
                aplicarErro(cpf, "Informe um CPF válido.");
                formularioValido = false;
            } else {
                aplicarValido(cpf);
            }

            if (!validarEmail(email.value.trim())) {
                aplicarErro(email, "Informe um e-mail válido.");
                formularioValido = false;
            } else {
                aplicarValido(email);
            }

            if (!senha.value.trim() || senha.value.length < 6) {
                aplicarErro(senha, "A senha deve ter pelo menos 6 caracteres.");
                formularioValido = false;
            } else {
                aplicarValido(senha);
            }

            if (!formularioValido) {
                event.preventDefault();
                mostrarToast("Verifique os campos destacados antes de continuar.", "error");
            }
        });
    }
});
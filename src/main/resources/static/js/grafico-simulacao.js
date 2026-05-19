let grafico = null;
let dadosGraficoAtual = null;

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor || 0);
}

function extrairNumero(texto) {
    if (!texto || texto.includes('Não disponível')) {
        return 0;
    }

    return parseFloat(
        texto
            .replace(/[^\d,.-]/g, '')
            .replace(/\./g, '')
            .replace(',', '.')
    ) || 0;
}

function painelSimulacaoAberto() {
    const collapse = document.getElementById('collapseWidthExample');
    return collapse && collapse.classList.contains('show');
}

function destruirGrafico() {
    if (grafico) {
        grafico.destroy();
        grafico = null;
    }
}

function gerarSerieTrade(valorInicial, valorFinal, pontos = 14) {
    const serie = [];

    const inicio = Number(valorInicial) || 0;
    const fim = Number(valorFinal) || 0;

    for (let i = 0; i < pontos; i++) {
        const progresso = i / (pontos - 1);
        const base = inicio + ((fim - inicio) * progresso);

        const oscilacao =
            Math.sin(i * 1.55) * (base * 0.018) +
            Math.cos(i * 0.85) * (base * 0.010);

        if (i === 0) {
            serie.push(inicio);
        } else if (i === pontos - 1) {
            serie.push(fim);
        } else {
            serie.push(Math.max(base + oscilacao, 0));
        }
    }

    return serie;
}

function gerarLinhaTendencia(serieBase) {
    return serieBase.map(function (valor, index) {
        const deslocamento = valor * 0.035;
        const suavizacao = Math.sin(index * 0.9) * (valor * 0.008);

        return Math.max(valor - deslocamento + suavizacao, 0);
    });
}

function criarGrafico(cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje) {
    const canvas = document.getElementById('graficoCotacao');

    if (!canvas) return;

    destruirGrafico();

    canvas.style.pointerEvents = 'none';

    const ctx = canvas.getContext('2d');

    const labels = [
        'D-13', 'D-12', 'D-11', 'D-10', 'D-9', 'D-8', 'D-7',
        'D-6', 'D-5', 'D-4', 'D-3', 'D-2', 'D-1', 'Hoje'
    ];

    const serieCotacao = gerarSerieTrade(cotacaoPassada, cotacaoAtual, labels.length);
    const serieTendencia = gerarLinhaTendencia(serieCotacao);

    const gradientCotacao = ctx.createLinearGradient(0, 0, 0, 360);
    gradientCotacao.addColorStop(0, 'rgba(49, 208, 127, 0.30)');
    gradientCotacao.addColorStop(1, 'rgba(49, 208, 127, 0.02)');

    grafico = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Cotação simulada',
                    data: serieCotacao,
                    borderColor: 'rgba(49, 208, 127, 1)',
                    backgroundColor: gradientCotacao,
                    borderWidth: 3,
                    pointRadius: 3,
                    pointHoverRadius: 0,
                    pointBackgroundColor: 'rgba(49, 208, 127, 1)',
                    pointBorderColor: 'rgba(5, 10, 17, 1)',
                    pointBorderWidth: 2,
                    tension: 0.38,
                    fill: true
                },
                {
                    label: 'Linha de tendência',
                    data: serieTendencia,
                    borderColor: 'rgba(13, 110, 253, 1)',
                    backgroundColor: 'transparent',
                    borderWidth: 2,
                    borderDash: [8, 6],
                    pointRadius: 0,
                    pointHoverRadius: 0,
                    tension: 0.38,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            events: [],

            animation: {
                duration: 850,
                easing: 'easeOutQuart'
            },

            plugins: {
                legend: {
                    display: true,
                    labels: {
                        color: '#dbe4f0',
                        font: {
                            size: 12,
                            weight: '700'
                        },
                        usePointStyle: true,
                        pointStyle: 'line'
                    }
                },
                tooltip: {
                    enabled: false
                }
            },

            scales: {
                x: {
                    ticks: {
                        color: '#94a3b8',
                        font: {
                            size: 11,
                            weight: '600'
                        }
                    },
                    grid: {
                        color: 'rgba(148, 163, 184, 0.08)',
                        borderDash: [4, 4]
                    },
                    border: {
                        color: 'rgba(148, 163, 184, 0.12)'
                    }
                },

                y: {
                    beginAtZero: false,
                    ticks: {
                        color: 'rgba(49, 208, 127, 1)',
                        callback: function(value) {
                            return 'R$ ' + Number(value).toFixed(2).replace('.', ',');
                        }
                    },
                    grid: {
                        color: 'rgba(148, 163, 184, 0.10)'
                    },
                    border: {
                        color: 'rgba(49, 208, 127, 0.45)'
                    }
                }
            }
        }
    });
}

function atualizarDadosGrafico() {
    const cotacaoPassadaText = document.getElementById('cotacaoPassada')?.innerText || '';
    const cotacaoAtualText = document.getElementById('cotacaoAtual')?.innerText || '';
    const valorConvertidoNoPassadoText = document.getElementById('valorConvertidoNoPassado')?.innerText || '';
    const valorHojeText = document.getElementById('valorHoje')?.innerText || '';

    dadosGraficoAtual = [
        extrairNumero(cotacaoPassadaText),
        extrairNumero(cotacaoAtualText),
        extrairNumero(valorConvertidoNoPassadoText),
        extrairNumero(valorHojeText)
    ];

    atualizarResumoTrade(...dadosGraficoAtual);
}

function atualizarResumoTrade(cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje) {
    const variacaoTexto = document.getElementById('tradeVariacaoTexto');
    const badge = document.getElementById('tradeResultadoBadge');
    const descricao = document.getElementById('tradeResultadoDescricao');

    if (!cotacaoPassada || !cotacaoAtual) {
        return;
    }

    const percentual = ((cotacaoAtual - cotacaoPassada) / cotacaoPassada) * 100;
    const percentualFormatado = Math.abs(percentual).toFixed(2).replace('.', ',');

    if (variacaoTexto) {
        variacaoTexto.textContent =
            percentual >= 0
                ? `▲ ${percentualFormatado}% desde a data selecionada`
                : `▼ ${percentualFormatado}% desde a data selecionada`;
    }

    if (badge) {
        badge.textContent = percentual >= 0
            ? `▲ ${percentualFormatado}%`
            : `▼ ${percentualFormatado}%`;

        badge.classList.remove('trade-badge-positive', 'trade-badge-negative');
        badge.classList.add(percentual >= 0 ? 'trade-badge-positive' : 'trade-badge-negative');
    }

    if (descricao && valorHoje && valorConvertidoNoPassado) {
        const diferenca = valorHoje - valorConvertidoNoPassado;
        const textoDiferenca = formatarMoeda(Math.abs(diferenca));

        descricao.textContent =
            diferenca >= 0
                ? `Se você tivesse convertido na data selecionada, seu valor hoje seria maior em ${textoDiferenca}.`
                : `Se você tivesse convertido na data selecionada, seu valor hoje seria menor em ${textoDiferenca}.`;
    }
}

function formularioSimulacaoValido() {
    const valor = document.getElementById('valorSimulacao')?.value;
    const origem = document.getElementById('origemSimulacao')?.value;
    const destino = document.getElementById('destinoSimulacao')?.value;
    const data = document.getElementById('dataSimulacao')?.value;

    return Boolean(valor && origem && destino && data);
}

function iniciarCollapseSimulacao() {
    const botaoSimulacao = document.getElementById('btnSimulacao');
    const textoBotao = document.getElementById('textoBotaoSimulacao');
    const collapseElement = document.getElementById('collapseWidthExample');

    if (!botaoSimulacao || !collapseElement) return;

    const collapseInstance = bootstrap.Collapse.getOrCreateInstance(collapseElement, {
        toggle: false
    });

    botaoSimulacao.addEventListener('click', function (event) {
        event.preventDefault();
        event.stopPropagation();

        collapseInstance.toggle();
    });

    collapseElement.addEventListener('shown.bs.collapse', function () {
        botaoSimulacao.setAttribute('aria-expanded', 'true');

        if (textoBotao) {
            textoBotao.textContent = 'Ocultar simulação';
        }
    });

    collapseElement.addEventListener('hide.bs.collapse', function () {
        destruirGrafico();
    });

    collapseElement.addEventListener('hidden.bs.collapse', function () {
        botaoSimulacao.setAttribute('aria-expanded', 'false');

        if (textoBotao) {
            textoBotao.textContent = 'Exibir simulação';
        }
    });
}

function iniciarDropdownUsuario() {
    const userDropdown = document.getElementById('userDropdown');

    if (!userDropdown) return;

    bootstrap.Dropdown.getOrCreateInstance(userDropdown);
}

document.addEventListener('DOMContentLoaded', function () {
    iniciarCollapseSimulacao();
    iniciarDropdownUsuario();
});

document.body.addEventListener('htmx:beforeRequest', function (event) {
    const elemento = event.detail.elt;
    const form = elemento?.id === 'formSimulacao'
        ? elemento
        : elemento?.closest?.('#formSimulacao');

    if (!form) {
        return;
    }

    if (!formularioSimulacaoValido()) {
        event.preventDefault();
        alert('Preencha valor, moeda de origem, moeda de destino e data antes de simular.');
    }
});

document.body.addEventListener('htmx:afterSwap', function (event) {
    if (event.detail.target.id === 'simulacao') {
        atualizarDadosGrafico();

        if (painelSimulacaoAberto() && dadosGraficoAtual) {
            setTimeout(function () {
                criarGrafico(...dadosGraficoAtual);
            }, 150);
        }

        iniciarDropdownUsuario();
    }
});
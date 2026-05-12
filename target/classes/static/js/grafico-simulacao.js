let grafico;

function formatarMoeda(valor) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor || 0);
}

function criarGrafico(cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje) {
  const canvas = document.getElementById('graficoCotacao');

  if (!canvas) {
    console.warn('Canvas #graficoCotacao não encontrado');
    return;
  }

  const ctx = canvas.getContext('2d');

  if (!ctx) {
    console.warn('Contexto 2D do canvas não encontrado');
    return;
  }

  if (grafico) {
    grafico.destroy();
  }

  grafico = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Data selecionada', 'Cotação atual'],
      datasets: [
        {
          label: 'Cotação',
          data: [cotacaoPassada, cotacaoAtual],
          borderColor: 'rgba(49, 208, 127, 1)',
          backgroundColor: 'rgba(49, 208, 127, 0.18)',
          borderWidth: 3,
          tension: 0.35,
          fill: true,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: 'rgba(49, 208, 127, 1)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          yAxisID: 'yCotacao'
        },
        {
          label: 'Valor convertido',
          data: [valorConvertidoNoPassado, valorHoje],
          borderColor: 'rgba(34, 211, 238, 1)',
          backgroundColor: 'rgba(34, 211, 238, 0.08)',
          borderWidth: 3,
          tension: 0.35,
          fill: false,
          borderDash: [6, 4],
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: 'rgba(34, 211, 238, 1)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          yAxisID: 'yValor'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 700,
        easing: 'easeOutQuart'
      },
      interaction: {
        mode: 'index',
        intersect: false
      },
      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            color: '#dbe4f0',
            font: {
              size: 12,
              weight: '600'
            },
            usePointStyle: true,
            pointStyle: 'circle',
            padding: 18
          }
        },
        tooltip: {
          enabled: true,
          backgroundColor: 'rgba(15, 23, 34, 0.96)',
          titleColor: '#ffffff',
          bodyColor: '#dbe4f0',
          borderColor: 'rgba(148, 163, 184, 0.20)',
          borderWidth: 1,
          padding: 12,
          displayColors: true,
          callbacks: {
            label: function(context) {
              return `${context.dataset.label}: ${formatarMoeda(context.raw)}`;
            }
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: '#cbd5e1',
            font: {
              size: 12,
              weight: '600'
            }
          },
          grid: {
            color: 'rgba(148, 163, 184, 0.08)'
          },
          border: {
            color: 'rgba(148, 163, 184, 0.12)'
          }
        },
        yCotacao: {
          type: 'linear',
          position: 'left',
          beginAtZero: false,
          ticks: {
            color: '#cbd5e1',
            callback: function(value) {
              return formatarMoeda(value);
            }
          },
          grid: {
            color: 'rgba(148, 163, 184, 0.10)',
            drawBorder: false
          },
          border: {
            display: false
          }
        },
        yValor: {
          type: 'linear',
          position: 'right',
          beginAtZero: false,
          ticks: {
            color: '#cbd5e1',
            callback: function(value) {
              return formatarMoeda(value);
            }
          },
          grid: {
            drawOnChartArea: false
          },
          border: {
            display: false
          }
        }
      }
    }
  });
}

function extrairNumero(texto) {
  if (!texto || texto.includes('Não disponível')) {
    return 0;
  }

  return parseFloat(
    texto
      .replace(/[^\d,.-]/g, '')
      .replace(',', '.')
  ) || 0;
}

document.body.addEventListener('htmx:afterSwap', (event) => {
  if (event.detail.target.id === 'simulacao') {
    const cotacaoPassadaText =
      document.getElementById('cotacaoPassada')?.innerText || '';

    const cotacaoAtualText =
      document.getElementById('cotacaoAtual')?.innerText || '';

    const valorConvertidoNoPassadoText =
      document.getElementById('valorConvertidoNoPassado')?.innerText || '';

    const valorHojeText =
      document.getElementById('valorHoje')?.innerText || '';

    criarGrafico(
      extrairNumero(cotacaoPassadaText),
      extrairNumero(cotacaoAtualText),
      extrairNumero(valorConvertidoNoPassadoText),
      extrairNumero(valorHojeText)
    );
  }
});

window.addEventListener('load', () => {
  const canvas = document.getElementById('graficoCotacao');

  if (canvas) {
    criarGrafico(0, 0, 0, 0);
  }
});
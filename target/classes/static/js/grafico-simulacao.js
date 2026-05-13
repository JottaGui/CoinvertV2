let grafico;

function formatarMoeda(valor) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor || 0);
}

function criarGrafico(cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje) {
  const canvas = document.getElementById('graficoCotacao');

  if (!canvas) return;

  const ctx = canvas.getContext('2d');

  if (grafico) {
    grafico.destroy();
  }

  const gradient1 = ctx.createLinearGradient(0, 0, 0, 350);
  gradient1.addColorStop(0, 'rgba(49, 208, 127, 0.95)');
  gradient1.addColorStop(1, 'rgba(49, 208, 127, 0.45)');

  const gradient2 = ctx.createLinearGradient(0, 0, 0, 350);
  gradient2.addColorStop(0, 'rgba(13, 110, 253, 0.95)');
  gradient2.addColorStop(1, 'rgba(13, 110, 253, 0.45)');

  const gradient3 = ctx.createLinearGradient(0, 0, 0, 350);
  gradient3.addColorStop(0, 'rgba(99, 102, 241, 0.95)');
  gradient3.addColorStop(1, 'rgba(99, 102, 241, 0.45)');

  const gradient4 = ctx.createLinearGradient(0, 0, 0, 350);
  gradient4.addColorStop(0, 'rgba(34, 211, 238, 0.95)');
  gradient4.addColorStop(1, 'rgba(34, 211, 238, 0.45)');

  grafico = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: [
        'Cotação passada',
        'Cotação atual',
        'Valor na data',
        'Valor hoje'
      ],
      datasets: [{
        label: 'Valores',
        data: [
          cotacaoPassada,
          cotacaoAtual,
          valorConvertidoNoPassado,
          valorHoje
        ],
        backgroundColor: [gradient1, gradient2, gradient3, gradient4],
        borderColor: [
          'rgba(49, 208, 127, 1)',
          'rgba(13, 110, 253, 1)',
          'rgba(99, 102, 241, 1)',
          'rgba(34, 211, 238, 1)'
        ],
        borderWidth: 1.5,
        borderRadius: 12,
        borderSkipped: false,
        maxBarThickness: 80
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 700,
        easing: 'easeOutQuart'
      },
      plugins: {
        legend: {
          display: true,
          labels: {
            color: '#dbe4f0',
            font: {
              size: 12,
              weight: '600'
            },
            usePointStyle: true,
            pointStyle: 'rectRounded'
          }
        },
        tooltip: {
          backgroundColor: 'rgba(15, 23, 34, 0.96)',
          titleColor: '#ffffff',
          bodyColor: '#dbe4f0',
          borderColor: 'rgba(148, 163, 184, 0.20)',
          borderWidth: 1,
          padding: 12,
          callbacks: {
            label: function(context) {
              return ` ${formatarMoeda(context.raw)}`;
            }
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: '#cbd5e1',
            font: {
              size: 11,
              weight: '600'
            }
          },
          grid: {
            display: false
          },
          border: {
            color: 'rgba(148, 163, 184, 0.12)'
          }
        },
        y: {
          beginAtZero: true,
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
        }
      }
    }
  });
}

function extrairNumero(texto) {
  if (!texto || texto.includes('Não disponível')) return 0;
  return parseFloat(texto.replace(/[^\d,.-]/g, '').replace(',', '.')) || 0;
}

document.body.addEventListener('htmx:afterSwap', (event) => {
  if (event.detail.target.id === 'simulacao') {
    const cotacaoPassadaText = document.getElementById('cotacaoPassada')?.innerText || '';
    const cotacaoAtualText = document.getElementById('cotacaoAtual')?.innerText || '';
    const valorConvertidoNoPassadoText = document.getElementById('valorConvertidoNoPassado')?.innerText || '';
    const valorHojeText = document.getElementById('valorHoje')?.innerText || '';

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
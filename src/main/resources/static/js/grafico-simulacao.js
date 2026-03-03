let grafico;

function criarGrafico(cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje) {
  const ctx = document.getElementById('graficoCotacao').getContext('2d');

  if (grafico) {
    grafico.destroy();
  }

  grafico = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Cotação Passada', 'Cotação Atual', 'Valor no Passado', 'Valor Hoje'],
      datasets: [{
        label: 'Valores',
        data: [cotacaoPassada, cotacaoAtual, valorConvertidoNoPassado, valorHoje],
        backgroundColor: [
          'rgba(54, 162, 235, 0.7)',
          'rgba(255, 206, 86, 0.7)',
          'rgba(75, 192, 192, 0.7)',
          'rgba(153, 102, 255, 0.7)'
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
}

function extrairNumero(texto) {
  if (!texto || texto.includes('Não disponível')) return 0;
  return parseFloat(texto.replace(/[^\d,.-]/g, '').replace(',', '.')) || 0;
}

// Atualiza gráfico após HTMX atualizar a div #simulacaor
document.body.addEventListener('htmx:afterSwap', (event) => {
  if (event.detail.target.id === 'simulacaor') {
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

// Inicializa gráfico vazio no carregamento da página
window.addEventListener('load', () => {
  criarGrafico(0, 0, 0, 0);
});

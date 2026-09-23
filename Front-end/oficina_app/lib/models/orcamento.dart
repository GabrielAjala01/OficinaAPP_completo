class ItemServico {
  final int? idItemServico;
  final int idServico;
  final String? nomeServico;
  final double qtdHoras;
  final double desconto;

  ItemServico({
    this.idItemServico,
    required this.idServico,
    this.nomeServico,
    required this.qtdHoras,
    required this.desconto,
  });

  factory ItemServico.fromJson(Map<String, dynamic> json) {
    return ItemServico(
      idItemServico: json['idItemServico'],
      idServico: json['idServico'],
      nomeServico: json['nomeServico'],
      qtdHoras: (json['qtdHoras'] ?? 0).toDouble(),
      desconto: (json['desconto'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'idServico': idServico,
      'qtdHoras': qtdHoras,
      'desconto': desconto,
    };
  }
}

class Orcamento {
  final int? idOrcamento;
  final int? idFuncionario;
  final String situacao;
  final double valorTotal;
  final int idCliente;
  final String placaVeiculo;
  final List<ItemServico> itens;

  Orcamento({
    this.idOrcamento,
    this.idFuncionario,
    required this.situacao,
    required this.valorTotal,
    required this.idCliente,
    required this.placaVeiculo,
    this.itens = const [],
  });

  factory Orcamento.fromJson(Map<String, dynamic> json) {
    var itensJson = json['itens'] as List<dynamic>?;
    List<ItemServico> itensList = itensJson != null
        ? itensJson.map((i) => ItemServico.fromJson(i as Map<String, dynamic>)).toList()
        : [];

    return Orcamento(
      idOrcamento: json['idOrcamento'],
      idFuncionario: json['idFuncionario'],
      situacao: json['situacao'] ?? 'AGUARDANDO',
      valorTotal: (json['valorTotal'] ?? 0).toDouble(),
      idCliente: json['idCliente'],
      placaVeiculo: json['placaVeiculo'],
      itens: itensList,
    );
  }
}
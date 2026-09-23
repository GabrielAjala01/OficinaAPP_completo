import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../models/orcamento.dart';
import '../services/api_service.dart';
import 'package:printing/printing.dart';

class OrcamentoDetalhesScreen extends StatefulWidget {
  final int orcamentoId;

  const OrcamentoDetalhesScreen({super.key, required this.orcamentoId});

  @override
  State<OrcamentoDetalhesScreen> createState() => _OrcamentoDetalhesScreenState();
}

class _OrcamentoDetalhesScreenState extends State<OrcamentoDetalhesScreen> {
  final ApiService _apiService = ApiService();
  Orcamento? _orcamento;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _carregarOrcamento();
  }

  Future<void> _carregarOrcamento() async {
    setState(() => _isLoading = true);
    try {
      final response = await _apiService.getRequest('/orcamento/${widget.orcamentoId}');
      if (response.statusCode == 200) {
        setState(() {
          _orcamento = Orcamento.fromJson(jsonDecode(response.body));
        });
      } else {
        _mostrarErro('Erro ao carregar orçamento.');
      }
    } catch (e) {
      _mostrarErro('Erro de conexão: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _mudarStatus(String novoStatus) async {
    try {
      final response = await _apiService.putRequest(
        '/orcamento/atualizarSituacao/${widget.orcamentoId}',
        {"situacao": novoStatus},
      );
      if (response.statusCode == 200) {
        _carregarOrcamento();
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Estado atualizado!'), backgroundColor: Colors.green));
      }
    } catch (e) {
      _mostrarErro('Erro ao atualizar estado.');
    }
  }

  Future<void> _removerItem(int idItemServico) async {
    try {
      final response = await _apiService.deleteRequest('/orcamento/${widget.orcamentoId}/itens/$idItemServico');
      if (response.statusCode == 200) {
        _carregarOrcamento();
      } else {
        _mostrarErro('Erro ao remover serviço.');
      }
    } catch (e) {
      _mostrarErro('Erro de conexão: $e');
    }
  }
  Future<void> _abrirPdf() async {
    setState(() => _isLoading = true);

    try {
      final response = await _apiService.getRequest('/orcamento/${widget.orcamentoId}/pdf');

      if (response.statusCode == 200) {
        await Printing.layoutPdf(
          onLayout: (_) async => response.bodyBytes,
          name: 'Orcamento_${widget.orcamentoId}.pdf',
        );
      } else {
        _mostrarErro('Erro ao gerar PDF. O servidor retornou: ${response.statusCode}');
      }
    } catch (e) {
      _mostrarErro('Erro de conexão ao tentar abrir o PDF: $e');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }



  void _mostrarErro(String msg) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg), backgroundColor: Colors.red));
    }
  }

  void _abrirModalAdicionarServico() {
    showDialog(
      context: context,
      builder: (context) => _ModalAdicionarServico(
        orcamentoId: widget.orcamentoId,
        aoSalvar: _carregarOrcamento,
      ),
    );
  }

  Color _obterCorStatus(String situacao) {
    switch (situacao) {
      case 'APROVADO': return Colors.green;
      case 'REPROVADO': return Colors.red;
      case 'CONVERTIDO_OS': return Colors.purple;
      default: return Colors.orange;
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading && _orcamento == null) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }
    if (_orcamento == null) {
      return const Scaffold(body: Center(child: Text('Orçamento não encontrado.')));
    }

    return Scaffold(
      appBar: AppBar(
        title: Text('Orçamento #${_orcamento!.idOrcamento}', style: const TextStyle(color: Colors.white)),
        backgroundColor: Colors.blue,
        iconTheme: const IconThemeData(color: Colors.white),
        actions: [
          IconButton(
            icon: const Icon(Icons.picture_as_pdf),
            tooltip: 'Gerar PDF',
            onPressed: _abrirPdf,
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.more_vert),
            onSelected: _mudarStatus,
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'AGUARDANDO', child: Text('Marcar como Aguardando')),
              const PopupMenuItem(value: 'APROVADO', child: Text('Marcar como Aprovado')),
              const PopupMenuItem(value: 'REPROVADO', child: Text('Marcar como Reprovado')),
              const PopupMenuItem(value: 'CONVERTIDO_OS', child: Text('Converter para O.S.')),
            ],
          )
        ],
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            color: Colors.blue.shade50,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text('Veículo: ${_orcamento!.placaVeiculo}', style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Chip(
                      label: Text(_orcamento!.situacao.replaceAll('_', ' '), style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      backgroundColor: _obterCorStatus(_orcamento!.situacao),
                    ),
                    Text(
                      'Total: R\$ ${_orcamento!.valorTotal.toStringAsFixed(2)}',
                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Colors.blue),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const Divider(height: 1),
          Expanded(
            child: _orcamento!.itens.isEmpty
                ? const Center(child: Text('Nenhum serviço adicionado ainda.'))
                : ListView.builder(
              itemCount: _orcamento!.itens.length,
              itemBuilder: (context, index) {
                final item = _orcamento!.itens[index];
                return ListTile(
                  leading: const CircleAvatar(child: Icon(Icons.build)),
                  title: Text(item.nomeServico ?? 'Serviço ID: ${item.idServico}'),
                  subtitle: Text('Qtd Horas: ${item.qtdHoras} | Desc: R\$ ${item.desconto.toStringAsFixed(2)}'),
                  trailing: IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () => _removerItem(item.idItemServico!),
                  ),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _abrirModalAdicionarServico,
        backgroundColor: Colors.blue,
        icon: const Icon(Icons.add, color: Colors.white),
        label: const Text('Adicionar Serviço', style: TextStyle(color: Colors.white)),
      ),
    );
  }
}


// Modal para pesquisar e adicionar o serviço
class _ModalAdicionarServico extends StatefulWidget {
  final int orcamentoId;
  final VoidCallback aoSalvar;

  const _ModalAdicionarServico({required this.orcamentoId, required this.aoSalvar});

  @override
  State<_ModalAdicionarServico> createState() => _ModalAdicionarServicoState();
}

class _ModalAdicionarServicoState extends State<_ModalAdicionarServico> {
  final ApiService _apiService = ApiService();
  List<dynamic> _servicosDisponiveis = [];
  int? _idServicoSelecionado;

  final TextEditingController _horasController = TextEditingController(text: '1.0');
  final TextEditingController _descontoController = TextEditingController(text: '0.0');

  bool _isLoading = true;
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _carregarServicos();
  }

  Future<void> _carregarServicos() async {
    try {
      final response = await _apiService.getRequest('/servicos');
      if (response.statusCode == 200) {
        setState(() {
          _servicosDisponiveis = jsonDecode(response.body);
        });
      }
    } catch (e) {
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _salvarItem() async {
    if (_idServicoSelecionado == null) return;
    setState(() => _isSaving = true);

    try {
      final body = {
        "idServico": _idServicoSelecionado,
        "qtdHoras": double.tryParse(_horasController.text) ?? 1.0,
        "desconto": double.tryParse(_descontoController.text) ?? 0.0,
      };

      final response = await _apiService.postRequest('/orcamento/${widget.orcamentoId}/itens', body);

      if (response.statusCode == 200 || response.statusCode == 201) {
        widget.aoSalvar();
        if (mounted) Navigator.pop(context); // Fecha o modal
      }
    } catch (e) {
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Adicionar Serviço'),
      content: _isLoading
          ? const SizedBox(height: 50, child: Center(child: CircularProgressIndicator()))
          : SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            DropdownButtonFormField<int>(
              initialValue: _idServicoSelecionado, // Diz ao Flutter qual é o valor atual
              decoration: const InputDecoration(
                  labelText: 'Selecione o Serviço',
                  border: OutlineInputBorder()
              ),
              items: _servicosDisponiveis.isEmpty ? [] : _servicosDisponiveis.map((s) {
                int idDoServico = s['id'] ?? s['idServico'];
                return DropdownMenuItem<int>(
                  value: idDoServico,
                  child: Text('${s['nome']} (R\$ ${s['valor']})'),
                );
              }).toList(),
              onChanged: (val) => setState(() => _idServicoSelecionado = val),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _horasController,
              decoration: const InputDecoration(labelText: 'Qtd. de Horas', border: OutlineInputBorder()),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _descontoController,
              decoration: const InputDecoration(labelText: 'Desconto (R\$)', border: OutlineInputBorder()),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancelar')),
        ElevatedButton(
          onPressed: (_idServicoSelecionado != null && !_isSaving) ? _salvarItem : null,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
          child: _isSaving ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(color: Colors.white)) : const Text('Adicionar', style: TextStyle(color: Colors.white)),
        ),
      ],
    );
  }
}
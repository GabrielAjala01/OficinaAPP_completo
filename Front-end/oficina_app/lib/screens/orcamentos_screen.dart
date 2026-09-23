import 'dart:convert';
import 'package:flutter/material.dart';
import '../models/orcamento.dart';
import '../services/api_service.dart ';
import '../widgets/modal_novo_orcamento.dart';
import 'orcamentos_detalhes_screen.dart';


class OrcamentosScreen extends StatefulWidget {
  const OrcamentosScreen({super.key});

  @override
  State<OrcamentosScreen> createState() => _OrcamentosScreenState();
}

class _OrcamentosScreenState extends State<OrcamentosScreen> {
  final ApiService _apiService = ApiService();
  List<Orcamento> _orcamentos = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _carregarOrcamentos();
  }

  Future<void> _carregarOrcamentos() async {
    setState(() => _isLoading = true);
    try {
      final response = await _apiService.getRequest('/orcamento');
      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        setState(() {
          _orcamentos = data.map((json) => Orcamento.fromJson(json)).toList();
        });
      } else {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Erro ao carregar orçamentos.'), backgroundColor: Colors.red),
          );
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro de conexão: $e'), backgroundColor: Colors.red),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  // Função auxiliar para definir a cor da etiqueta dependendo do status
  Color _obterCorStatus(String situacao) {
    switch (situacao) {
      case 'APROVADO':
        return Colors.green;
      case 'REPROVADO':
        return Colors.red;
      case 'CONVERTIDO_OS':
        return Colors.purple;
      case 'AGUARDANDO':
      default:
        return Colors.orange;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Orçamentos', style: TextStyle(color: Colors.white)),
        backgroundColor: Colors.blue,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _orcamentos.isEmpty
          ? const Center(child: Text('Nenhum orçamento encontrado.', style: TextStyle(fontSize: 16)))
          : ListView.builder(
        padding: const EdgeInsets.all(8),
        itemCount: _orcamentos.length,
        itemBuilder: (context, index) {
          final orcamento = _orcamentos[index];
          return Card(
            elevation: 2,
            margin: const EdgeInsets.symmetric(vertical: 6),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: _obterCorStatus(orcamento.situacao),
                child: const Icon(Icons.request_quote, color: Colors.white),
              ),
              title: Text('Orçamento #${orcamento.idOrcamento}', style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text(
                'Placa: ${orcamento.placaVeiculo}\nTotal: R\$ ${orcamento.valorTotal.toStringAsFixed(2)}',
              ),
              isThreeLine: true,
              trailing: Chip(
                label: Text(
                  orcamento.situacao.replaceAll('_', ' '),
                  style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                ),
                backgroundColor: _obterCorStatus(orcamento.situacao),
              ),
              onTap: () async {
                 final recarregar = await Navigator.push(context, MaterialPageRoute(builder: (_) => OrcamentoDetalhesScreen(orcamentoId: orcamento.idOrcamento!)));
                 if (recarregar == true) _carregarOrcamentos();
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await showDialog(
          context: context,
          builder: (context) => const ModalNovoOrcamento(),
          );
          _carregarOrcamentos();
        },
        backgroundColor: Colors.blue,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
}
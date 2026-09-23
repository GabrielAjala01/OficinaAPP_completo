import 'dart:convert';
import 'package:flutter/material.dart';
import '../models/cliente.dart';
import '../models/veiculo.dart';
import '../services/api_service.dart';
import '../screens/orcamentos_detalhes_screen.dart';

class ModalNovoOrcamento extends StatefulWidget {
  const ModalNovoOrcamento({super.key});

  @override
  State<ModalNovoOrcamento> createState() => _ModalNovoOrcamentoState();
}

class _ModalNovoOrcamentoState extends State<ModalNovoOrcamento> {
  final ApiService _apiService = ApiService();

  List<Cliente> _clientes = [];
  Cliente? _clienteSelecionado;
  Veiculo? _veiculoSelecionado;

  bool _isLoading = true;
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _carregarClientes();
  }

  Future<void> _carregarClientes() async {
    try {
      final response = await _apiService.getRequest('/clientes');
      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        setState(() {
          _clientes = data.map((json) => Cliente.fromJson(json)).toList();
        });
      }
    } catch (e) {
      // Tratar erro
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _criarOrcamento() async {
    if (_clienteSelecionado == null || _veiculoSelecionado == null) return;

    setState(() => _isSaving = true);
    try {
      final response = await _apiService.postRequest(
          '/orcamento?idCliente=${_clienteSelecionado!.idCliente}&placa=${_veiculoSelecionado!.placa}',
          {}
      );

      if (response.statusCode == 201) {
        final dados = jsonDecode(response.body);
        if (mounted) {
          Navigator.pop(context); // Fecha o modal
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => OrcamentoDetalhesScreen(orcamentoId: dados['idOrcamento'])),
          );
        }
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erro ao criar orçamento')));
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Novo Orçamento', style: TextStyle(color: Colors.blue)),
      content: _isLoading
          ? const SizedBox(height: 100, child: Center(child: CircularProgressIndicator()))
          : Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          DropdownButtonFormField<Cliente>(
            decoration: const InputDecoration(labelText: 'Selecione o Cliente', border: OutlineInputBorder()),
            items: _clientes.map((c) => DropdownMenuItem(value: c, child: Text(c.nome))).toList(),
            onChanged: (cliente) {
              setState(() {
                _clienteSelecionado = cliente;
                _veiculoSelecionado = null; // Reseta o veículo se mudar de cliente
              });
            },
          ),
          const SizedBox(height: 16),
          if (_clienteSelecionado != null)
            DropdownButtonFormField<Veiculo>(
              value: _veiculoSelecionado,
              decoration: const InputDecoration(labelText: 'Selecione o Veículo', border: OutlineInputBorder()),
              items: _clienteSelecionado!.veiculos.map((v) => DropdownMenuItem(value: v, child: Text('${v.marca} ${v.modelo} - ${v.placa}'))).toList(),
              onChanged: (veiculo) => setState(() => _veiculoSelecionado = veiculo),
            ),
        ],
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancelar')),
        ElevatedButton(
          onPressed: (_clienteSelecionado != null && _veiculoSelecionado != null && !_isSaving) ? _criarOrcamento : null,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
          child: _isSaving
              ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(color: Colors.white))
              : const Text('Criar', style: TextStyle(color: Colors.white)),
        ),
      ],
    );
  }
}
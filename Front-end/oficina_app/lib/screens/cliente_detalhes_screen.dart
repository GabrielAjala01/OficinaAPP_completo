import 'dart:convert';
import 'package:flutter/material.dart';
import '../models/cliente.dart';
import '../services/api_service.dart';
import '../widgets/modal_cadastro_veiculo.dart';
import '../screens/cadastro_cliente_screen.dart';
import '../screens/orcamentos_detalhes_screen.dart';

class ClienteDetalhesScreen extends StatefulWidget {
  final Cliente cliente;

  const ClienteDetalhesScreen({super.key, required this.cliente});

  @override
  State<ClienteDetalhesScreen> createState() => _ClienteDetalhesScreenState();
}

class _ClienteDetalhesScreenState extends State<ClienteDetalhesScreen> {
  late Cliente _clienteAtual;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _clienteAtual = widget.cliente;
    _recarregarCliente();
  }


  Future<void> _recarregarCliente() async {
    setState(() => _isLoading = true);
    try {
      final response = await ApiService().getRequest('/clientes/${_clienteAtual.idCliente}');
      if (response.statusCode == 200) {
        setState(() {
          _clienteAtual = Cliente.fromJson(jsonDecode(response.body));
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Erro ao recarregar dados do cliente.')),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _abrirModalCadastroVeiculo() async {
    final bool? recarregar = await showDialog<bool>(
      context: context,
      builder: (context) => ModalCadastroVeiculo(idCliente: _clienteAtual.idCliente!),
    );

    if (recarregar == true) {
      _recarregarCliente();
    }
  }
  Future<void> _excluirVeiculo(String placa) async {
    final confirmar = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Excluir Veículo'),
        content: Text('Deseja realmente excluir o veículo de placa $placa?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Excluir', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    ) ?? false;

    if (!confirmar) return;

    setState(() => _isLoading = true);
    try {
      final response = await ApiService().deleteRequest('/veiculos/$placa');
      if (response.statusCode == 204 || response.statusCode == 200) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Veículo excluído!'), backgroundColor: Colors.green));
          _recarregarCliente();
        }
      } else {
        setState(() => _isLoading = false);
        if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erro ao excluir. Verifique se o veículo possui orçamentos/O.S.'), backgroundColor: Colors.red));
      }
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Erro: $e'), backgroundColor: Colors.red));
    }
  }

  Future<void> _excluirCliente() async {
    if (_clienteAtual.veiculos.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Não é possível excluir um cliente que possui veículos vinculados.'), backgroundColor: Colors.orange));
      return;
    }

    final confirmar = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Excluir Cliente'),
        content: const Text('Deseja realmente excluir este cliente permanentemente?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Excluir', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    ) ?? false;

    if (!confirmar) return;

    setState(() => _isLoading = true);
    try {
      final response = await ApiService().deleteRequest('/clientes/${_clienteAtual.idCliente}');
      if (response.statusCode == 204 || response.statusCode == 200) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Cliente excluído com sucesso!'), backgroundColor: Colors.green));
          Navigator.pop(context, true);
        }
      } else {
        setState(() => _isLoading = false);
        if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erro ao excluir cliente.'), backgroundColor: Colors.red));
      }
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Erro: $e'), backgroundColor: Colors.red));
    }
  }

  Future<void> _gerarOrcamento(String placa) async {
    setState(() => _isLoading = true);
    try {
      // O Spring Boot espera os parâmetros na URL
      final response = await ApiService().postRequest(
          '/orcamento?idCliente=${_clienteAtual.idCliente}&placa=$placa',
          {} // Corpo vazio
      );

      if (response.statusCode == 201) {
        final dados = jsonDecode(response.body);
        final int idNovoOrcamento = dados['idOrcamento'];

        if (mounted) {
          // Navega para a tela do orçamento
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => OrcamentoDetalhesScreen(orcamentoId: idNovoOrcamento)),
          );
        }
      } else {
        if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erro ao criar orçamento.'), backgroundColor: Colors.red));
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Erro: $e'), backgroundColor: Colors.red));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_clienteAtual.nome, style: const TextStyle(color: Colors.white)),
        backgroundColor: Colors.blue,
        iconTheme: const IconThemeData(color: Colors.white),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit, color: Colors.white),
            tooltip: 'Editar Cliente',
            onPressed: () async {

              final bool? recarregar = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => CadastroClienteScreen(clienteEdicao: _clienteAtual),
                ),
              );
              if (recarregar == true) {
                _recarregarCliente();
              }
            },
          ),
          IconButton(
            icon: const Icon(Icons.delete, color: Colors.white),
            tooltip: 'Excluir Cliente',
            onPressed: _excluirCliente,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Informações Pessoais', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.blue)),
                    const Divider(),
                    const SizedBox(height: 8),
                    _buildInfoRow(Icons.badge, 'CPF/CNPJ', _clienteAtual.cpfCnpj),
                    _buildInfoRow(Icons.phone, 'Telefone', _clienteAtual.telefone ?? 'Não informado'),
                    _buildInfoRow(Icons.email, 'Email', _clienteAtual.email ?? 'Não informado'),
                    _buildInfoRow(Icons.location_on, 'Endereço', _clienteAtual.endereco ?? 'Não informado'),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),

            // Cabeçalho de Veículos
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Veículos', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                ElevatedButton.icon(
                  onPressed: _abrirModalCadastroVeiculo,
                  icon: const Icon(Icons.add, color: Colors.white),
                  label: const Text('Adicionar', style: TextStyle(color: Colors.white)),
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
                ),
              ],
            ),
            const SizedBox(height: 16),

            _clienteAtual.veiculos.isEmpty
                ? const Center(
              child: Padding(
                padding: EdgeInsets.all(20.0),
                child: Text('Nenhum veículo vinculado.', style: TextStyle(color: Colors.grey, fontStyle: FontStyle.italic)),
              ),
            )
                : ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: _clienteAtual.veiculos.length,
              itemBuilder: (context, index) {
                final veiculo = _clienteAtual.veiculos[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8.0),
                  child: ListTile(
                    leading: const CircleAvatar(
                      backgroundColor: Colors.orange,
                      child: Icon(Icons.directions_car, color: Colors.white),
                    ),
                    title: Text('${veiculo.marca} ${veiculo.modelo} (${veiculo.ano})', style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('Placa: ${veiculo.placa} | Cor: ${veiculo.cor ?? "N/A"}'),
                    trailing:Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.request_quote, color: Colors.green),
                          tooltip: 'Novo Orçamento',
                          onPressed: () => _gerarOrcamento(veiculo.placa),
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          tooltip: 'Excluir Veículo',
                          onPressed: () => _excluirVeiculo(veiculo.placa),
                        ),
                      ],
                    ) ,
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6.0),
      child: Row(
        children: [
          Icon(icon, size: 20, color: Colors.grey.shade600),
          const SizedBox(width: 12),
          Expanded(
            child: RichText(
              text: TextSpan(
                style: const TextStyle(color: Colors.black87, fontSize: 15),
                children: [
                  TextSpan(text: '$label: ', style: const TextStyle(fontWeight: FontWeight.bold)),
                  TextSpan(text: value),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
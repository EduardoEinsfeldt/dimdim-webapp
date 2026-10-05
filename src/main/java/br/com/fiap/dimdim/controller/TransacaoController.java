package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.model.Conta;
import br.com.fiap.dimdim.model.Transacao;
import br.com.fiap.dimdim.repository.ContaRepository;
import br.com.fiap.dimdim.repository.TransacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class TransacaoController {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    public TransacaoController(TransacaoRepository transacaoRepository, ContaRepository contaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
    }

    @GetMapping("/contas/{contaId}/transacoes")
    public String listar(@PathVariable Integer contaId, Model model) {
        Conta conta = contaRepository.findById(contaId).orElseThrow();
        model.addAttribute("conta", conta);
        model.addAttribute("transacoes", transacaoRepository.findByContaIdOrderByDataTransacaoDesc(contaId));
        model.addAttribute("contas", contaRepository.findAll());
        return "transacoes";
    }

    @PostMapping("/transacoes")
    public String criar(@RequestParam Integer contaId,
                        @RequestParam String descricao,
                        @RequestParam BigDecimal valor,
                        @RequestParam String tipo,
                        RedirectAttributes redirect) {
        Conta conta = contaRepository.findById(contaId).orElseThrow();
        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setDataTransacao(LocalDateTime.now());
        transacaoRepository.save(transacao);
        redirect.addFlashAttribute("mensagem", "Transacao criada.");
        return "redirect:/contas/" + contaId + "/transacoes";
    }

    @PostMapping("/transacoes/{id}")
    public String atualizar(@PathVariable Integer id,
                            @RequestParam Integer contaId,
                            @RequestParam String descricao,
                            @RequestParam BigDecimal valor,
                            @RequestParam String tipo,
                            RedirectAttributes redirect) {
        Transacao transacao = transacaoRepository.findById(id).orElseThrow();
        Conta conta = contaRepository.findById(contaId).orElseThrow();
        transacao.setConta(conta);
        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacaoRepository.save(transacao);
        redirect.addFlashAttribute("mensagem", "Transacao atualizada.");
        return "redirect:/contas/" + contaId + "/transacoes";
    }

    @PostMapping("/transacoes/{id}/excluir")
    public String excluir(@PathVariable Integer id,
                          @RequestParam Integer contaId,
                          RedirectAttributes redirect) {
        transacaoRepository.deleteById(id);
        redirect.addFlashAttribute("mensagem", "Transacao excluida.");
        return "redirect:/contas/" + contaId + "/transacoes";
    }
}

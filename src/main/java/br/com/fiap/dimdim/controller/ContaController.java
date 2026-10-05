package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.model.Conta;
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

@Controller
public class ContaController {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaController(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contas", contaRepository.findAll());
        return "index";
    }

    @PostMapping("/contas")
    public String criar(@RequestParam String nome,
                        @RequestParam String tipo,
                        @RequestParam BigDecimal saldo,
                        RedirectAttributes redirect) {
        Conta conta = new Conta();
        conta.setNome(nome);
        conta.setTipo(tipo);
        conta.setSaldo(saldo);
        contaRepository.save(conta);
        redirect.addFlashAttribute("mensagem", "Conta criada.");
        return "redirect:/";
    }

    @PostMapping("/contas/{id}")
    public String atualizar(@PathVariable Integer id,
                            @RequestParam String nome,
                            @RequestParam String tipo,
                            @RequestParam BigDecimal saldo,
                            RedirectAttributes redirect) {
        Conta conta = contaRepository.findById(id).orElseThrow();
        conta.setNome(nome);
        conta.setTipo(tipo);
        conta.setSaldo(saldo);
        contaRepository.save(conta);
        redirect.addFlashAttribute("mensagem", "Conta atualizada.");
        return "redirect:/";
    }

    @PostMapping("/contas/{id}/excluir")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        transacaoRepository.findByContaIdOrderByDataTransacaoDesc(id)
                .forEach(t -> transacaoRepository.deleteById(t.getId()));
        contaRepository.deleteById(id);
        redirect.addFlashAttribute("mensagem", "Conta excluida.");
        return "redirect:/";
    }
}

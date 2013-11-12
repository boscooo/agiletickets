package br.com.caelum.agiletickets.controllers;

import static br.com.caelum.vraptor.view.Results.status;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Strings;

@Resource
public class EspetaculosController {

	private final Agenda agenda;
	private Validator validator;
	private Result result;
	private final DiretorioDeEstabelecimentos estabelecimentos;

	public EspetaculosController(Agenda agenda,
			DiretorioDeEstabelecimentos estabelecimentos, Validator validator,
			Result result) {
		this.agenda = agenda;
		this.estabelecimentos = estabelecimentos;
		this.validator = validator;
		this.result = result;
	}

	@Get
	@Path("/espetaculos")
	public List<Espetaculo> lista() {
		result.include("estabelecimentos", estabelecimentos.todos());
		return agenda.espetaculos();
	}

	@Post
	@Path("/espetaculos")
	public void adiciona(Espetaculo espetaculo) {
		validarEspetaculo(espetaculo);
		validator.onErrorRedirectTo(this).lista();
		agenda.cadastra(espetaculo);
		result.redirectTo(this).lista();
	}

	@Get
	@Path("/sessao/{id}")
	public void sessao(Long id) {
		Sessao sessao = agenda.sessao(id);
		if (sessao == null) {
			result.notFound();
		}
		result.include("sessao", sessao);
	}

	@Post
	@Path("/sessao/{sessaoId}/reserva")
	public void reserva(Long sessaoId, final Integer quantidade) {
		Sessao sessao = agenda.sessao(sessaoId);
		if (sessao == null) {
			result.notFound();
			return;
		}

		validarReserva(quantidade, sessao);
		validator.onErrorRedirectTo(this).sessao(sessao.getId());

		sessao.reserva(quantidade);
		adicionarMensagem("Sessao reservada com sucesso");
		result.redirectTo(IndexController.class).index();
	}

	@Get
	@Path("/espetaculo/{espetaculoId}/sessoes")
	public void sessoes(Long espetaculoId) {
		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);
		result.include("espetaculo", espetaculo);
	}

	@Post
	@Path("/espetaculo/{espetaculoId}/sessoes")
	public void cadastraSessoes(Long espetaculoId, LocalDate inicio,
			LocalDate fim, LocalTime horario, Periodicidade periodicidade) {

		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario,
				periodicidade);

		agenda.agende(sessoes);
		adicionarMensagem(sessoes.size() + " sessoes criadas com sucesso");
		result.redirectTo(this).lista();
	}

	private void adicionarMensagem(String mensagem) {
		result.include("message", mensagem);
	}

	private Espetaculo carregaEspetaculo(Long espetaculoId) {
		Espetaculo espetaculo = agenda.espetaculo(espetaculoId);
		if (espetaculo == null) {
			validator.add(new ValidationMessage("", ""));
		}
		validator.onErrorUse(status()).notFound();
		return espetaculo;
	}

	private void validarReserva(final Integer quantidade, Sessao sessao) {
		if (quantidade < 1) {
			adicionarValidacao("Voce deve escolher um lugar ou mais");
		}

		if (!sessao.podeReservar(quantidade)) {
			adicionarValidacao("Nao existem ingressos disponíveis");
		}
	}

	private void adicionarValidacao(String mensagem) {
		validator.add(new ValidationMessage(mensagem, ""));
	}

	private void validarEspetaculo(Espetaculo espetaculo) {
		if (Strings.isNullOrEmpty(espetaculo.getNome())) {
			adicionarValidacao("Nome do espetáculo nao pode estar em branco");
		}
		if (Strings.isNullOrEmpty(espetaculo.getDescricao())) {
			adicionarValidacao("Descricao do espetaculo nao pode estar em branco");
		}
	}
}

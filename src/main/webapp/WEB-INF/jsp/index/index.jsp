<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	<h2>Pr�ximas sess�es agora</h2>
	<ul id="sessoes">
	<c:forEach items="${sessoes}" var="sessao">
		<li>
			<a href="/sessao/${sessao.id}">
				${sessao.dia} - ${sessao.espetaculo.nome } - Ingressos Dispon�veis: ${sessao.ingressosDisponiveis}
			</a>
		</li>
	</c:forEach>
	</ul>
</body>
</html>
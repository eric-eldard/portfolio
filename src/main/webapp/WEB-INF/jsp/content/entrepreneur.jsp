<%@ page session="false" %>
<h1>Aluvi.ai</h1>
<p>
    In late 2024, I partnered with Brazen's co-founder on a brand-new venture: <b>Aluvi.ai</b>. Aluvi provides
    customer success AI agents, powered by a custom Retrieval-Augmented Generation (RAG) workflow, employing pgvector
    similarity search and OpenAI's gpt-4o-mini model, to assist account managers in understanding the health of their
    accounts.
</p>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="JSP 3, Bootstrap 5, CSS3, TypeScript"/>
    <jsp:param name="backend" value="Java 21, Spring Boot 3, Spring Web, Spring Security"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="storage" value="PostgreSQL 17 in Amazon RDS"/>
    <jsp:param name="ai" value="pgvector, gpt-4o-mini"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="deployment" value="Amazon EC2"/>
</jsp:include>

<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="aluvi-home.png"/>
    <jsp:param name="description" value="Aluvi home page"/>
</jsp:include>

<h4>Retrieval-Augmented Generation</h4>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="aluvi-rag.png"/>
    <jsp:param name="description" value="Aluvi RAG workflow"/>
    <jsp:param name="classes"     value="white-border"/>
</jsp:include>

<h4>Proprietary, internal tools help us tune the RAG workflow</h4>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="aluvi-message-inspector.png"/>
    <jsp:param name="description" value="Aluvi message inspector"/>
</jsp:include>
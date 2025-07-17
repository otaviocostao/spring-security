# API de Feed Social com Spring Security & JWT

![Java](https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green.svg?style=for-the-badge&logo=spring)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-blueviolet.svg?style=for-the-badge&logo=spring-security)
![JPA/Hibernate](https://img.shields.io/badge/JPA_/_Hibernate-orange.svg?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue.svg?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-red.svg?style=for-the-badge&logo=apache-maven)

Este projeto implementa uma API RESTful segura para um feed de posts, demonstrando um fluxo completo de autenticação e autorização utilizando **Spring Boot** e **Spring Security**. O sistema utiliza **JSON Web Tokens (JWT)** assinados com chaves assimétricas **RSA** para garantir a comunicação stateless e segura entre o cliente e o servidor.

## ✨ Funcionalidades Principais

-   **Autenticação com JWT:** Endpoint de login (`/login`) que valida as credenciais do usuário e retorna um JWT.
-   **Autorização Baseada em Roles:** O acesso aos endpoints é controlado por roles (ex: `ADMIN`, `USER`).
-   **Endpoints Protegidos:** Implementação de segurança em endpoints para operações de CRUD (Criar, Ler, Deletar) de posts.
-   **Autorização por Dono do Recurso:** Um usuário só pode deletar seus próprios posts, a menos que seja um `ADMIN`.
-   **Feed com Paginação:** Endpoint (`/feed`) que retorna uma lista paginada de posts.
-   **Inicialização de Dados:** Criação automática de um usuário `ADMIN` e roles na inicialização da aplicação via `data.sql`.
-   **Geração de Chaves RSA:** Script e instruções para gerar o par de chaves pública/privada necessário para assinar os JWTs.

## 🛠️ Tecnologias Utilizadas

-   **Backend:**
    -   [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
    -   [Spring Boot 3](https://spring.io/projects/spring-boot)
    -   [Spring Security 6](https://spring.io/projects/spring-security)
    -   [Spring Data JPA (Hibernate)](https://spring.io/projects/spring-data-jpa)
    -   [Maven](https://maven.apache.org/)
-   **Banco de Dados:**
    -   [PostgreSQL](https://www.postgresql.org/)
-   **Testes de API:**
    -   [Insomnia](https://insomnia.rest/) / [Postman](https://www.postman.com/)

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

-   **JDK 17** ou superior
-   **Maven 3.8** ou superior
-   **PostgreSQL** instalado e em execução
-   Uma ferramenta de API como **Insomnia** ou **Postman**

### 1. Clonar o Repositório

```bash
git clone https://github.com/otaviocostao/spring-security.git
cd .\spring-security
```

### 2. Gerar as Chaves Criptográficas RSA

Para assinar os JWTs, você precisa de um par de chaves pública/privada. Execute os seguintes comandos no terminal, na raiz do projeto:

Generated bash
### Gerar a chave privada (formato PKCS#8)]
```
openssl genpkey -algorithm RSA -out src/main/resources/app.key -pkeyopt rsa_keygen_bits:2048
```

### Extrair a chave pública
```
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

Isso criará os arquivos app.key e app.pub dentro de src/main/resources.

### 3. Iniciar o Banco de Dados com Docker Compose

O projeto já inclui um arquivo docker-compose.yml que provisiona um container PostgreSQL com todas as configurações necessárias.
Para iniciar o banco de dados, execute o seguinte comando no terminal, na raiz do projeto:
```
docker-compose up -d
```
up cria e inicia o container.
-d (detached mode) executa o container em segundo plano.

O arquivo src/main/resources/application.properties já está configurado para se conectar a este container Docker.
Para verificar se o container está em execução, use o comando docker ps.

### 4. Executar a Aplicação
Use o Maven para compilar e executar o projeto:

```
mvn spring-boot:run
```

A API estará disponível em http://localhost:8080.

## 📖 Endpoints da API

Aqui está uma visão geral dos principais endpoints.

Método	Endpoint	Descrição	Autenticação	Autorização
POST	/login	Autentica um usuário e retorna um JWT.	Não requerida	-
GET	/feed	Retorna uma lista paginada de posts.	Requer Token	Qualquer usuário
POST	/posts	Cria um novo post. O post é associado ao usuário autenticado.	Requer Token	Qualquer usuário
DELETE	/posts/{postId}	Deleta um post.	Requer Token	ADMIN ou dono do post
GET	/users	(Exemplo) Retorna uma lista de usuários.	Requer Token	Apenas ADMIN

Corpo da Requisição de Login (POST /login)
Generated json
{
  "username": "admin",
  "password": "123"
}

Exemplo de Resposta do Login
Generated json
{
  "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
  "expiresIn": 3600
}

Para acessar os endpoints protegidos, inclua o accessToken no cabeçalho Authorization da sua requisição:

Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...

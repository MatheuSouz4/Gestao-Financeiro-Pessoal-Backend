# 🚀 MsSystems - Backend API

> API RESTful desenvolvida para fins acadêmico de um ecossistema de gestão financeira pessoal **"MsSystems"**.

Este projeto gerencia o núcleo de inteligência financeira, garantindo a integridade dos dados, a segurança das transações e o processamento eficiente de fluxos de caixa. Foi desenvolvido com foco em escalabilidade e padrões de projeto modernos.

## 🛠️ Tecnologias e Ferramentas

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.x
* **Persistência:** Spring Data JPA
* **Segurança:** Spring Security (JWT/Basic Auth)
* **Banco de Dados:** PostgreSQL 17
* **Gerenciador de Dependências:** Maven
* **Arquitetura:** Camadas (Controller, Service, Repository, Entity, DTO)

## 📌 Funcionalidades Principais

* **Gestão de Lançamentos:** Registro completo de entradas e saídas com validação de dados.
* **Lógica de Quitações:** Sistema inteligente para transição de status de títulos (Aberto/Liquidado) com atualização automática de saldos.
* **Segurança e Proteção:** Implementação de camadas de segurança para proteção de dados financeiros sensíveis.
* **Padronização de Respostas:** Manipulador global de exceções para garantir que a API retorne erros estruturados e compreensíveis.
* **Qualidade (SQA):** Código escrito seguindo princípios de Clean Code e alinhado a modelos de maturidade de software.

## ⚙️ Como Executar o Projeto

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/MatheuSouz4/mssystems-backend.git](https://github.com/MatheuSouz4/mssystems-backend.git)
    ```
2.  **Configuração do Banco:** Certifique-se de ter o PostgreSQL 17 instalado e configure as credenciais no arquivo `src/main/resources/application.properties`.
3.  **Build e Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
A API estará disponível em: `http://localhost:8080`

---
💻 **Interface do Usuário:** [Acesse o Repositório Frontend](https://github.com/MatheuSouz4/Gestao-Financeira-Pesssoal.MsSystems)

# 🚌🚦 Projeto: PetroCarga – Mobilidade Inteligente

> Uma plataforma inovadora desenvolvida para otimizar a gestão de vagas de carga e descarga na cidade de Petrópolis.  
> Conectando empresas, motoristas e a CPTrans, o PetroCarga digitaliza o processo de agendamento e monitoramento de vagas, promovendo mais eficiência e organização no trânsito urbano. 🚗💨

---

## 🗺️ Índice

- [🚀 Sobre o Projeto](#-sobre-o-projeto)
- [🧰 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [🏗️ Arquitetura do Sistema](#️-arquitetura-do-sistema)
- [⚙️ Instalação e Execução](#️-instalação-e-execução)
- [📖 Swagger](#-swagger)

---

## 🚀 Sobre o Projeto

O **PetroCarga** é um projeto desenvolvido por **residentes STEM do Serratec**, em parceria com a **CPTrans**, com o objetivo de resolver um problema real enfrentado pela cidade de **Petrópolis**:  
o gerenciamento manual e ineficiente das **vagas de carga e descarga**.

Através dessa plataforma, é possível realizar o **agendamento digital das vagas**, **monitorar as utilizações em tempo real** e **gerenciar permissões** de forma centralizada.  
O sistema foi projetado para proporcionar **maior controle operacional** e **reduzir conflitos de uso** em áreas urbanas de alta movimentação.

> 💡 Em resumo, o PetroCarga é uma solução moderna para uma cidade mais organizada e inteligente.

---

## 🧰 Tecnologias Utilizadas

O projeto foi desenvolvido utilizando um ecossistema moderno e robusto, voltado para aplicações corporativas e escaláveis.

### ⚙️ Backend
- [Java 21](https://www.oracle.com/java/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL 17](https://www.postgresql.org/)
- [Flyway](https://flywaydb.org/) – versionamento e controle de migrações do banco de dados

### 🧪 Testes e Ferramentas de Apoio
- [Postman](https://www.postman.com/) – testes de requisições HTTP
- [Maven](https://maven.apache.org/) – gerenciamento de dependências e build

---

## 🏗️ Arquitetura do Sistema

A aplicação segue uma **arquitetura REST padrão**, com camadas bem definidas para facilitar a manutenção e escalabilidade:
<p align="center">
<img width="464" height="525" alt="image" src="https://github.com/user-attachments/assets/68efc98f-a7b8-4b50-8944-3cd8c34eec41" />
</p>

- **Controller:** expõe os endpoints da API REST.  
- **Service:** contém a lógica de negócios.  
- **Repository:** faz a comunicação com o banco de dados.  
- **DTO / Entity:** representam os objetos de transferência e persistência de dados.  
- **Flyway:** controla a versão e atualização do esquema do banco automaticamente.

---

## ⚙️ Instalação e Execução

### 🧩 Pré-requisitos

Certifique-se de ter instalado em sua máquina:
- ☕ **Java 21**
- 🐘 **PostgreSQL 17**
- 💻 **IDE** de sua preferência  
  > Recomendado: *Spring Tools Suite 4*, *VS Code* ou *IntelliJ IDEA*

---

### 🗂️ Configuração do Banco de Dados

1. Configure o usuário e senha padrão:
  Usuário: postgres
  Senha: 123456
2. Crie um banco chamado `petrocarga`  

3. O Flyway será responsável por criar e versionar automaticamente as tabelas na primeira execução.

---

### ▶️ Executando a Aplicação

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/petrocarga.git

# Acesse o diretório do projeto
cd petro-carga

# Execute o projeto com Maven
mvn spring-boot:run


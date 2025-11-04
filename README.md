# EDUCARA

EDUCARA é um projeto para visualização de objetos educacionais 3D utilizando realidade aumentada. Este projeto é desenvolvido para dispositivos Android usando Kotlin, integrando tecnologias como o ARCore.

## Visão Geral

Este repositório é um monorepo que contém:

- **App Educara:** Versão completa do aplicativo com todos os recursos.
- **Serviços:** Um back-end de serviços web básico integrado ao aplicativo.

## Tecnologias Utilizadas

- **Kotlin:** Linguagem de programação principal do projeto.
- **Android:** Plataforma de desenvolvimento.
- **ARCore:** Utilizado para integrar a realidade aumentada.
- **Docker:** Para conteinerização dos serviços web do back-end.

## Como Começar

Siga as instruções abaixo para clonar, compilar e executar o projeto.

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

- [Android Studio](https://developer.android.com/studio)
- [Git](https://git-scm.com/)

### Clonando o Repositório

```bash
git clone https://github.com/luispscarvalho/EducaRA.git
cd educara
```

### Compilando e Executando
- Abra um terminal de comando e execute o comando a partir do diretório "servicos":

```bash
docker-compose up -d --build
```

ou utilize o Docker Desktop para iniciar o back-end.
- Abra o projeto no Android Studio que está na pasta "aplicativo".
- Sincronize os arquivos do projeto e as dependências (Gradle).
- Conecte um dispositivo Android ou use um emulador.
- Clique em "Run" para compilar e executar o aplicativo.

### Estrutura do Projeto
```bash
educara/
│
├── aplicativo/        # Código fonte do App Educara FULL
│
├── servicos/          # Código fonte de um back-end básico
└── README.md          # Documentação do projeto
```

🚧Projeto em desenvolvimento...
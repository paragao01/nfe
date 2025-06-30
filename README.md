# Projeto de Integração NFe

Este projeto é uma aplicação de linha de comando em Java para comunicação com os web services da Nota Fiscal Eletrônica (NFe) e da Nota Fiscal de Consumidor Eletrônica (NFCe) do Brasil. Ele encapsula a complexidade das chamadas SOAP, assinaturas digitais e as particularidades de cada serviço e estado.

## Funcionalidades

- Consulta de Status do Serviço
- Consulta de Situação da NFe
- Envio de NFe (Síncrono e Assíncrono)
- Cancelamento de NFe
- Inutilização de Numeração
- Consulta de Cadastro de Contribuinte
- Geração de Logs de Entrada e Saída

## Tecnologias Utilizadas

- **Java 8+**
- **Apache Ant:** Para automação do build.
- **JAXB:** Para manipulação de objetos XML.
- **Apache Axis (implícito):** Para a comunicação com os web services SOAP.

## Estrutura do Projeto

```
nfe/
├── src/                  # Código-fonte Java
│   └── br/com/debra/
│       ├── certificado/  # Lógica para manipulação de Certificado Digital
│       └── nfe/          # Lógica principal da NFe
│           ├── dom/      # Objetos de domínio e Enums
│           ├── schema/   # Classes JAXB geradas dos schemas XSD
│           ├── util/     # Classes utilitárias (XML, Chave de Acesso, etc)
│           └── wsdl/     # Stubs de cliente para os web services
├── lib/                  # Dependências (JARs)
├── schemas/              # Arquivos de Schema XSD da NFe
├── cert/                 # Local para armazenar o certificado digital
├── log/                  # Arquivos de log gerados pela aplicação
├── build.xml             # Script de build do Apache Ant
├── nfe.conf              # Arquivo principal de configuração
├── WebServicesNfe.ini    # Mapeamento de URLs dos web services
└── nfe.sh                # Script para execução da aplicação
```

## Configuração

Antes de executar o projeto, é necessário configurar três pontos principais.

### 1. Arquivo `nfe.conf`

Este é o arquivo de configuração principal.

```ini
# Ambiente: 1 para Produção, 2 para Homologação
AMBIENTE = 2
# Caminhos para os diretórios (relativos à raiz do projeto)
PASTALOG = ./log
ARQURLNFE= ./WebServicesNfe.ini
ARQSCHEMA= ./schemas
ARQCERTIF= ./cert
# Configurações de Rede
TIMEOUTWS= 30000
TENTATIVA= 3
# Habilita/desabilita a validação do XML contra o schema
VALIDACAO= TRUE
```
**Atenção:** Os caminhos no arquivo original podem estar absolutos (`/log`, `/schemas`, etc.). É recomendado alterá-los para caminhos relativos (`./log`, `./schemas`) para tornar o projeto portável.

### 2. Certificado Digital

A aplicação requer um certificado digital do tipo A1 (arquivo `.pfx` ou `.p12`) para se autenticar nos web services da SEFAZ.

1.  Coloque o arquivo do seu certificado no diretório `cert/`.
2.  A configuração do certificado é lida a partir do código, no método `iniciaConfiguracoes()` do arquivo `src/br/com/debra/nfe/Main.java`. Você precisará ajustar o nome do arquivo, a senha e o alias diretamente no código:

    ```java
    // ... dentro de iniciaConfiguracoes() ...
    String caminhoCertificado = config.getCertificado(); // Vem de nfe.conf -> /cert
    String nomeCertificado = "NOME_DO_SEU_CERTIFICADO.pfx";
    String senhaCertificado = "SENHA_DO_SEU_CERTIFICADO";
    String aliasCertificado = "ALIAS_DO_SEU_CERTIFICADO"; // Opcional

    Certificado certificado = CertificadoService.certificadoPfx(
        caminhoCertificado + "/" + nomeCertificado, 
        senhaCertificado
    );
    // ...
    ```

### 3. Scripts de Execução

Os scripts `nfe.sh` e `AssinaA1.sh` possuem caminhos absolutos hardcoded. Você **precisa** editá-los para que funcionem em seu ambiente.

**Exemplo de correção para `nfe.sh`:**

```bash
#!/bin/bash
# Navega para o diretório onde o script está localizado
cd "$(dirname "$0")"
java -jar nfe.jar
```

## Como Compilar

O projeto utiliza Apache Ant para o build. Para compilar, execute o seguinte comando na raiz do projeto:

```bash
ant
```

Isso irá compilar o código-fonte, gerar o arquivo `nfe.jar` no diretório raiz e limpar os arquivos `.class` temporários.

## Como Executar

A aplicação é executada via linha de comando, recebendo os parâmetros através de `stdin` e retornando o resultado em `stdout`.

O formato de entrada é uma string única, com parâmetros separados por ponto e vírgula:

```
<conteudo_xml>;<codigo_servico>[;<parametro_3>;<parametro_4>]
```

O script `nfe.sh` facilita a execução:

```bash
echo "<conteudo_xml>;<codigo_servico>" | ./nfe.sh
```

### Serviços Disponíveis

| Código | Serviço               | Exemplo de Parâmetros                                 |
| :----- | :-------------------- | :---------------------------------------------------- |
| `1`    | Status do Serviço     | `1`                                                   |
| `2`    | Situação da NFe       | `<chave_acesso>;2`                                    |
| `3`    | Envio de NFe          | `<xml_completo_da_nfe>;3`                             |
| `4`    | Consulta de Cadastro  | `<cnpj_ou_ie>;4`                                      |
| `5`    | Cancelamento de NFe   | `<xml_da_nfe>;5;<justificativa>;<protocolo>`          |
| `6`    | Inutilização de NFe   | `<xml_parcial_ide>;6;<justificativa>`                 |

**Exemplo Prático (Status do Serviço):**

```bash
# O primeiro parâmetro pode ser qualquer coisa, pois não é usado para este serviço.
echo "status;1" | ./nfe.sh
```

A saída será algo como:

```
# Status: 107 - Servico em Operacao # SVRS - Sefaz Virtual RS
```

## Observações Importantes

*   **Script `AssinaA1.sh`:** Este script parece estar desatualizado ou incorreto, pois chama uma classe `AssinaA1` que não existe no projeto. A assinatura dos XMLs é realizada internamente pela aplicação Java, não por um script separado.
*   **Portabilidade:** A principal barreira para a portabilidade são os caminhos absolutos nos scripts e, potencialmente, no `nfe.conf`. Siga as instruções na seção de configuração para corrigi-los. 
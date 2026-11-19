# Auditoria final de aceitação — Jogo da Velha

## Versão e escopo

- Branch: fix/integracao-final-jogo-da-velha.
- Base da auditoria: 1455b3312adf03499ff3e74849bcc51d89b57691.
- Ambiente executado: Microsoft OpenJDK 17.0.11, Windows/PowerShell.
- Original localizado: C:\Users\julia\Downloads\Velha.zip.
- SHA-256 do original: 7E11C116198614C0B12A582173FBAAB97054EF618C375913707667060F5A02FD.

Foram lidos os sete fontes do ZIP original e os sete fontes de produção atuais.
A matriz cobre todos os requisitos encaminhados pelo usuário e os contratos e
comentários desses fontes. O ZIP original não contém um enunciado formal ou uma
rubrica separados; critérios adicionais de um documento não fornecido não foram
presumidos nem certificados.

APROVADO significa que a evidência indicada sustenta o requisito. A indicação
EST identifica inspeção estática, sem apresentá-la como teste executado.
NÃO VERIFICADO identifica informação indisponível; REPROVADO identificaria
um requisito conhecido que falhou. Não há requisito funcional reprovado.

## Evidências reproduzíveis

- **EST:** leitura integral das classes; comparação com os sete fontes do original,
  registrada em [comparacao-esqueleto.txt](aceitacao/comparacao-esqueleto.txt).
- **R:** TestesRegras: testarMarca, testarTabuleiro, testarCasosExplicitos,
  testarConfiguracoes e testarFalhas, usando o Tabuleiro real.
- **I:** TestesEntrada: entrada simulada em objetos reais, incluindo dois humanos
  consecutivos e escolhas X/O, sem recriar o fluxo de entrada.
- **F:** TestesEntrada falha e TestesRegras.testarFalhas: falhas deliberadamente
  injetadas para conferir propagação e preservação da causa.
- **T:** TestesTerminal: processos reais de Velha, com entrada automatizada por pipes.
- **L:** TestesTerminal.testarAlternanciaLonga: 12 partidas completas na mesma JVM.
- **Z:** pacote extraído em pasta nova, sem classpath/sourcepath de dependências,
  com os fontes e os testes contidos na distribuição.

Os resultados da execução registrada estão em
[resultado-testes.txt](aceitacao/resultado-testes.txt). Foram aprovadas:

- 735.393 asserções de regras.
- 11 asserções de entrada.
- Uma asserção combinada para os três pontos de falha de leitura.
- 737 asserções de terminal, em nove processos reais de Velha.

Total dessa execução registrada: 736.142 asserções. Esses números não representam
partidas independentes: cada configuração ou partida verifica várias condições.
O teste de regras cobre 65.536 caracteres, 19.683 configurações para o analista
e 39.366 cenários do computador. Inclui estados artificiais para conferir regras
locais, sem afirmar que sejam todos alcançáveis numa partida.

A validação independente do pacote está registrada em
[validacao-zip.txt](aceitacao/validacao-zip.txt). A quantidade de asserções e
processos de terminal pode variar em novas execuções por causa do sorteio e dos
roteiros associados ao iniciante. A exigência é que todas sejam aprovadas.

## Alternância sem partidas omitidas

A execução registrada da sessão longa foi:

| Partida na mesma sessão | Iniciante |
|---|---|
| 1 | Computador |
| 2 | Humano |
| 3 | Computador |
| 4 | Humano |
| 5 | Computador |
| 6 | Humano |
| 7 | Computador |
| 8 | Humano |
| 9 | Computador |
| 10 | Humano |
| 11 | Computador |
| 12 | Humano |

A conversa completa está em
[alternancia-12-partidas.txt](aceitacao/alternancia-12-partidas.txt), e o resumo,
em [iniciantes.txt](aceitacao/iniciantes.txt). Todas as partidas terminaram em
empate, foram verificadas e tiveram tentativas de entrada inválida.

O primeiro iniciante permaneceu aleatório. Depois de observar o primeiro,
o teste calculou deterministicamente quem deveria iniciar cada partida seguinte.
O teste não altera o sorteio nem a lógica do programa. A série também confere
tabuleiro vazio, marcas diferentes e alternância de todos os turnos.

Registros de processos diferentes não formam uma única sequência de partidas:
cada novo processo sorteia seu primeiro iniciante. Os registros desta auditoria
identificam sessão e número da partida para evitar essa ambiguidade.

## Matriz de conformidade

### Estrutura e orientação a objetos

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| G01 | Responsabilidades das sete classes preservadas | Todas | EST: original e implementação, com matriz somente em Tabuleiro e sessão em Velha | APROVADO |
| G02 | Assinaturas dos métodos e construtores originais | Todas | EST: comparação de todas as assinaturas; clone de Tabuleiro usa retorno covariante compatível | APROVADO |
| G03 | Comentários orientadores e sequência didática | Todas | EST: leitura; os 24 comentários de tentativa do automático são idênticos e mantêm a ordem | APROVADO |
| G04 | Encapsulamento e atributos privados | Marca, Tabuleiro, jogadores, analista | EST; R: acesso pelas operações públicas, sem acesso direto à matriz | APROVADO |
| G05 | Contrato da interface Jogador implementado | Jogador e jogadores | EST; compilação conjunta de getMarca e facaSuaJogada | APROVADO |
| G06 | Chamada polimórfica por Jogador | Velha | EST: variável Jogador; T: ambas as implementações executadas | APROVADO |
| G07 | Java simples, sem Minimax, frameworks ou dependências externas | Produção | EST: sete fontes e imports; compilação com dependências vazias, exceto JDK | APROVADO |
| G08 | Pacotes, imports e uma única classe de cada tipo | Todas | EST: sete fontes de produção; compilação limpa | APROVADO |
| G09 | Nenhum método incompleto ou código morto que comprometa o jogo | Todas | EST integral, compilação e R/I/T; verificações defensivas do código dos colegas preservadas | APROVADO |
| G10 | Referências nulas tratadas nas operações e resultados | Todas | R/I/T: argumentos nulos, EOF e empate; EST dos caminhos de controle | APROVADO |
| G11 | Nenhuma exceção silenciosamente ignorada na produção | Todas | EST: nenhum catch vazio; F: causas preservadas; Velha diferencia EOF e falha interna | APROVADO |
| G12 | Trabalho e histórico dos três integrantes preservados | Repositório | EST: commits anteriores mantidos; origens no README; alterações nesta auditoria limitadas ao diagnóstico e testes/documentação | APROVADO |

### Marca

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| M01 | Aceitar exclusivamente X e O | Marca | R: todos os 65.536 valores de char | APROVADO |
| M02 | Rejeitar símbolo inválido com exceção e mensagem | Marca | R: todos os demais caracteres, incluindo x, o e espaço | APROVADO |
| M03 | getSimbolo devolver o símbolo armazenado | Marca | R: construção de X e O e consulta nas configurações | APROVADO |
| M04 | Preservar toString original | Marca | EST: expressão original preservada; R: texto X | APROVADO |
| M05 | equals por conteúdo, com identidade, null, classe e cast | Marca | EST; R: marcas iguais/diferentes, null, outro tipo e subclasse | APROVADO |
| M06 | hashCode consistente com equals | Marca | R: marcas e tabuleiros iguais com referências independentes | APROVADO |
| M07 | Não permitir alteração inválida por setter | Marca | EST: símbolo privado, sem setter | APROVADO |
| M08 | Cópia e clone válidos; modelo nulo rejeitado | Marca | R: cópia, clone independente e construtor com modelo nulo; EST do diagnóstico de clone | APROVADO |

### Tabuleiro

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| T01 | Matriz privada 3x3 e nove casas inicialmente vazias | Tabuleiro | EST; R: todas as nove consultas iniciais | APROVADO |
| T02 | Inserção de X e O | Tabuleiro | R: configurações e cenários explícitos | APROVADO |
| T03 | Rejeitar índices inválidos nas três operações públicas | Tabuleiro | R: limites -1 e 3 para linha/coluna em haMarca, getMarca e setMarca; EST das condições | APROVADO |
| T04 | Rejeitar marca nula | Tabuleiro | R: tentativa de inserção nula sem alterar a casa | APROVADO |
| T05 | Rejeitar posição ocupada | Tabuleiro | R: tentativa de sobrescrita; I/T: nova solicitação ao humano | APROVADO |
| T06 | Preservar marcas já existentes | Tabuleiro | R: símbolos e referências preservados em todos os cenários do automático | APROVADO |
| T07 | toString legível e coordenadas coerentes | Tabuleiro | R: desenho vazio exato; T: desenhos e convenção 1 a 3 | APROVADO |
| T08 | Construtor de cópia com matriz independente | Tabuleiro | R: alterar a cópia não altera o original | APROVADO |
| T09 | Cópia profunda das marcas | Tabuleiro | R: nove marcas de uma cópia cheia têm conteúdo igual e referências diferentes | APROVADO |
| T10 | clone independente | Tabuleiro | R: clone e original recebem jogadas independentes | APROVADO |
| T11 | equals e hashCode consistentes | Tabuleiro | R: matrizes iguais/diferentes, simetria, null e outro tipo | APROVADO |
| T12 | Construtor sem argumentos e rejeição de modelo nulo | Tabuleiro | R: construção vazia e cópia de null | APROVADO |

### Jogador humano

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| H01 | Escolher X ou O validamente | JogadorHumano | I: duas instâncias recebem X e O, inclusive normalização de minúsculas; T: sessões reais | APROVADO |
| H02 | Rejeitar marcas inválidas, inclusive XO | JogadorHumano | I/T: XO e ? antes de uma escolha válida | APROVADO |
| H03 | Rejeitar entradas não numéricas | JogadorHumano | I/T: abc e nova solicitação | APROVADO |
| H04 | Rejeitar casa ocupada e solicitar outra | JogadorHumano | I/T: tentativa em casa ocupada seguida de jogada válida | APROVADO |
| H05 | Rejeitar coordenadas fora do intervalo | JogadorHumano | I/T: 0 e 4; EST de linha e coluna | APROVADO |
| H06 | Entrada inválida não encerrar nem perder a vez | JogadorHumano | I: apenas uma inserção após erros; T/L: sequência de turnos conferida | APROVADO |
| H07 | Coordenadas externas 1 a 3 convertidas para 0 a 2 | JogadorHumano | I: entrada 1,2 coloca marca em 0,1; R/T: tabuleiro e jogadas | APROVADO |
| H08 | EOF na escolha não atribuir X automaticamente | JogadorHumano | I/T: EOFException e encerramento sem escolha fictícia | APROVADO |
| H09 | EOF na linha ou coluna não concluir a jogada | JogadorHumano | I/T: encerramento sem inserção nem próximo turno | APROVADO |
| H10 | Preservar entradas entre partidas | JogadorHumano | I: X, S e O antecipados; T/L: leitor compartilhado em partidas consecutivas | APROVADO |
| H11 | Repetir com S e encerrar com N ou EOF na resposta | JogadorHumano | I/T/L: S/N, resposta inválida e EOF | APROVADO |
| H12 | Diferenciar digitação inválida e falha interna de leitura | JogadorHumano | I: repetição por erro de digitação; F: IOException propagada com causa | APROVADO |
| H13 | Validar tabuleiro e marca nos construtores | JogadorHumano | I: argumentos nulos rejeitados; EST das duas sobrecargas | APROVADO |
| H14 | Mensagens sem referências de depuração | JogadorHumano | I: ausência de JogadorHumano.java:; EST das mensagens | APROVADO |

### Jogador automático

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| A01 | Vitória imediata | JogadorAutomatico | R: comparação por simulação em todas as configurações, para ambas as marcas | APROVADO |
| A02 | Bloqueio de vitória imediata adversária | JogadorAutomatico | R: mesma enumeração, incluindo ameaças do adversário | APROVADO |
| A03 | Vencer antes de bloquear | JogadorAutomatico | R: primeira etapa da simulação e configurações com ameaças simultâneas | APROVADO |
| A04 | Preparar com uma marca própria e duas casas vazias | JogadorAutomatico | EST do auxiliar; R: resultado da terceira prioridade conferido por simulação | APROVADO |
| A05 | Escolher posição livre em ordem crescente no caso restante | JogadorAutomatico | R: tabuleiro vazio e configurações sem ação estratégica | APROVADO |
| A06 | Uma jogada por chamada quando houver espaço | JogadorAutomatico | R: exatamente uma diferença entre antes/depois, inclusive chamadas sucessivas | APROVADO |
| A07 | Nenhuma sobrescrita ou marca nula | JogadorAutomatico | R: casas ocupadas e suas referências preservadas; inserções válidas no Tabuleiro real | APROVADO |
| A08 | Estratégia com X e O | JogadorAutomatico | R: 19.683 configurações para cada uma das duas marcas | APROVADO |
| A09 | Ordem das três linhas, três colunas e duas diagonais | JogadorAutomatico | EST: 24 comentários e coordenadas; R: primeira sequência elegível da simulação | APROVADO |
| A10 | Usar API pública e índices entre 0 e 2 | JogadorAutomatico | EST: nenhum acesso à matriz; R: todas as chamadas concluídas com validação real | APROVADO |
| A11 | Tabuleiro cheio sem alteração | JogadorAutomatico | R: todas as configurações cheias e chamadas repetidas | APROVADO |
| A12 | Construtores validam e sorteiam somente quando necessário | JogadorAutomatico | EST do sorteio original; R: null, 200 marcas sorteadas e referência fornecida preservada | APROVADO |

### Analista situacional

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| N01 | Detectar vitória nas três linhas | AnalistaSituacional | R: casos explícitos para X/O e todas as configurações | APROVADO |
| N02 | Detectar vitória nas três colunas | AnalistaSituacional | R: casos explícitos para X/O e todas as configurações | APROVADO |
| N03 | Detectar vitória na diagonal principal | AnalistaSituacional | R: casos explícitos para X/O e todas as configurações | APROVADO |
| N04 | Detectar vitória na diagonal secundária | AnalistaSituacional | R: casos explícitos para X/O e todas as configurações | APROVADO |
| N05 | Vitória na última posição disponível | AnalistaSituacional | R: XOXOXOOX. seguido da marca X na última casa | APROVADO |
| N06 | Retornar null e manter jogo em andamento sem vencedor nem lotação | AnalistaSituacional | R: todas as configurações incompletas sem vencedor | APROVADO |
| N07 | Identificar empate | AnalistaSituacional | R: tabuleiro cheio sem vencedor; T/L: empates completos | APROVADO |
| N08 | fimDeJogo verdadeiro em vitória ou empate | AnalistaSituacional | R: vencedor/lotação de cada uma das 19.683 configurações | APROVADO |
| N09 | toString, equals e hashCode coerentes | AnalistaSituacional | R: conteúdo, matrizes independentes iguais, null e outro tipo | APROVADO |
| N10 | Observar o tabuleiro real e respeitar o contrato de cópia | AnalistaSituacional | R: original, cópia e clone acompanham a mesma partida; EST do contrato documentado | APROVADO |

### Classe principal e sessão

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| P01 | Sortear o primeiro iniciante uma única vez | Velha | EST: expressão original Math.random antes do laço de partidas; T/L: primeiro resultado observado sem forçá-lo | APROVADO |
| P02 | Escolha/atribuição de marcas conforme o professor | Velha e jogadores | EST: dois ramos originais preservados; I/T/L: humano escolhe se começa, computador sorteia se começa | APROVADO |
| P03 | Marcas sempre diferentes | Velha | T/L: cabeçalho de todas as partidas confere X/O opostos | APROVADO |
| P04 | Alternar todos os turnos pela interface Jogador | Velha | EST; T/L: cada evento humano/computador e sua marca conferidos | APROVADO |
| P05 | Não conceder turno após vitória ou empate | Velha | EST: verificação antes da troca; T/L: nenhum evento de turno após o resultado | APROVADO |
| P06 | Vitória humana | Velha | T: processo real termina com VOCE VENCEU e apenas um resultado | APROVADO |
| P07 | Vitória do computador/derrota humana | Velha | T: processo real termina com VOCE PERDEU e apenas um resultado | APROVADO |
| P08 | Empate sem NullPointerException | Velha | T/L: DEU VELHA, ausência de exceção e retorno à pergunta de repetição | APROVADO |
| P09 | Exibir jogadas, tabuleiro e resultado correto | Velha e JogadorHumano | EST das saídas; R: formatação; T/L: mensagens, número de jogadas e resultado | APROVADO |
| P10 | Perguntar sobre outra partida | Velha | T/L: pergunta alcançada somente após resultado completo | APROVADO |
| P11 | Reiniciar com nove casas vazias | Velha | T/L: desenho vazio exato em cada novo cabeçalho | APROVADO |
| P12 | Segundo iniciante diferente do primeiro | Velha | L: partidas 1 e 2; expectativa derivada do primeiro sorteio | APROVADO |
| P13 | Terceiro iniciante igual ao primeiro | Velha | L: partidas 1 e 3, sem omissões | APROVADO |
| P14 | Continuar alternando, sem influência de entradas inválidas | Velha | L: 12 partidas com erros de entrada e S/N inválido, todas conferidas | APROVADO |
| P15 | N encerrar sem iniciar outra partida | Velha | T/L: quantidade exata de cabeçalhos e saída normal após N | APROVADO |
| P16 | EOF encerrar corretamente e sem acesso a humano não inicializado | Velha | EST do catch com retorno; T: EOF na marca, linha, coluna e repetição | APROVADO |

### Documentação

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| D01 | Nomes dos três integrantes sem inventar dados pessoais | README | EST: Júlia, Giovana e Bruna, com origens confirmadas no histórico | APROVADO |
| D02 | Descrição do programa | README | EST: objetivo e execução no terminal | APROVADO |
| D03 | Instruções de compilação e execução | README | Comandos reproduzidos na árvore e em Z | APROVADO |
| D04 | Versão do Java utilizado | README | EST e execução: Microsoft OpenJDK 17.0.11 | APROVADO |
| D05 | Organização e responsabilidade das classes | README | EST: árvore de arquivos e descrição das sete classes | APROVADO |
| D06 | Explicação do polimorfismo | README | EST: interface Jogador e chamada em Velha | APROVADO |
| D07 | Quatro prioridades do computador | README | EST: prioridades e ordem das oito sequências | APROVADO |
| D08 | Critério da terceira prioridade | README | EST: uma própria, duas vazias e primeira casa da sequência | APROVADO |
| D09 | Convenção de coordenadas | README | EST: 1 a 3 na interface e 0 a 2 internamente; I/T | APROVADO |
| D10 | Orientação sobre nova partida e encerramento | README | EST: S/N, alternância e EOF; T/L | APROVADO |
| D11 | Limitações existentes | README | EST: heurística pode perder; analista é observador compartilhado; distinção dos estados artificiais | APROVADO |
| D12 | Testes, comandos e diferença entre simulação de entrada e jogo real | README | EST: quatro execuções Java, logs, pipes e nenhuma substituição das classes do jogo | APROVADO |

### Entrega e Git

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| Z01 | ZIP com sete fontes, testes, README e evidências | Distribuição | Inventário e comparação de conteúdo com a árvore auditada | APROVADO |
| Z02 | Não incluir binários antigos ou temporários desnecessários | Distribuição | Inventário: sem .class, build, .git ou dist no ZIP; backups fora do pacote | APROVADO |
| Z03 | Extrair em pasta completamente nova | Distribuição | Z: diretório temporário exclusivo por validação | APROVADO |
| Z04 | Compilar fontes juntos sem dependências externas ao pacote | Distribuição | Z: classpath/sourcepath vazios, usando somente o JDK e fontes entregues | APROVADO |
| Z05 | Executar os testes do pacote extraído | Distribuição | Z: R/I/F/T/L novamente aprovados | APROVADO |
| Z06 | Commit na integração, preservando main e sem publicação automática | Repositório | Revisão do diff; commit local posterior à validação; nenhum push, PR ou merge nesta auditoria | APROVADO |

### Limite documental

| ID | Requisito | Responsável | Evidência | Resultado |
|---|---|---|---|---|
| Q01 | Critérios de uma rubrica formal adicional não fornecida | Documento externo | O Velha.zip contém apenas sete fontes; não foi localizado enunciado formal separado nas pastas consultadas | NÃO VERIFICADO |

Q01 não representa uma falha funcional conhecida. Impede afirmar conformidade com
regras externas desconhecidas. A aprovação técnica se refere aos requisitos
fornecidos e ao esqueleto original efetivamente inspecionado.

## Achados e alterações desta auditoria

1. **Alternância:** nenhum defeito encontrado. O sorteio e a inversão de iniciante
   em Velha permaneceram inalterados. Acrescentada comprovação de 12 partidas.
2. **Marca.clone:** havia um catch vazio preexistente. O construtor de cópia só
   rejeita modelo nulo e this não é nulo, portanto não foi reproduzida falha no
   caminho normal. O catch agora lança IllegalStateException com a causa para
   atender ao requisito explícito de não ocultar exceções.
3. **Evidências de terminal:** cada execução agora usa uma pasta própria para não
   misturar conversas de execuções diferentes. Todos os turnos e suas marcas
   também são conferidos, além dos resultados.
4. **Cobertura:** acrescentadas verificações da cópia das nove marcas, desenho
   exato, igualdade de analistas sobre matrizes independentes, modelos nulos,
   classe em equals e preservação da marca fornecida ao computador.
5. **Documentação e pacote:** README atualizado, esta matriz e evidências
   adicionadas; ZIP recriado e validado novamente.

Não houve reescrita do tabuleiro, humano, automático, analista ou controle da sessão
nesta auditoria. O histórico das três contribuições foi preservado. Os backups de
binário e distribuições anteriores ficam em dist/preservados, ignorados pelo Git
e excluídos do ZIP final. Binários de execução são gerados em build.

Não foram encontrados defeitos conhecidos que impeçam os requisitos funcionais
verificados. Não foi executada uma sessão manual com teclado físico: a entrada foi
automatizada. Os testes reais de terminal e o registro de 12 partidas podem ser
reproduzidos pelos comandos do README.

## Executar a versão entregue

~~~powershell
javac -encoding UTF-8 -Xlint:all -d build Velha.java AnalistaSituacional.java jogador\*.java
java -cp build Velha
~~~

Para repetir a aceitação:

~~~powershell
javac -encoding UTF-8 -Xlint:all -d build Velha.java AnalistaSituacional.java jogador\*.java testes\*.java
java -ea -cp build TestesRegras
java -ea -cp build TestesEntrada
java -ea -cp build TestesEntrada falha
java -ea -cp build TestesTerminal
~~~

## Parecer técnico

**PROJETO TECNICAMENTE APROVADO PARA ENTREGA E APRESENTAÇÃO.**

A afirmação cobre os requisitos funcionais fornecidos, o esqueleto localizado,
a documentação e o pacote testado. Não equivale a uma prova de ausência de todo
defeito possível nem certifica critérios externos indisponíveis identificados em Q01.

# Jogo da Velha em Java

Trabalho de Programação Orientada a Objetos: uma pessoa joga contra o computador
pelo terminal. O projeto usa apenas a biblioteca padrão do Java.

## Integrantes e origem das implementações

- Júlia: Marca e JogadorAutomatico, incorporados pela PR #2.
- Giovana: interface Jogador e implementações de Tabuleiro, incorporadas pela PR #1.
- Bruna: JogadorHumano e AnalistaSituacional, recuperados da branch bruna.

Foram usados apenas os nomes confirmados no trabalho. A integração restaura os
trechos ausentes do tabuleiro e corrige pacotes, entrada e controle das partidas,
preservando os métodos e a estratégia do código-base.

## Estrutura

~~~text
Velha.java
AnalistaSituacional.java
jogador/
    Marca.java
    Tabuleiro.java
    Jogador.java
    JogadorAutomatico.java
    JogadorHumano.java
testes/
    TestesRegras.java
    TestesEntrada.java
    TestesTerminal.java
~~~

- **Velha:** sorteia o primeiro iniciante, cria os jogadores e controla a sessão.
- **AnalistaSituacional:** observa o tabuleiro e identifica vencedor e fim da partida.
- **Marca:** aceita somente os caracteres X e O.
- **Tabuleiro:** encapsula a matriz 3x3, valida acessos e impede sobrescrita.
- **Jogador:** contrato com getMarca() e facaSuaJogada().
- **JogadorHumano:** valida o teclado e realiza uma jogada por turno.
- **JogadorAutomatico:** escolhe a jogada pelas quatro prioridades do professor.

## Compilar e executar

Ambiente verificado: Microsoft OpenJDK **17.0.11**, Windows e PowerShell.
É necessário ter um JDK instalado e os comandos java e javac no PATH.

Na raiz do projeto:

~~~powershell
javac -encoding UTF-8 -Xlint:all -d build Velha.java AnalistaSituacional.java jogador\*.java
java -cp build Velha
~~~

A pasta build/ recebe os binários. Os arquivos .class antigos não devem ser usados
como dependências nem copiados para a entrega dos fontes. O ZIP de distribuição
pode ser extraído em uma pasta nova para conferir uma compilação limpa.

Para compilar e executar os testes:

~~~powershell
javac -encoding UTF-8 -Xlint:all -d build Velha.java AnalistaSituacional.java jogador\*.java testes\*.java
java -ea -cp build TestesRegras
java -ea -cp build TestesEntrada
java -ea -cp build TestesEntrada falha
java -ea -cp build TestesTerminal
~~~

Os comandos Java devem terminar sem erros. TestesTerminal grava as conversas
em uma pasta exclusiva de cada execução: build/relatorios/terminal-*/terminal-*.txt.
O resumo alternancia.txt e a conversa da sessão longa registram todas as
12 partidas consecutivas, sem omissões. A primeira é sorteada; as outras
são conferidas deterministicamente a partir desse resultado.

## Como jogar

1. O programa sorteia quem começa a primeira partida.
2. Se o humano começar, ele escolhe X ou O. Se o computador começar, sua marca
   é sorteada e o humano recebe a outra.
3. Digite linha e coluna, uma por vez, usando **1, 2 ou 3**, como no desenho.
   As classes convertem essas coordenadas para os índices internos **0, 1 e 2**.
4. Entrada inválida ou casa ocupada provoca nova solicitação, sem perder a vez.
5. A partida termina imediatamente em vitória ou empate. Responda **S** para outra
   partida ou **N** para sair. Quem começa alterna a cada nova partida.

A escolha de marca aceita minúsculas após normalização, mas rejeita textos como
XO. A classe Marca recebe somente os caracteres maiúsculos validados.
O encerramento da entrada (EOF) durante uma jogada cancela a sessão, sem anunciar
vitória, derrota ou empate por esse motivo. EOF na pergunta de repetição encerra.
Um único leitor é compartilhado entre partidas para preservar entradas antecipadas.

## Estratégia do computador

As prioridades são:

1. **Ganhar:** duas marcas próprias e uma casa vazia.
2. **Bloquear:** duas marcas adversárias e uma casa vazia.
3. **Preparar:** uma marca própria e duas casas vazias.
4. **Ocupar uma casa livre:** percorrer linhas e colunas em ordem crescente.

Nas três primeiras etapas, a ordem é: linhas 0, 1 e 2; colunas 0, 1 e 2;
diagonal principal; diagonal secundária.

Na preparação, escolhe-se a primeira casa vazia da primeira sequência elegível.
As coordenadas da sequência são percorridas da esquerda para a direita ou de
cima para baixo; a diagonal secundária começa no canto superior direito.
Uma marca própria e duas casas vazias excluem a presença de marca adversária.

O algoritmo é uma heurística simples, sem Minimax: ele pode perder. Em um tabuleiro
cheio, o método de jogada automática não altera nenhuma posição.

## Conceitos de POO

- Encapsulamento da marca e da matriz por atributos privados.
- Construtores com validação, construtores de cópia e clone().
- Interface Jogador e chamada polimórfica de facaSuaJogada() em Velha.
- Sobrescrita de toString(), equals() e hashCode().
- Comparação de marcas pelo conteúdo e consistência entre igualdade e hash.
- Exceções para argumentos inválidos e falhas internas diagnosticáveis.

O construtor de cópia e o clone de Tabuleiro criam matriz e marcas independentes.
O analista é um observador: seu construtor principal, sua cópia e seu clone
acompanham o mesmo tabuleiro. Copiar um analista não cria uma fotografia
independente da partida; esse contrato está documentado no código.

## Verificações

Os testes usam os fontes reais, incluindo o Tabuleiro real:

- Todos os 65.536 valores de char para a validação de Marca.
- Tabuleiro vazio, índices inválidos, marca nula, casa ocupada, igualdade,
  hash e cópia profunda.
- Vencedor e fim de jogo nas 19.683 configurações de casas.
- Jogada automática em 39.366 cenários, para X e O, conferida por simulação
  independente; prioridades, preservação de casas e uma jogada por chamada.
- Vitória nas oito sequências, empate e vitória na última casa.
- Entradas inválidas, conversão de coordenadas, EOF e leitor entre partidas.
- Falhas de leitura e consulta/escrita injetadas para conferir suas causas.
- Processos reais de Velha: vitória humana, derrota, empate, entradas inválidas,
  três partidas consecutivas e uma sessão adicional de 12 partidas,
  reinício vazio, alternância de todos os turnos e iniciantes, S/N e EOF.

Os testes exaustivos de regras incluem estados artificiais e não afirmam que todos
eles sejam alcançáveis em uma partida. Os testes de terminal usam pipes para
simular digitação, sem trocar as classes do jogo nem o sorteio original. A busca
de roteiros no teste somente encontra sequências legais para exercitar os resultados.
O número de processos pode variar por causa do sorteio.

O programa integrado pode ser demonstrado com os mesmos comandos de execução,
sem executar os testes. Não são necessários frameworks, extensões da IDE ou rede.

A matriz completa dos requisitos e a comparação com o Velha.zip original estão
em ACEITACAO.md. A pasta aceitacao/ contém os resultados registrados e a conversa
completa das 12 partidas usadas na auditoria de aceitação.
